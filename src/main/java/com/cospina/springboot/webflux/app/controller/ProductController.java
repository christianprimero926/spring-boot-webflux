package com.cospina.springboot.webflux.app.controller;

import com.cospina.springboot.webflux.app.models.documents.Product;
import com.cospina.springboot.webflux.app.models.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@SessionAttributes("product")
@Controller
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService service;

    @GetMapping({"/show_all", "/"})
    public Mono<String> showAll(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCase();

        products.subscribe(prod -> log.info(prod.getName()));

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");

        return Mono.just("show_all");
    }

    @GetMapping("/form")
    public Mono<String> create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("title", "Formulario de Productos");
        model.addAttribute("button", "Crear");

        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> update(@PathVariable String id, Model model) {
        Mono<Product> productMono = service.findById(id).doOnNext(product -> {
            log.info("Producto: " + product.getName());
        }).defaultIfEmpty(new Product());

        model.addAttribute("button", "Editar");
        model.addAttribute("title", "Editar Producto");
        model.addAttribute("product", productMono);

        return Mono.just("form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> updateV2(@PathVariable String id, Model model) {
        return service.findById(id).doOnNext(product -> {
                    model.addAttribute("button", "Editar");
                    model.addAttribute("title", "Editar Producto");
                    model.addAttribute("product", product);
                    log.info("Producto: " + product.getName());
                }).defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("no Existe el prodcto"));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/show_all?error=no+existe+el+producto"));
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Errores en el formulario Producto");
            model.addAttribute("button", "Guardar");
            return Mono.just("form");
        } else {
            status.setComplete();
            if (product.getCreateAt() == null) {
                product.setCreateAt(new Date());
            }
            return service.save(product).doOnNext(prod -> {
                log.info("Producto almacenado: " + prod.getName() + " Id: " + prod.getId());
            }).thenReturn("redirect:/show_all?success=producto+guardado+con+exito");
        }

    }

    @GetMapping("/delete/{id}")
    public Mono<String> delete(@PathVariable String id) {
        return service.findById(id)
                .defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("No existe el prodcto"));
                    }
                    return Mono.just(p);
                })
//                Se puede simplificar de esta manera
//                .flatMap(service::delete)
                .flatMap(p -> {
                    log.info("Eliminando producto: " + p.getId() + "-" + p.getName());
                    return service.delete(p);
                })
                .then(Mono.just("redirect:/show_all?success=producto+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/show_all?error=no+existe+el+producto"));

    }

    @GetMapping("/show_all-datadriver")
    public String showAllDataDriver(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCase().delayElements(Duration.ofSeconds(1));

        products.subscribe(prod -> log.info(prod.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 1));
        model.addAttribute("title", "Listado de productos");

        return "show_all";
    }

    @GetMapping("/show_all-full")
    public String showAllFull(Model model) {

        Flux<Product> products = service.findAllWithNameUpperCaseRepeat();

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");

        return "show_all";
    }

    @GetMapping("/show_all-chunked")
    public String showAllChunked(Model model) {

        Flux<Product> products = service.findAllWithNameUpperCaseRepeat();

        model.addAttribute("products", products);
        model.addAttribute("title", "Listado de productos");

        return "show_all-chunked";
    }

}
