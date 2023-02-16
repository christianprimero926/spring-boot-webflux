package com.cospina.springboot.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cospina.springboot.webflux.app.models.dao.ProductDao;
import com.cospina.springboot.webflux.app.models.documents.Product;

import reactor.core.publisher.Flux;

@Controller
public class ProductController {

	@Autowired
	private ProductDao dao;

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@GetMapping({ "/show_all", "/" })
	public String showAll(Model model) {
		Flux<Product> products = dao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});

		products.subscribe(prod -> log.info(prod.getName()));

		model.addAttribute("products", products);
		model.addAttribute("tittle", "Listado de productos");

		return "show_all";
	}

}
