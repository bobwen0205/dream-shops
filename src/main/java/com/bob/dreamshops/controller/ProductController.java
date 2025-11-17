package com.bob.dreamshops.controller;

import com.bob.dreamshops.dto.ProductDto;
import com.bob.dreamshops.exceptions.AlreadyExistsException;
import com.bob.dreamshops.exceptions.ResourceNotFoundException;
import com.bob.dreamshops.model.Product;
import com.bob.dreamshops.request.AddProductRequest;
import com.bob.dreamshops.request.UpdateProductRequest;
import com.bob.dreamshops.response.ApiResponse;
import com.bob.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", productDtos));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request) {
        try {
            Product theProduct = productService.addProduct(request);
            ProductDto productDto = productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("Add product success", productDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity
                    .status(CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody UpdateProductRequest request, @PathVariable Long productId) {
        try {
            Product theProduct = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("Update product success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Delete product success", productId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category
    ) {
        try {
            List<Product> products = productService.searchProducts(brand, name, category);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity
                        .status(NOT_FOUND)
                        .body(new ApiResponse("Product not found", productDtos));
            }

            return ResponseEntity.ok(new ApiResponse("success", productDtos));

        } catch (Exception e) {
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam("brand") String brand, @RequestParam("name") String name) {
//        try {
//            List<Product> products = productService.getProductsByBrandAndName(brand, name);
//            if (products.isEmpty()) {
//                return ResponseEntity
//                        .status(NOT_FOUND)
//                        .body(new ApiResponse("Product not found", products));
//            }
//            return ResponseEntity.ok(new ApiResponse("success", products));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam("brand") String brand, @RequestParam("category") String category) {
//        try {
//            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
//            if (products.isEmpty()) {
//                return ResponseEntity
//                        .status(NOT_FOUND)
//                        .body(new ApiResponse("Product not found", products));
//            }
//            return ResponseEntity.ok(new ApiResponse("success", products));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse> getProductByName(@RequestParam("name") String name) {
//        try {
//            List<Product> products = productService.getProductsByName(name);
//            if (products.isEmpty()) {
//                return ResponseEntity
//                        .status(NOT_FOUND)
//                        .body(new ApiResponse("Product not found", products));
//            }
//            return ResponseEntity.ok(new ApiResponse("success", products));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse> getProductByBrand(@RequestParam("brand") String brand) {
//        try {
//            List<Product> products = productService.getProductByBrand(brand);
//            if (products.isEmpty()) {
//                return ResponseEntity
//                        .status(NOT_FOUND)
//                        .body(new ApiResponse("Product not found", products));
//            }
//            return ResponseEntity.ok(new ApiResponse("success", products));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<ApiResponse> getProductByCategory(@RequestParam("category") String category) {
//        try {
//            List<Product> products = productService.getProductsByCategory(category);
//            if (products.isEmpty()) {
//                return ResponseEntity
//                        .status(NOT_FOUND)
//                        .body(new ApiResponse("Product not found", products));
//            }
//            return ResponseEntity.ok(new ApiResponse("success", products));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }

    @GetMapping("/product/count")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam("brand") String brand, @RequestParam("name") String name) {
        try {
            Long productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product count!", productCount));
        } catch (Exception e) {
            return ResponseEntity
                    .status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
