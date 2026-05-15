/**
 * Container Primitive
 * Reusable container component for consistent layout
 * Uses centralized container classes from global.css
 */

import { forwardRef } from 'react';

const Container = forwardRef(({ className = '', children, size = 'xl', ...props }, ref) => {
  const containerSizes = {
    sm: 'container-sm',
    md: 'container-md',
    lg: 'container-lg',
    xl: 'container-xl',
    '2xl': 'container-2xl',
    full: 'container',
    edge: 'container-edge-safe',
  };

  return (
    <div
      ref={ref}
      className={`${containerSizes[size] || containerSizes.xl} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
});

Container.displayName = 'Container';

export default Container;
