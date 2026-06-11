import { useState, useRef, useEffect } from 'react';
import PropTypes from 'prop-types';

/**
 * LazyImage Component
 * Implements lazy loading for images with loading states and error handling
 * Improves initial page load performance by deferring off-screen image loading
 */
export default function LazyImage({
  src,
  alt,
  className = '',
  placeholder = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300"%3E%3Crect fill="%23e5e7eb" width="400" height="300"/%3E%3C/svg%3E',
  threshold = 0.1,
  onLoad,
  onError,
  ...props
}) {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isError, setIsError] = useState(false);
  const [isInView, setIsInView] = useState(false);
  const imgRef = useRef(null);
  const observerRef = useRef(null);

  useEffect(() => {
    // Set up Intersection Observer for lazy loading
    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setIsInView(true);
            observerRef.current.disconnect();
          }
        });
      },
      { threshold }
    );

    if (imgRef.current) {
      observerRef.current.observe(imgRef.current);
    }

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [threshold]);

  const handleLoad = (e) => {
    setIsLoaded(true);
    if (onLoad) onLoad(e);
  };

  const handleError = (e) => {
    setIsError(true);
    if (onError) onError(e);
  };

  return (
    <div ref={imgRef} className={`relative overflow-hidden ${className}`} {...props}>
      {/* Placeholder while loading */}
      {!isLoaded && (
        <img
          src={placeholder}
          alt={alt}
          className="absolute inset-0 w-full h-full object-cover blur-sm"
          aria-hidden="true"
        />
      )}

      {/* Actual image */}
      <img
        src={isInView ? src : undefined}
        alt={alt}
        className={`w-full h-full object-cover transition-opacity duration-300 ${
          isLoaded ? 'opacity-100' : 'opacity-0'
        }`}
        onLoad={handleLoad}
        onError={handleError}
        loading="lazy"
        decoding="async"
      />

      {/* Error state */}
      {isError && (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-100">
          <div className="text-center p-4">
            <svg
              className="w-12 h-12 mx-auto text-gray-400 mb-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            <p className="text-sm text-gray-500">Failed to load image</p>
          </div>
        </div>
      )}

      {/* Loading spinner */}
      {!isLoaded && !isError && (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-50">
          <div className="w-8 h-8 border-2 border-gray-200 border-t-gray-600 rounded-full animate-spin" />
        </div>
      )}
    </div>
  );
}

LazyImage.propTypes = {
  src: PropTypes.string.isRequired,
  alt: PropTypes.string.isRequired,
  className: PropTypes.string,
  placeholder: PropTypes.string,
  threshold: PropTypes.number,
  onLoad: PropTypes.func,
  onError: PropTypes.func,
};
