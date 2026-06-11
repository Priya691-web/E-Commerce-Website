package com.fashionstore.dao.interfaces;

import com.fashionstore.model.Product;
import java.util.List;

public interface ProductDAO {
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(int categoryId);
    Product getProductById(int id);
}
