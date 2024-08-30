package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long id;

    @NotNull
    private Long userOrderId;
    @PastOrPresent
    private LocalDate purchaseDate;
    @PositiveOrZero
    private double totalOrderPrice;
    @NotEmpty
    private List<ProductShortDto> products = new ArrayList<>();
}
