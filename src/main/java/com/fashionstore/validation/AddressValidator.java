package com.fashionstore.validation;

import com.fashionstore.model.Address;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Server-side validator for Address forms.
 * Returns field-level error messages instead of a single boolean.
 */
public final class AddressValidator {

    private static final Pattern PHONE_IN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PHONE_GENERIC = Pattern.compile("^[+]?\\d{7,15}$");
    private static final Pattern PIN_IN = Pattern.compile("^\\d{6}$");
    private static final Pattern POSTAL_GENERIC = Pattern.compile("^[A-Za-z0-9 -]{3,12}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]{2,100}$");

    private static final Set<String> ALLOWED_TYPES = Set.of("billing", "shipping", "both");

    private AddressValidator() {}

    /**
     * Validate an Address. Returns a map of fieldName -> error message.
     * An empty map means the address is valid.
     */
    public static Map<String, String> validate(Address a) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (a == null) {
            errors.put("_global", "Address is required");
            return errors;
        }

        // address type
        if (isBlank(a.getAddressType())) {
            errors.put("addressType", "Address type is required");
        } else if (!ALLOWED_TYPES.contains(a.getAddressType())) {
            errors.put("addressType", "Address type must be billing, shipping, or both");
        }

        // full name
        String fullName = trim(a.getFullName());
        if (isBlank(fullName)) {
            errors.put("fullName", "Full name is required");
        } else if (fullName.length() < 2 || fullName.length() > 100) {
            errors.put("fullName", "Full name must be between 2 and 100 characters");
        } else if (!NAME_PATTERN.matcher(fullName).matches()) {
            errors.put("fullName", "Full name contains invalid characters");
        }

        // phone (country aware)
        String phone = trim(a.getPhone());
        String country = trim(a.getCountry());
        if (isBlank(phone)) {
            errors.put("phone", "Phone number is required");
        } else if ("India".equalsIgnoreCase(country)) {
            if (!PHONE_IN.matcher(phone).matches()) {
                errors.put("phone", "Enter a valid 10-digit Indian mobile number");
            }
        } else if (!PHONE_GENERIC.matcher(phone).matches()) {
            errors.put("phone", "Enter a valid phone number (7-15 digits)");
        }

        // address line 1
        String line1 = trim(a.getAddressLine1());
        if (isBlank(line1)) {
            errors.put("addressLine1", "Address line 1 is required");
        } else if (line1.length() > 255) {
            errors.put("addressLine1", "Address line 1 must be 255 characters or less");
        }

        // address line 2 (optional)
        if (a.getAddressLine2() != null && a.getAddressLine2().length() > 255) {
            errors.put("addressLine2", "Address line 2 must be 255 characters or less");
        }

        // city
        String city = trim(a.getCity());
        if (isBlank(city)) {
            errors.put("city", "City is required");
        } else if (city.length() > 100) {
            errors.put("city", "City must be 100 characters or less");
        }

        // state
        String state = trim(a.getState());
        if (isBlank(state)) {
            errors.put("state", "State is required");
        } else if (state.length() > 100) {
            errors.put("state", "State must be 100 characters or less");
        }

        // postal code (country aware)
        String postal = trim(a.getPostalCode());
        if (isBlank(postal)) {
            errors.put("postalCode", "Postal code is required");
        } else if ("India".equalsIgnoreCase(country)) {
            if (!PIN_IN.matcher(postal).matches()) {
                errors.put("postalCode", "Enter a valid 6-digit Indian PIN code");
            }
        } else if (!POSTAL_GENERIC.matcher(postal).matches()) {
            errors.put("postalCode", "Enter a valid postal code");
        }

        // country
        if (isBlank(country)) {
            errors.put("country", "Country is required");
        } else if (country.length() > 100) {
            errors.put("country", "Country must be 100 characters or less");
        }

        return errors;
    }

    public static boolean isValid(Address a) {
        return validate(a).isEmpty();
    }

    /**
     * Sanitize an Address in place: trims fields, normalizes types.
     */
    public static void sanitize(Address a) {
        if (a == null) return;
        a.setFullName(trimSafe(a.getFullName()));
        a.setPhone(trimSafe(a.getPhone()));
        a.setAddressLine1(trimSafe(a.getAddressLine1()));
        a.setAddressLine2(trimSafe(a.getAddressLine2()));
        a.setCity(trimSafe(a.getCity()));
        a.setState(trimSafe(a.getState()));
        a.setPostalCode(trimSafe(a.getPostalCode()));
        a.setCountry(trimSafe(a.getCountry()));
        if (a.getAddressType() != null) {
            a.setAddressType(a.getAddressType().toLowerCase().trim());
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String trimSafe(String s) {
        return s == null ? null : s.trim();
    }
}
