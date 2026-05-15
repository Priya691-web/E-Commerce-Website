package com.fashionstore.service;

import com.fashionstore.model.Product;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.daoimpl.ProductDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1);
        product.setProductName("Test Product");
        product.setPrice(99.99);
        product.setStockQuantity(10);
    }

    @Test
    void testGetProductById_Success() {
        when(productDAO.getProductById(1)).thenReturn(product);

        Product result = productService.getProductById(1);

        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        verify(productDAO, times(1)).getProductById(1);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productDAO.getProductById(999)).thenReturn(null);

        Product result = productService.getProductById(999);

        assertNull(result);
        verify(productDAO, times(1)).getProductById(999);
    }

    @Test
    void testGetAllProducts_Success() {
        List<Product> products = Arrays.asList(product);
        when(productDAO.getAllProducts()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productDAO, times(1)).getAllProducts();
    }

    @Test
    void testSearchProducts_Success() {
        List<Product> products = Arrays.asList(product);
        when(productDAO.searchProducts("test")).thenReturn(products);

        List<Product> result = productService.searchProducts("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productDAO, times(1)).searchProducts("test");
    }

    @Test
    void testUpdateStock_Success() {
        when(productDAO.updateStock(1, 5)).thenReturn(true);

        boolean result = productService.updateStock(1, 5);

        assertTrue(result);
        verify(productDAO, times(1)).updateStock(1, 5);
    }
}
