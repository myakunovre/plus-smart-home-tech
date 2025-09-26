package interaction.client;

import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreFeignClient {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto dto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto dto);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestParam UUID productId,
                                    @RequestParam String quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
