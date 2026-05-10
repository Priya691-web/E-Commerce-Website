package com.fashionstore.cache;

import java.util.concurrent.TimeUnit;

public class CacheTTL {
    // Short TTL for frequently changing data
    public static final long SHORT = TimeUnit.MINUTES.toMinutes(5);
    
    // Medium TTL for moderately changing data
    public static final long MEDIUM = TimeUnit.HOURS.toHours(1);
    
    // Long TTL for rarely changing data
    public static final long LONG = TimeUnit.HOURS.toHours(6);
    
    // Very long TTL for static data
    public static final long VERY_LONG = TimeUnit.HOURS.toHours(24);
    
    // Extended TTL for very static data
    public static final long EXTENDED = TimeUnit.DAYS.toDays(7);
    
    // Instant TTL for session data
    public static final long INSTANT = TimeUnit.MINUTES.toMinutes(1);
    
    private CacheTTL() {}
}
