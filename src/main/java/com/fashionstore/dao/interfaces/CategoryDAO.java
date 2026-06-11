package com.fashionstore.dao.interfaces;

import com.fashionstore.model.Category;
import java.util.List;

public interface CategoryDAO {
    List<Category> getAllCategories();
    Category getCategoryById(int id);
}
