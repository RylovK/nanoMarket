package my.project.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.BrandDTO;
import my.project.productservice.exception.ValidationErrorException;
import my.project.productservice.service.BrandService;
import my.project.productservice.service.FileUploadService;
import my.project.productservice.validator.BrandValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Validated
@Tag(name = "Brand API", description = "API for brand management")
public class BrandController {

    private final BrandService brandService;
    private final BrandValidator brandValidator;
    private final FileUploadService fileUploadService;

    @GetMapping
    @Operation(summary = "Get all brands with pagination",
            description = "Retrieve a paginated list of brands. Optionally filter by brand name")
    public ResponseEntity<Page<BrandDTO>> getAllBrandsWithPagination(
            @RequestParam(required = false)
            @Parameter(description = "Filter brands by name", example = "Apple") String brandName,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number", example = "0") Integer page,

            @RequestParam(defaultValue = "25")
            @Parameter(description = "Page size", example = "25") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandDTO> allBrands = brandService.getAllBrands(brandName, pageable);
        return ResponseEntity.ok(allBrands);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a brand by ID",
            description = "Retrieve details of a brand by its unique ID")
    public ResponseEntity<BrandDTO> getBrand(@PathVariable @Min(1) Long id) {
        BrandDTO brandById = brandService.getBrandById(id);
        return ResponseEntity.ok(brandById);
    }

    @PostMapping
    @Operation(summary = "Create a new brand",
            description = "Create a new brand with the provided information")
    public ResponseEntity<BrandDTO> createBrand(
            @RequestBody @Valid
            @Parameter(description = "Brand details to create", required = true) BrandDTO brand,

            BindingResult bindingResult) {
        brandValidator.validate(brand, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        BrandDTO created = brandService.createBrand(brand);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Upload an image for a brand",
            description = "Upload a brand image and associate it with the given brand ID")
    public ResponseEntity<BrandDTO> uploadBrandImage(
            @PathVariable Long id,

            @RequestParam("file")
            @Parameter(description = "Image file to upload", required = true) MultipartFile file) {
        String url = fileUploadService.uploadFile(file, "brand-images");
        BrandDTO updated = brandService.uploadImage(id, url);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing brand",
            description = "Update the details of an existing brand")
    public ResponseEntity<BrandDTO> updateBrand(
            @PathVariable Long id,

            @RequestBody @Valid
            @Parameter(description = "Updated brand details", required = true) BrandDTO brand,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        BrandDTO updated = brandService.updateBrand(id, brand);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a brand by ID",
            description = "Delete the brand with the specified ID")
    public ResponseEntity<BrandDTO> deleteBrand(@PathVariable Long id) {
        if (brandService.deleteBrand(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
