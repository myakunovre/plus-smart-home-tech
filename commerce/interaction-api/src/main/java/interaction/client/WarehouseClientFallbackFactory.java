package interaction.client;

import interaction.model.cart.ShoppingCartDto;
import interaction.model.warehouse.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseClientFallbackFactory implements WarehouseFeignClient {

    @Override
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public BookedProductDto checkAvailability(ShoppingCartDto cart) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void acceptReturn(Map<UUID, Long> productsToReturn) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }
}