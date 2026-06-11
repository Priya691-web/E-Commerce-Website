package com.fashionstore.dao.interfaces;

import com.fashionstore.model.OrderItem;
import java.util.List;

public interface OrderItemDAO {
    boolean addOrderItems(List<OrderItem> items);
    List<OrderItem> getItemsByOrderId(int orderId);
}
