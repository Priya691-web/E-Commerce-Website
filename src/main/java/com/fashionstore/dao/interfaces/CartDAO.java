package com.fashionstore.dao.interfaces;

import com.fashionstore.model.Cart;
import com.fashionstore.model.CartItem;
import java.util.List;

public interface CartDAO {
    Cart getCartByUserId(int userId);
    boolean createCart(int userId);
    boolean addItemToCart(int cartId, int variantId, int quantity);
    List<CartItem> getCartItems(int cartId);
    boolean removeItemFromCart(int cartItemId);
    boolean clearCart(int cartId);
}
