package my.project.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.exception.ValidationErrorException;
import my.project.productservice.service.FileUploadService;
import my.project.productservice.service.ProductService;
import my.project.productservice.validator.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "API for product management")
public class ProductController {

    private final ProductService productService;
    private final ProductValidator productValidator;
    private final FileUploadService fileUploadService;

    @GetMapping
    @Operation(
            summary = "Get all products with filters and pagination",
            description = """
                    Retrieve a paginated list of products with optional filters.
                    Filters include name (partial match, case-insensitive), categoryId (exact match), and brandId (exact match)
                    """)
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(required = false)
            @Parameter(description = """
                    Map of filters to apply. Available keys:
                    - **name**: Filter by product name (partial match, case insensitive).
                    - **categoryId**: Filter by category ID (exact match).
                    - **brandId**: Filter by brand ID (exact match).
                    """
            ) Map<String, String> filters,
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number", example = "0") Integer page,
            @RequestParam(defaultValue = "25")
            @Parameter(description = "Number of items per page", example = "25") Integer size,
            @RequestParam(defaultValue = "id,asc")
            @Parameter(description = "Sorting criteria in the format: property,direction. Default is 'id,asc'.", example = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Sort sortBy = Sort.by(direction, sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<ProductDTO> allProducts = productService.getAllProducts(filters, pageable);

        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID",
            description = "Retrieve details of a product by its unique ID")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable
            @Parameter(description = "ID of the product to retrieve", required = true) Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Create a new product with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details to create",
                    required = true,
                    content = @Content(mediaType = "application/json")
            ))
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
    @Operation(summary = "Update an existing product",
            description = "Update the details of an existing product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(mediaType = "application/json")
            ))
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable
            @Parameter(description = "ID of the product to update", required = true) Long id,

            @RequestBody @Valid ProductDTO productDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID",
            description = "Delete the product with the specified ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}")
    @Operation(summary = "Upload an image for a product",
            description = "Upload a product image and associate it with the given product ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Image file to upload",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            ))
    public ResponseEntity<ProductDTO> uploadProductImage(
            @PathVariable
            @Parameter(description = "ID of the product to associate the image with", required = true) Long id,

            @RequestBody MultipartFile file) {
        String url = fileUploadService.uploadFile(file, "product-images");
        ProductDTO updated = productService.uploadImage(id, url);
        return ResponseEntity.ok(updated);
    }
}
