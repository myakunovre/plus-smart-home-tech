package interaction.client;

import interaction.model.cart.ChangeProductQuantityRequest;
import interaction.model.cart.ShoppingCartDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartFeignClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@Valid @NotEmpty @RequestParam String userName);

    @PutMapping
    ShoppingCartDto addProduct(@Valid @NotEmpty @RequestParam String username,
                               @RequestBody Map<UUID, Long> productsToAdd);

    @DeleteMapping
    void deactivateShoppingCart(@Valid @NotEmpty @RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProducts(@Valid @NotEmpty String username,
                                   @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@Valid @NotEmpty String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}
