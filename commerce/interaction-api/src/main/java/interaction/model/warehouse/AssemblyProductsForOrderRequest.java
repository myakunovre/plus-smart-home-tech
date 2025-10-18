package interaction.model.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {

    @NotEmpty(message = "Products map cannot be empty")
    private Map<UUID, Long> products;

    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;
}