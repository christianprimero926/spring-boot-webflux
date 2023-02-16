package com.cospina.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cospina.springboot.webflux.app.models.documents.Product;

public interface ProductDao extends ReactiveMongoRepository<Product, String>{

}
