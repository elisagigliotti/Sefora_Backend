package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AccountDto {
    private Long id;
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstname;
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastname;
    @NotEmpty
    private String email;
    @NotNull
    private Role role;
    @Size(min = 9, max = 10, message = "Phone number must be 8 characters")
    private String phone;
    @NotNull
    private Boolean banned;
    @NotEmpty
    private String profileImage;
    private Long cartId;
    private List<WishlistDto> wishlistsDto;
    private List<PurchaseDto> purchaseDto;
    private List<ProductDto> productsDto;

}
