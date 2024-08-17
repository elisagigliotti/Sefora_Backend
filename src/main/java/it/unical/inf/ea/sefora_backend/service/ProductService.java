package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto save(ProductDto request);

    ProductDto getProductById(Long id);

    List<ProductDto> getAllProducts();

    void deleteProduct(Long id);

    List<ProductDto> getAllProductsByOwner(Long id);

}
