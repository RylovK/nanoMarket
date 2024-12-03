package my.project.productservice.controller;

import jakarta.validation.Valid;
import my.project.productservice.dto.BrandDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("/api/v1/product/brand")
public class BrandController {

    @GetMapping
    public ResponseEntity<Page<BrandDTO>> getAllBrands(@RequestParam(required = false, value = "25") int size,
                                                       @RequestParam(required = false, value = "0") int page,
                                                       @RequestParam(required = false) String brandName) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrand(@PathVariable long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody @Valid BrandDTO brand,
                                                BindingResult bindingResult) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable long id,
                                                @RequestBody @Valid BrandDTO brand) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BrandDTO> deleteBrand(@PathVariable long id) {
        return null;
    }
}
