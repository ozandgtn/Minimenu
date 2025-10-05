package com.minimenu.app.controller;

import com.minimenu.app.entity.Product;
import com.minimenu.app.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<Product>> list(@RequestParam(required = false) Long categoryId) {
        if (categoryId == null) {
            return ResponseEntity.ok(service.findAll());
        }
        return ResponseEntity.ok(service.findByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product body) {
        body.setId(null); // dışarıdan id gelirse yok say
        Product created = service.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product body) {
        return service.findById(id)
                .map(existing -> {
                    existing.setName(body.getName());
                    existing.setDescription(body.getDescription());
                    existing.setPrice(body.getPrice());
                    existing.setImageUrl(body.getImageUrl());
                    if (body.getCategory() != null) {
                        existing.setCategory(body.getCategory());
                    }
                    return ResponseEntity.ok(service.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
