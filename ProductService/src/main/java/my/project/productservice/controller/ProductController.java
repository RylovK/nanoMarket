package my.project.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.ProductAvailabilityDTO;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.dto.ProductReservationRequest;
import my.project.productservice.exception.ValidationErrorException;
import my.project.productservice.service.ProductService;
import my.project.productservice.validator.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductValidator productValidator;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(@RequestParam Map<String, String> filters,
                                                       @RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "25") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> allProducts = productService.getAllProducts(filters, pageable);
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }



    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO productDTO,
                                                    BindingResult bindingResult) {
        productValidator.validate(productDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        ProductDTO productDTOSaved = productService.createProduct(productDTO);
        return ResponseEntity.ok(productDTOSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestBody @Valid ProductDTO productDTO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProductDTO> uploadProductImage(@PathVariable Long id,
                                                         @RequestBody MultipartFile file) {
        ProductDTO updated = productService.uploadImage(id, file);
        return ResponseEntity.ok(updated);
    }

}
