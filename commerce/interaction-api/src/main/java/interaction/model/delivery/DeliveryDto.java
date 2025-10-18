package interaction.model.delivery;

import interaction.model.warehouse.AddressDto;
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
public class DeliveryDto {

    @NotNull(message = "Идентификатор доставки не может быть null")
    private UUID deliveryId;

    @NotNull(message = "Адрес отправления не может быть null")
    private AddressDto fromAddress;

    @NotNull(message = "Адрес назначения не может быть null")
    private AddressDto toAddress;

    @NotNull(message = "Идентификатор заказа не может быть null")
    private UUID orderId;

    @NotNull(message = "Статус доставки не может быть null")
    private DeliveryState deliveryState;
}