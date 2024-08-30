package it.unical.inf.ea.sefora_backend.service;

import it.unical.inf.ea.sefora_backend.dao.ProductDao;
import it.unical.inf.ea.sefora_backend.dao.AccountDao;
import it.unical.inf.ea.sefora_backend.dto.ProductDto;
import it.unical.inf.ea.sefora_backend.entities.Product;
import it.unical.inf.ea.sefora_backend.entities.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private AccountDao accountDao;

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setUserProductId(product.getProductAccount().getId());
        productDto.setImageProduct(product.getImageProduct());
        productDto.setCategory(product.getCategory());
        return productDto;
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        Account account = accountDao.findById(productDto.getUserProductId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        product.setProductAccount(account);
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageProduct(productDto.getImageProduct());
        product.setCategory(productDto.getCategory());
        return product;
    }

    private void validateProductDto(ProductDto productDTO) {
        if (productDTO.getId() != null && productDao.findById(productDTO.getId()).isEmpty())
            throw new RuntimeException("Product not found!");

        if (productDTO.getName().isEmpty())
            throw new RuntimeException("Product name cannot be empty!");

        if (productDTO.getPrice() < 0)
            throw new RuntimeException("Product price cannot be negative!");

        if (accountDao.findById(productDTO.getUserProductId()).isEmpty())
            throw new RuntimeException("User not found!");
    }

    @Override
    public ProductDto save(ProductDto productDTO) {
        validateProductDto(productDTO);
        Product product = convertToEntity(productDTO);
        return convertToDto(productDao.save(product));
    }

    @Override
    public List<ProductDto> getAllProductsByOwner(Long id) {
        return productDao.findAllByProductAccount_Id(id)
                .stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> findProductsByCurrentUser(Principal currentUser) {
        var user = (Account) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return productDao.findAllByProductAccount_Id(user.getId())
                .stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
        return convertToDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productDao.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (productDao.findById(id).isEmpty())
            throw new RuntimeException("Product not found!");

        productDao.deleteById(id);
    }
}
