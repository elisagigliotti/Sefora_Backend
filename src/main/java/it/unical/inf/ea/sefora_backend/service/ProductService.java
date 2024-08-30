package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dto.ProductDto;

import java.security.Principal;
import java.util.List;

public interface ProductService {
    ProductDto getProductById(Long id);

    List<ProductDto> getAllProducts();

    void deleteProduct(Long id);

    ProductDto save(ProductDto productDTO);

    List<ProductDto> getAllProductsByOwner(Long id);

    List<ProductDto> findProductsByCurrentUser(Principal currentUser);

}
