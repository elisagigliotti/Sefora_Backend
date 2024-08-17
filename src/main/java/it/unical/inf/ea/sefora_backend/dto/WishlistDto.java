package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.WishlistType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class WishlistDto {
    private Long id;

    @NotNull
    private Long userWishlistId;

    @NotNull
    private List<WishlistProductDto> wishlistProducts;

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull
    private WishlistType type;

    private List<UserDto> sharedWithUsers;

    private String shareableLink;
}
