package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishlistProductDto {
    private Long id;

    @NotNull
    private Long wishlistId;

    @NotNull
    private Long productId;
}
