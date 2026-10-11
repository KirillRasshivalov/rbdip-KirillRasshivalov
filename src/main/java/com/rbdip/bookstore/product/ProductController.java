package com.rbdip.bookstore.product;

import com.rbdip.bookstore.dto.CreateProductRequest;
import com.rbdip.bookstore.dto.ProductCreatedResponse;
import com.rbdip.bookstore.dto.ProductView;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCreatedResponse createProduct(@RequestBody CreateProductRequest body) {
        Product saved = productRepository.save(new Product(body.name(), body.price(), body.description()));
        return new ProductCreatedResponse(saved.getId(), saved.getName());
    }

    @GetMapping("/products")
    public List<ProductView> listProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductView(p.getId(), p.getName(), p.getPrice().toString()))
                .toList();
    }
}
