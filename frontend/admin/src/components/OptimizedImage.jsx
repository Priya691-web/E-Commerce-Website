import { useState, useRef, useEffect } from 'react';

export default function OptimizedImage({
  src,
  alt,
  webpSrc,
  width,
  height,
  className = '',
  loading = 'lazy',
  placeholder = 'blur',
  ...props
}) {
  const [isLoaded, setIsLoaded] = useState(false);
  const [hasError, setHasError] = useState(false);
  const imgRef = useRef(null);

  useEffect(() => {
    const img = imgRef.current;
    if (img && img.complete) {
      setIsLoaded(true);
    }
  }, []);

  const handleLoad = () => {
    setIsLoaded(true);
  };

  const handleError = () => {
    setHasError(true);
  };

  const placeholderStyle = {
    backgroundColor: '#f3f4f6',
    backgroundImage: 'linear-gradient(90deg, #f3f4f6 25%, #e5e7eb 50%, #f3f4f6 75%)',
    backgroundSize: '200% 100%',
    animation: 'shimmer 1.5s infinite',
  };

  return (
    <div
      className={`relative overflow-hidden ${className}`}
      style={{ width: '100%', height: '100%' }}
      {...props}
    >
      {!isLoaded && !hasError && (
        <div
          className="absolute inset-0"
          style={placeholderStyle}
          aria-hidden="true"
        />
      )}
      
      {webpSrc ? (
        <picture>
          <source srcSet={webpSrc} type="image/webp" />
          <img
            ref={imgRef}
            src={src}
            alt={alt}
            width={width}
            height={height}
            loading={loading}
            onLoad={handleLoad}
            onError={handleError}
            className={`w-full h-full object-cover transition-opacity duration-300 ${
              isLoaded ? 'opacity-100' : 'opacity-0'
            }`}
            decoding="async"
            fetchPriority={loading === 'eager' ? 'high' : 'auto'}
          />
        </picture>
      ) : (
        <img
          ref={imgRef}
          src={src}
          alt={alt}
          width={width}
          height={height}
          loading={loading}
          onLoad={handleLoad}
          onError={handleError}
          className={`w-full h-full object-cover transition-opacity duration-300 ${
            isLoaded ? 'opacity-100' : 'opacity-0'
          }`}
          decoding="async"
          fetchPriority={loading === 'eager' ? 'high' : 'auto'}
        />
      )}

      {hasError && (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-100">
          <span className="text-gray-400 text-sm">Image not available</span>
        </div>
      )}

      <style jsx>{`
        @keyframes shimmer {
          0% {
            background-position: 200% 0;
          }
          100% {
            background-position: -200% 0;
          }
        }
      `}</style>
    </div>
  );
}
