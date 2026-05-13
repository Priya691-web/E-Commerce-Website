package com.fashionstore.mapper;

import com.fashionstore.dto.ProductDTO;
import com.fashionstore.dto.CategoryDTO;
import com.fashionstore.dto.ProductSizeDTO;
import com.fashionstore.dto.ReviewDTO;
import com.fashionstore.model.Product;
import com.fashionstore.model.Category;
import com.fashionstore.model.ProductSize;
import com.fashionstore.model.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Product Entity ↔ DTO conversions
 * Provides safe mapping without exposing internal details
 */
public class ProductMapper {

    /**
     * Convert Product Entity to ProductDTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(java.math.BigDecimal.valueOf(product.getPrice()));
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.isActive());
        dto.setBrand(product.getBrand());
        
        if (product.getCreatedAt() != null) {
            dto.setCreatedAt(product.getCreatedAt().toLocalDateTime());
        }
        
        // Map nested objects if present
        // if (product.getCategory() != null) {
        //     dto.setCategory(toCategoryDTO(product.getCategory()));
        // }
        
        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            List<ProductSizeDTO> sizeDTOs = product.getSizes().stream()
                    .map(this::toProductSizeDTO)
                    .collect(Collectors.toList());
            dto.setSizes(sizeDTOs);
        }
        
        // Calculate rating and review count if reviews are available
        // if (product.getReviews() != null && !product.getReviews().isEmpty()) {
        //     List<Review> approvedReviews = product.getReviews().stream()
        //             .filter(Review::getApproved)
        //             .collect(Collectors.toList());
        //     
        //     if (!approvedReviews.isEmpty()) {
        //         double averageRating = approvedReviews.stream()
        //                 .mapToInt(Review::getRating)
        //                 .average()
        //                 .orElse(0.0);
        //         dto.setAverageRating(averageRating);
        //         dto.setReviewCount(approvedReviews.size());
        //     }
        // }
        
        dto.setIsTrending(product.isTrending());

        return dto;
    }

    /**
     * Convert ProductDTO to Product Entity
     */
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setProductName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice().doubleValue());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(dto.getActive());
        product.setBrand(dto.getBrand());
        
        if (dto.getCreatedAt() != null) {
            product.setCreatedAt(java.sql.Timestamp.valueOf(dto.getCreatedAt()));
        }

        // Map nested objects if present
        // if (dto.getCategory() != null) {
        //     product.setCategory(toCategoryEntity(dto.getCategory()));
        // }
        
        if (dto.getSizes() != null && !dto.getSizes().isEmpty()) {
            List<ProductSize> sizes = dto.getSizes().stream()
                    .map(this::toProductSizeEntity)
                    .collect(Collectors.toList());
            product.setSizes(sizes);
        }
        
        product.setTrending(dto.getIsTrending());

        return product;
    }

    /**
     * Convert Category Entity to CategoryDTO
     */
    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        // dto.setDescription(category.getDescription());
        // dto.setImageUrl(category.getImageUrl());
        // dto.setActive(category.getActive());
        // dto.setParentId(category.getParentId());
        // dto.setSortOrder(category.getSortOrder());

        return dto;
    }

    /**
     * Convert CategoryDTO to Category Entity
     */
    public Category toCategoryEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setCategoryName(dto.getCategoryName());
        // category.setDescription(dto.getDescription());
        // category.setImageUrl(dto.getImageUrl());
        // category.setActive(dto.getActive());
        // category.setParentId(dto.getParentId());
        // category.setSortOrder(dto.getSortOrder());

        return category;
    }

    /**
     * Convert ProductSize Entity to ProductSizeDTO
     */
    public ProductSizeDTO toProductSizeDTO(ProductSize productSize) {
        if (productSize == null) {
            return null;
        }

        ProductSizeDTO dto = new ProductSizeDTO();
        // dto.setSizeId(productSize.getProductSizeId());
        dto.setProductId(productSize.getProductId());
        dto.setSizeLabel(productSize.getSizeLabel());
        dto.setStockQuantity(productSize.getStockQuantity());
        dto.setAvailable(productSize.isAvailable());
        // dto.setSku(productSize.getSku());
        // dto.setWeight(productSize.getWeight());
        // dto.setDimensions(productSize.getDimensions());

        return dto;
    }

    /**
     * Convert ProductSizeDTO to ProductSize Entity
     */
    public ProductSize toProductSizeEntity(ProductSizeDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductSize productSize = new ProductSize();
        // productSize.setProductSizeId(dto.getSizeId());
        productSize.setProductId(dto.getProductId());
        productSize.setSizeLabel(dto.getSizeLabel());
        productSize.setStockQuantity(dto.getStockQuantity());
        productSize.setAvailable(dto.isAvailable());
        // productSize.setSku(dto.getSku());
        // productSize.setWeight(dto.getWeight());
        // productSize.setDimensions(dto.getDimensions());

        return productSize;
    }

    /**
     * Convert Review Entity to ReviewDTO
     */
    public ReviewDTO toReviewDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setUserId(review.getUserId());
        dto.setProductId(review.getProductId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        // dto.setApproved(review.getApproved());
        
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().toLocalDateTime());
        }
        
        // if (review.getUpdatedAt() != null) {
        //     dto.setUpdatedAt(review.getUpdatedAt().toLocalDateTime());
        // }

        return dto;
    }

    /**
     * Convert ReviewDTO to Review Entity
     */
    public Review toReviewEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setReviewId(dto.getReviewId());
        review.setUserId(dto.getUserId());
        review.setProductId(dto.getProductId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        // review.setApproved(dto.getApproved());
        
        if (dto.getCreatedAt() != null) {
            review.setCreatedAt(java.sql.Timestamp.valueOf(dto.getCreatedAt()));
        }
        
        // if (dto.getUpdatedAt() != null) {
        //     review.setUpdatedAt(java.sql.Timestamp.valueOf(dto.getUpdatedAt()));
        // }

        return review;
    }

    /**
     * Convert list of Product Entities to list of ProductDTOs
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of ProductDTOs to list of Product Entities
     */
    public List<Product> toEntityList(List<ProductDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update Product Entity from ProductDTO (partial update)
     */
    public void updateEntityFromDTO(ProductDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }

        if (dto.getName() != null) {
            product.setProductName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice().doubleValue());
        }
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }
        if (dto.getBrand() != null) {
            product.setBrand(dto.getBrand());
        }
        if (dto.getIsTrending() != null) {
            product.setTrending(dto.getIsTrending());
        }

        // Update nested objects
        // if (dto.getCategory() != null) {
        //     if (product.getCategory() == null) {
        //         product.setCategory(toCategoryEntity(dto.getCategory()));
        //     } else {
        //         updateCategoryFromDTO(dto.getCategory(), product.getCategory());
        //     }
        // }
    }

    /**
     * Update Category Entity from CategoryDTO
     */
    public ProductDTO toPublicDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(java.math.BigDecimal.valueOf(product.getPrice()));
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.isActive());
        dto.setBrand(product.getBrand());
        
        // Include category and sizes for public display
        // if (product.getCategory() != null) {
        //     dto.setCategory(toCategoryDTO(product.getCategory()));
        // }
        
        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            List<ProductSizeDTO> sizeDTOs = product.getSizes().stream()
                    .map(this::toProductSizeDTO)
                    .collect(Collectors.toList());
            dto.setSizes(sizeDTOs);
        }
        
        // Include rating information
        // if (product.getReviews() != null && !product.getReviews().isEmpty()) {
        //     List<Review> approvedReviews = product.getReviews().stream()
        //             .filter(Review::getApproved)
        //             .collect(Collectors.toList());
        //     
        //     if (!approvedReviews.isEmpty()) {
        //         double averageRating = approvedReviews.stream()
        //                 .mapToInt(Review::getRating)
        //                 .average()
        //                 .orElse(0.0);
        //         dto.setAverageRating(averageRating);
        //         dto.setReviewCount(approvedReviews.size());
        //     }
        // }

        return dto;
    }

    /**
     * Create ProductDTO for admin display (includes more information)
     */
    public ProductDTO toAdminDTO(Product product) {
        ProductDTO dto = toDTO(product);
        // Admin can see more details like internal IDs, stock levels, etc.
        return dto;
    }

    /**
     * Create ProductDTO for search results (minimal information)
     */
    public ProductDTO toSearchResultDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getProductName());
        dto.setDescription(product.getDescription() != null && product.getDescription().length() > 200 
                ? product.getDescription().substring(0, 200) + "..." 
                : product.getDescription());
        dto.setPrice(java.math.BigDecimal.valueOf(product.getPrice()));
        dto.setImageUrl(product.getImageUrl());
        dto.setBrand(product.getBrand());
        
        // Include basic rating info
        // if (product.getReviews() != null && !product.getReviews().isEmpty()) {
        //     List<Review> approvedReviews = product.getReviews().stream()
        //             .filter(Review::getApproved)
        //             .collect(Collectors.toList());
        //     
        //     if (!approvedReviews.isEmpty()) {
        //         double averageRating = approvedReviews.stream()
        //                 .mapToInt(Review::getRating)
        //                 .average()
        //                 .orElse(0.0);
        //         dto.setAverageRating(averageRating);
        //         dto.setReviewCount(approvedReviews.size());
        //     }
        // }

        return dto;
    }
}
