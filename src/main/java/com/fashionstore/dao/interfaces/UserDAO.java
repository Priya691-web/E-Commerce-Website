package com.fashionstore.dao.interfaces;

import com.fashionstore.model.User;
import java.util.List;

public interface UserDAO {
    boolean createUser(User user);
    User getUserByEmail(String email);
    User getUserById(int id);
    boolean updateUser(User user);
}
