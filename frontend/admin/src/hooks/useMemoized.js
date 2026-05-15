import { useMemo, useCallback } from 'react';

/**
 * Memoized callback hook with dependencies
 * Prevents unnecessary function recreations
 */
export function useStableCallback(callback, deps) {
  return useCallback(callback, deps);
}

/**
 * Memoized value hook with dependencies
 * Prevents unnecessary recalculations
 */
export function useStableValue(factory, deps) {
  return useMemo(factory, deps);
}

/**
 * Memoized event handler
 * Prevents recreation on every render
 */
export function useEventHandler(handler, deps) {
  return useCallback(handler, deps);
}

/**
 * Memoized sort function
 * Prevents recreation when sorting data
 */
export function useSortFunction(sortFn, deps) {
  return useCallback(sortFn, deps);
}

/**
 * Memoized filter function
 * Prevents recreation when filtering data
 */
export function useFilterFunction(filterFn, deps) {
  return useCallback(filterFn, deps);
}
