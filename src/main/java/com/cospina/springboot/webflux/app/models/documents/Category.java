package com.cospina.springboot.webflux.app.models.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Data
@NoArgsConstructor
public class Category {
    @Id
    private String id;
    private String name;
    public Category(String name) {
        this.name = name;
    }
}
