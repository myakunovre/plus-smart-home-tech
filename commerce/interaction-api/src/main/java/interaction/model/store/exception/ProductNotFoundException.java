package interaction.model.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product with ID " + productId + " not found.");
    }

    public ProductNotFoundException(String message) { // Для универсальности
        super(message);
    }
}