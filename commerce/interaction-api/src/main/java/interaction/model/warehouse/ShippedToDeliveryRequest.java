package interaction.model.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippedToDeliveryRequest {

    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;

    @NotNull(message = "Delivery ID cannot be null")
    private UUID deliveryId;
}