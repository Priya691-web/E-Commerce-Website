# Dependency Cleanup Recommendations

## Maven Dependencies (pom.xml)

### Unused Dependencies
Review the following dependencies for potential removal:

**Jackson Dependencies:**
- Check if all Jackson modules are used (databind, annotations, core)
- Remove unused Jackson modules

**Logging Dependencies:**
- Multiple logging implementations may exist
- Consolidate to SLF4J + Logback (already used)
- Remove unused logging libraries

**Spring Dependencies:**
- Spring annotations are used without Spring dependency (BUG #11)
- Either add Spring dependencies or remove Spring annotations
- Current approach: Remove Spring annotations

**Testing Dependencies:**
- Check if all test libraries are used
- Remove unused test frameworks

### Recommendations

1. **Audit Jackson Usage:**
   - Search for `com.fasterxml.jackson` usage
   - Remove unused Jackson modules

2. **Consolidate Logging:**
   - Keep: SLF4J, Logback
   - Remove: Any other logging implementations

3. **Resolve Spring Dependency Issue:**
   - Option A: Add Spring Boot Starter dependencies
   - Option B: Remove all Spring annotations (current approach)
   - Recommendation: Add Spring dependencies if using Spring annotations, otherwise remove annotations

4. **Review Test Dependencies:**
   - Keep: JUnit 5, Mockito
   - Remove: Any unused test libraries

## JavaScript Dependencies (package.json)

### Admin Panel Dependencies
Review fashionstore-admin/package.json for unused dependencies:

**Common Unused Patterns:**
- Development dependencies not used in build
- Libraries with similar functionality (e.g., multiple UI libraries)
- Outdated packages

### Recommendations

1. **Run npm audit** to identify vulnerabilities
2. **Use npm-check-updates** to identify outdated packages
3. **Remove unused dependencies** using depcheck or similar tool

## Database Dependencies

### JDBC Driver
- MySQL Connector/J is required (used)
- No cleanup needed

### Redis Client
- Jedis or Lettuce for Redis caching
- Verify which is used and remove the other

## Cleanup Commands

### Maven
```bash
# Analyze dependencies
mvn dependency:analyze

# Find unused declared dependencies
mvn dependency:analyze-only

# Find used undeclared dependencies
mvn dependency:analyze
```

### npm
```bash
# Check for unused dependencies
npx depcheck

# Audit for vulnerabilities
npm audit

# Update dependencies
npm update
```

## Summary

**Immediate Actions:**
1. Remove identified dead code files (5 files)
2. Run Maven dependency analysis
3. Run npm dependency analysis (for admin panel)
4. Resolve Spring annotation dependency issue

**Medium Term:**
1. Consolidate logging dependencies
2. Remove unused Jackson modules
3. Clean up test dependencies

**Risk Assessment:**
- Dead code removal: Low risk (files not referenced)
- Dependency cleanup: Medium risk (requires verification)
