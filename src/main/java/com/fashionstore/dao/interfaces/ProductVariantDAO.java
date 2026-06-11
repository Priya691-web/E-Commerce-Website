package com.fashionstore.dao.interfaces;

import com.fashionstore.model.ProductVariant;
import java.util.List;

public interface ProductVariantDAO {
    List<ProductVariant> getVariantsByProductId(int productId);
    ProductVariant getVariantById(int id);
    boolean updateStock(int variantId, int newQuantity);
}
