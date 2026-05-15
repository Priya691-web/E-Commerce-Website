import { lazy, Suspense } from 'react';

export default function LazyComponent({ children, fallback = null }) {
  return (
    <Suspense
      fallback={
        fallback || (
          <div className="flex items-center justify-center p-8">
            <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
          </div>
        )
      }
    >
      {children}
    </Suspense>
  );
}

export function withLazyLoading(importFn, fallback = null) {
  const LazyComponent = lazy(importFn);
  return (props) => (
    <LazyComponent fallback={fallback}>
      <LazyComponent {...props} />
    </LazyComponent>
  );
}
