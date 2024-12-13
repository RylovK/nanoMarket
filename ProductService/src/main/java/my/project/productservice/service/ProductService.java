package my.project.productservice.service;

import jakarta.validation.Valid;
import my.project.productservice.dto.ProductDTO;
import my.project.productservice.dto.ProductReservationRequest;
import my.project.productservice.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing products.
 * Provides methods for retrieving, creating, updating, and deleting products.
 * It also supports filtering of products based on various criteria.
 */
public interface ProductService {

    /**
     * Retrieves a paginated list of products based on provided filters and pagination parameters.
     *
     * Filters can be applied to the following product fields:
     * <ul>
     *     <li><b>Product name</b> - filter products by their name or description.</li>
     *     <li><b>Category</b> - filter products by their category name or ID.</li>
     *     <li><b>Brand</b> - filter products by their brand name or ID.</li>
     * </ul>
     *
     * Pagination is supported through the {@link Pageable} parameter.
     *
     * @param filters a map containing the filter criteria, with key-value pairs where the key is the field name and
     *                the value is the filter value. Supported filters include:
     *                <ul>
     *                    <li>"name" - Product name or description for filtering.</li>
     *                    <li>"categoryId" - Category ID for filtering products by their category.</li>
     *                    <li>"brandId" - Brand ID for filtering products by their brand.</li>
     *                </ul>
     * @param pageable pagination information (page number, size, and sorting)
     * @return a {@link Page} containing {@link ProductDTO} objects matching the filters and pagination criteria
     */
    Page<ProductDTO> getAllProducts(Map<String, String> filters, Pageable pageable);

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product
     * @return a {@link ProductDTO} representing the product with the given ID
     * @throws ProductNotFoundException if the product with the given ID does not exist
     */
    ProductDTO getProductById(Long id);

    /**
     * Creates a new product.
     *
     * @param productDTO the product data transfer object containing the details of the product to be created
     * @return a {@link ProductDTO} representing the created product
     */
    ProductDTO createProduct(ProductDTO productDTO);

    /**
     * Updates an existing product.
     *
     * @param id the unique identifier of the product to be updated
     * @param productDTO the updated product data transfer object containing the new details of the product
     * @return a {@link ProductDTO} representing the updated product
     * @throws ProductNotFoundException if the product with the given ID does not exist
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * Deletes a product by its unique identifier.
     *
     * @param id the unique identifier of the product to be deleted
     * @throws ProductNotFoundException if the product with the given ID does not exist
     */
    boolean deleteProduct(Long id);

    /**
     * Method for check on stock quantity and reserve products
     *
     * @param reservationRequests list of products, containing product's id and quantity to be reserved
     */
    void reserveProducts(@Valid List<ProductReservationRequest> reservationRequests);

    ProductDTO uploadImage(Long id, MultipartFile file);
}
