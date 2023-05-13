package com.cospina.springboot.webflux.app;

import com.cospina.springboot.webflux.app.models.documents.Category;
import com.cospina.springboot.webflux.app.models.documents.Product;
import com.cospina.springboot.webflux.app.models.services.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
    @Autowired
    private ProductServiceImpl service;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mongoTemplate.dropCollection("products").subscribe();
        mongoTemplate.dropCollection("categories").subscribe();

        Category electronic = new Category("Electronico");
        Category sport = new Category("Deporte");
        Category computation = new Category("Computacion");
        Category furniture = new Category("Muebles");

        Flux.just(electronic, sport, computation, furniture)
                .flatMap(service::saveCategory)
                .doOnNext(c -> {
                    log.info("Categoria creada: " + c.getName() + ", Id: " + c.getId());
                }).thenMany(Flux.just(
                                new Product("TV Panasonic Pantalla LCD", 456.89, electronic),
                                new Product("Sony Camara HD Digital", 177.89, electronic),
                                new Product("Apple iPod", 46.89, electronic),
                                new Product("Sony Notebook", 846.89, computation),
                                new Product("Hewlett Packard Multifuncional", 200.89, computation),
                                new Product("Bianchi Bicicleta", 70.89, sport),
                                new Product("HP Notebook Omen 17", 2500.89, computation),
                                new Product("Mica Cómoda 5 Cajones", 150.89, furniture),
                                new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.89, electronic)
                        )
                        .flatMap(product -> {
                            product.setCreateAt(new Date());
                            return service.save(product);
                        }))
                .subscribe(product -> log.info("Insert: " + product.getId() + " " + product.getName()));
    }

}
