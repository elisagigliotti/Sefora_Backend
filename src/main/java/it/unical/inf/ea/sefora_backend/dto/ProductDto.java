package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductDto {
    private Long id;
    @NotEmpty
    private String name;
    @Size(min = 50, max = 1000, message = "Description must be between 50 and 1000 characters")
    private String description;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Float price;

    @NotNull
    private Category category;

    private String imageProduct;
    @NotNull
    private Long userProductId;
}

