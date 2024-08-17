package it.unical.inf.ea.sefora_backend.dto;

import it.unical.inf.ea.sefora_backend.entities.Address;
import it.unical.inf.ea.sefora_backend.entities.Role;
import it.unical.inf.ea.sefora_backend.utils.validation.ValidEmailAndPassword;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@ValidEmailAndPassword
public class UserDto {
    private Long id;
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstname;
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastname;
    private String email;
    private String password;
    @NotNull
    private Role role;
    @Size(min = 9, max = 10, message = "Phone number must be 8 characters")
    private String phone;
    @NotNull
    private Boolean banned;
    @NotEmpty
    private String profileImage;

    @NotNull
    private Long cartId;
    private List<WishlistDto> wishlistsDto;
    private List<OrderDto> ordersDto;
    @NotNull
    private Address address;
    private List<ProductDto> productsDto;

}
