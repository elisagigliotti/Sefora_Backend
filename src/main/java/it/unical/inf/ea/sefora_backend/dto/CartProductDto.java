package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductDto {
    private Long id;
    @NotNull
    private Long cartId;
    @NotNull
    private Long productId;
    @PositiveOrZero
    private Integer quantity;
}


