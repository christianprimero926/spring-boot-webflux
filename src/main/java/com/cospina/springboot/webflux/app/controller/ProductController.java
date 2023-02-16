package com.cospina.springboot.webflux.app.controller;

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
	
	@GetMapping({"/show_all", "/"})
	public String showAll(Model model) {		
		Flux<Product> products = dao.findAll();
		
		model.addAttribute("products", products);
		model.addAttribute("tittle", "Listado de productos");
		
		return "show_all";
	}

}
