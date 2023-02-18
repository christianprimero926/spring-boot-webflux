package com.cospina.springboot.webflux.app.models.services;

import com.cospina.springboot.webflux.app.models.dao.ProductDao;
import com.cospina.springboot.webflux.app.models.documents.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao dao;

    @Override
    public Flux<Product> findAll() {
        return dao.findAll();
    }

    @Override
    public Flux<Product> findAllWithNameUpperCase() {
        return dao.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Flux<Product> findAllWithNameUpperCaseRepeat() {
        return findAllWithNameUpperCase().repeat(5000);
    }

    @Override
    public Mono<Product> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return dao.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return dao.delete(product);
    }
}
