package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderProductDto {
    private Long id;

    @NotNull
    private Long orderId;
    @NotNull
    private Long productId;
    @Positive
    private Long quantity;
}
