package com.cospina.springboot.webflux.app.models.dao;

import com.cospina.springboot.webflux.app.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryDao extends ReactiveMongoRepository<Category, String> {
}
