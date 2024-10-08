package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CartDto {

    private Long id;
    @NotNull
    private Long userCartId;
    @NotEmpty
    private List<ProductShortDto> products;
}