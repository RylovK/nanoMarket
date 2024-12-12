package my.project.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.productservice.dto.BrandDTO;
import my.project.productservice.exception.ValidationErrorException;
import my.project.productservice.service.BrandService;
import my.project.productservice.validator.BrandValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final BrandValidator brandValidator;

    @GetMapping
    public ResponseEntity<Page<BrandDTO>> getAllBrands(@RequestParam(required = false) String brandName,
                                                       @RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "25") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandDTO> allBrands = brandService.getAllBrands(brandName, pageable);
        return ResponseEntity.ok(allBrands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrand(@PathVariable Long id) {
        BrandDTO brandById = brandService.getBrandById(id);
        return ResponseEntity.ok(brandById);
    }

    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody @Valid BrandDTO brand,
                                                BindingResult bindingResult) {
        brandValidator.validate(brand, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        BrandDTO created = brandService.createBrand(brand);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<BrandDTO> uploadBrandImage(@PathVariable Long id,
                                                     @RequestParam("file") MultipartFile file) {
        return null; //TODO
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id,
                                                @RequestBody @Valid BrandDTO brand,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        BrandDTO updated = brandService.updateBrand(id, brand);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BrandDTO> deleteBrand(@PathVariable Long id) {
        if (brandService.deleteBrand(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
