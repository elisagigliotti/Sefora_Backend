package it.unical.inf.ea.sefora_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductShortDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @Positive(message = "Price must be greater than or equal to 0")
    private Float price;
}
