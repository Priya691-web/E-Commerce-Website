package com.fashionstore.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Base Repository Interface
 * Provides standard CRUD operations for all repositories
 * Follows the Repository pattern for clean architecture
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseRepository<T, ID> {
    
    /**
     * Find entity by ID
     * @param id Primary key
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Find all entities with pagination
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of entities for the specified page
     */
    List<T> findAll(int page, int size);
    
    /**
     * Save entity (create or update)
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);
    
    /**
     * Save multiple entities
     * @param entities List of entities to save
     * @return List of saved entities
     */
    List<T> saveAll(List<T> entities);
    
    /**
     * Delete entity by ID
     * @param id Primary key
     */
    void deleteById(ID id);
    
    /**
     * Delete entity
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete multiple entities
     * @param entities List of entities to delete
     */
    void deleteAll(List<T> entities);
    
    /**
     * Count total number of entities
     * @return Total count
     */
    long count();
    
    /**
     * Check if entity exists by ID
     * @param id Primary key
     * @return True if entity exists
     */
    boolean existsById(ID id);
    
    /**
     * Find entity by ID or throw exception if not found
     * @param id Primary key
     * @return Entity
     * @throws RuntimeException if entity not found
     */
    default T findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException(
                    String.format("Entity with id %s not found", id)
                ));
    }
    
    /**
     * Save entity and flush immediately
     * @param entity Entity to save
     * @return Saved entity
     */
    T saveAndFlush(T entity);
    
    /**
     * Save entity and refresh
     * @param entity Entity to save
     * @return Refreshed entity
     */
    T saveAndRefresh(T entity);
}
