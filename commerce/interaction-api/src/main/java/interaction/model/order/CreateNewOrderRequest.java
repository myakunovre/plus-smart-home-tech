package interaction.model.order;

import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.AddressDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewOrderRequest {

    @NotNull
    ShoppingCartDto shoppingCartDto;

    @NotNull
    AddressDto addressDto;
}
