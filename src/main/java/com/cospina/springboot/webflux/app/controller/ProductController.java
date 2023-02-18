package com.cospina.springboot.webflux.app.controller;

import com.cospina.springboot.webflux.app.models.documents.Product;
import com.cospina.springboot.webflux.app.models.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SessionAttributes("product")
@Controller
public class ProductController {

    @Autowired
    private ProductService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

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

        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> update(@PathVariable String id, Model model){
        Mono<Product> productMono = service.findById(id).doOnNext(product -> {
            log.info("Producto: " + product.getName());
        }).defaultIfEmpty(new Product());

        model.addAttribute("title", "Editar Producto");
        model.addAttribute("product", productMono);

        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(Product product, SessionStatus status) {
        status.setComplete();
        return service.save(product).doOnNext(prod -> {
            log.info("Producto almacenado: " + prod.getName() + " Id: " + prod.getId());
        }).thenReturn("redirect:/show_all");
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
