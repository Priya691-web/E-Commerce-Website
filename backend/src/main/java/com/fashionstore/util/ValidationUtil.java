package com.fashionstore.util;

/**
 * Shared bounds and sanitization for servlet inputs. Prefer {@link com.fashionstore.validation.Validator}
 * for form field chains; use this class for query parameters and non-form payloads.
 */
public final class ValidationUtil {

    public static final int MAX_SEARCH_QUERY_LENGTH = 200;
    public static final int MAX_SEARCH_LIMIT = 50;
    public static final int MAX_FUZZ_DISTANCE = 4;
    public static final double MAX_FILTER_PRICE = 1_000_000d;

    /** Maximum units allowed per cart line (inventory-friendly cap). */
    public static final int MAX_PRODUCT_QUANTITY_PER_LINE = 99;

    public static final int MAX_COUPON_CODE_LENGTH = 40;

    private ValidationUtil() {}

    /**
     * Trims, strips ASCII control characters (except normal whitespace), and caps length for search queries.
     */
    public static String sanitizeSearchInput(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        int max = Math.min(trimmed.length(), MAX_SEARCH_QUERY_LENGTH);
        StringBuilder sb = new StringBuilder(max);
        for (int i = 0; i < trimmed.length() && sb.length() < MAX_SEARCH_QUERY_LENGTH; i++) {
            char c = trimmed.charAt(i);
            if (c == ' ' || c == '\t') {
                sb.append(c);
            } else if (c >= 32 && c != 127) {
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

    public static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clampSearchLimit(String raw, int defaultValue) {
        int v = parsePositiveInt(raw, defaultValue);
        return clampInt(v, 1, MAX_SEARCH_LIMIT);
    }

    public static int clampFuzzDistance(String raw, int defaultValue) {
        int v = parsePositiveInt(raw, defaultValue);
        return clampInt(v, 0, MAX_FUZZ_DISTANCE);
    }

    public static int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double clampPrice(String raw, double defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            double v = Double.parseDouble(raw.trim());
            if (v < 0 || Double.isNaN(v) || Double.isInfinite(v)) {
                return defaultValue;
            }
            return Math.min(v, MAX_FILTER_PRICE);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Coupon codes: trim, uppercase optional — keep alphanumerics + common separators only. */
    public static String normalizeCouponCode(String raw) {
        if (raw == null) {
            return "";
        }
        String t = raw.trim();
        if (t.length() > MAX_COUPON_CODE_LENGTH) {
            t = t.substring(0, MAX_COUPON_CODE_LENGTH);
        }
        return t;
    }

    /**
     * @return true if quantity is suitable for a cart line update
     */
    public static boolean isAllowedCartQuantity(int quantity) {
        return quantity >= 1 && quantity <= MAX_PRODUCT_QUANTITY_PER_LINE;
    }

    /** Safe filter tokens for advanced search (category, color, etc.). */
    public static String truncatePlaintext(String raw, int maxLen) {
        if (raw == null) {
            return null;
        }
        String s = sanitizeSearchInput(raw);
        if (s.isEmpty()) {
            return null;
        }
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }

    /** Allowed ordering keys for API advanced search (matches catalog UI). */
    public static String sanitizeAdvancedSearchSort(String raw) {
        if (raw == null || raw.isBlank()) {
            return "newest";
        }
        String v = raw.trim();
        return switch (v) {
            case "popular", "price_asc", "price_desc", "name_asc", "newest" -> v;
            default -> "newest";
        };
    }
}
