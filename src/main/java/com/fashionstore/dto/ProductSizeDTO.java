package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Product Size Data Transfer Object
 */
public class ProductSizeDTO {

    private Integer productSizeId;
    private Integer productId;
    private String sizeLabel;
    private Integer stockQuantity;
    private String skuCode;
    private Boolean available;

    public ProductSizeDTO() {}

    public ProductSizeDTO(Integer productSizeId, String sizeLabel, Integer stockQuantity) {
        this.productSizeId = productSizeId;
        this.sizeLabel = sizeLabel;
        this.stockQuantity = stockQuantity;
    }

    @JsonProperty("id")
    public Integer getProductSizeId() {
        return productSizeId;
    }

    public void setProductSizeId(Integer productSizeId) {
        this.productSizeId = productSizeId;
    }

    @JsonProperty("product_id")
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @JsonProperty("size_label")
    public String getSizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    @JsonProperty("stock_quantity")
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @JsonProperty("sku_code")
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    @JsonProperty("available")
    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
