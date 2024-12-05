package my.project.productservice.service;

import my.project.productservice.dto.BrandDTO;
import my.project.productservice.exception.BrandNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing brands.
 */
public interface BrandService {

    /**
     * Retrieves a paginated list of brands, with optional filtering by brand name.
     *
     * @param pageable pagination details (page number and size)
     * @param brandName optional name of the brand to filter by
     * @return a page of {@link BrandDTO} objects
     */
    Page<BrandDTO> getAllBrands(String brandName, Pageable pageable);

    /**
     * Retrieves a brand by its unique identifier.
     *
     * @param id the unique ID of the brand
     * @return the {@link BrandDTO} with the given ID
     * @throws BrandNotFoundException if the brand with the given ID is not found
     */
    BrandDTO getBrandById(long id) throws BrandNotFoundException;

    /**
     * Creates a new brand.
     *
     * @param brandDTO the {@link BrandDTO} containing brand details to be created
     * @return the created {@link BrandDTO}
     */
    BrandDTO createBrand(BrandDTO brandDTO);

    /**
     * Updates an existing brand.
     *
     * @param id the unique ID of the brand to be updated
     * @param brandDTO the {@link BrandDTO} containing updated brand details
     * @return the updated {@link BrandDTO}
     * @throws BrandNotFoundException if the brand to be updated does not exist
     */
    BrandDTO updateBrand(long id, BrandDTO brandDTO) throws BrandNotFoundException;

    /**
     * Deletes a brand by its unique identifier.
     *
     * @param id the unique ID of the brand to be deleted
     * @return true if the brand was successfully deleted, false if no brand with the given ID was found
     */
    boolean deleteBrand(long id);
}
