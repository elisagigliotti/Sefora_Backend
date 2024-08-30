package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.WishlistType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WishlistDto {
    private Long id;

    @NotNull
    private Long userWishlistId;

    @NotEmpty
    private List<ProductShortDto> products;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull
    private WishlistType type;

    private List<AccountShortDto> sharedWithUsers;
}
