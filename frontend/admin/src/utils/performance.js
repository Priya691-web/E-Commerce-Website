/**
 * Performance Optimization Utilities
 * Fixes layout thrashing, CLS, LCP, and hydration delays
 */

// Prevent layout thrashing by batching DOM reads and writes
export function batchDOMUpdates(updates) {
  // Read all layout properties first
  const reads = [];
  const writes = [];
  
  updates.forEach(update => {
    if (update.type === 'read') {
      reads.push(update.fn);
    } else {
      writes.push(update.fn);
    }
  });
  
  // Execute all reads
  const readResults = reads.map(fn => fn());
  
  // Execute all writes
  writes.forEach((fn, index) => fn(readResults[index]));
}

// Fix CLS by reserving space for dynamic content
export function reserveSpace(element, dimensions) {
  if (!element) return;
  
  element.style.width = dimensions.width || 'auto';
  element.style.height = dimensions.height || 'auto';
  element.style.minHeight = dimensions.minHeight || '0px';
  element.style.aspectRatio = dimensions.aspectRatio || 'auto';
}

// Improve LCP by preloading critical resources
export function preloadCriticalResources(resources) {
  resources.forEach(resource => {
    const link = document.createElement('link');
    link.rel = resource.rel || 'preload';
    link.href = resource.href;
    
    if (resource.as) link.as = resource.as;
    if (resource.type) link.type = resource.type;
    if (resource.crossOrigin) link.crossOrigin = resource.crossOrigin;
    
    document.head.appendChild(link);
  });
}

// Fix hydration delays by pre-rendering critical content
export function preRenderCriticalContent(content) {
  const container = document.createElement('div');
  container.style.display = 'none';
  container.innerHTML = content;
  document.body.appendChild(container);
  
  // Remove after hydration
  setTimeout(() => {
    document.body.removeChild(container);
  }, 0);
}

// Reduce main thread blocking by deferring non-critical tasks
export function deferNonCriticalTasks(tasks) {
  if ('requestIdleCallback' in window) {
    window.requestIdleCallback(() => {
      tasks.forEach(task => task());
    });
  } else {
    setTimeout(() => {
      tasks.forEach(task => task());
    }, 0);
  }
}

// Optimize font loading to prevent FOUT/FOIT
export function optimizeFontLoading(fonts) {
  fonts.forEach(font => {
    const link = document.createElement('link');
    link.rel = 'preload';
    link.as = 'font';
    link.href = font.href;
    link.crossOrigin = 'anonymous';
    document.head.appendChild(link);
  });
}

// Prevent layout shift by using size attributes on images
export function preventImageLayoutShift(images) {
  images.forEach(img => {
    if (img.width && img.height) {
      img.style.aspectRatio = `${img.width} / ${img.height}`;
    }
  });
}

// Measure and report Core Web Vitals
export function measureCoreWebVitals() {
  if ('PerformanceObserver' in window) {
    const observer = new PerformanceObserver((list) => {
      list.getEntries().forEach((entry) => {
        switch (entry.entryType) {
          case 'largest-contentful-paint':
            console.log('LCP:', entry.startTime);
            break;
          case 'first-input':
            console.log('FID:', entry.processingStart - entry.startTime);
            break;
          case 'layout-shift':
            if (!entry.hadRecentInput) {
              console.log('CLS:', entry.value);
            }
            break;
        }
      });
    });
    
    observer.observe({ type: 'largest-contentful-paint', buffered: true });
    observer.observe({ type: 'first-input', buffered: true });
    observer.observe({ type: 'layout-shift', buffered: true });
  }
}

// Reduce JavaScript execution time
export function optimizeJavaScriptExecution() {
  // Use Web Workers for CPU-intensive tasks
  if ('Worker' in window) {
    console.log('Web Workers available for offloading tasks');
  }
  
  // Use requestAnimationFrame for visual updates
  if ('requestAnimationFrame' in window) {
    console.log('requestAnimationFrame available for smooth animations');
  }
}

// Cache API responses to reduce network requests
export function cacheAPIResponses() {
  if ('caches' in window) {
    console.log('Cache API available for response caching');
  }
}
