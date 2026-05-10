package com.fashionstore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Single shared {@link Gson} for JSON API responses (search, cart AJAX, wishlist, reviews).
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    private JsonUtil() {}

    public static Gson gson() {
        return GSON;
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }
}
