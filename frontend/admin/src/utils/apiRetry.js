/**
 * API Retry Utility
 * Implements exponential backoff retry logic for failed API calls
 * Improves resilience against transient network failures
 */

class RetryError extends Error {
  constructor(message, originalError, retryCount) {
    super(message);
    this.name = 'RetryError';
    this.originalError = originalError;
    this.retryCount = retryCount;
  }
}

/**
 * Retry configuration
 */
const DEFAULT_RETRY_CONFIG = {
  maxRetries: 3,
  initialDelay: 1000, // 1 second
  maxDelay: 10000, // 10 seconds
  retryableStatusCodes: [408, 429, 500, 502, 503, 504],
  retryableErrors: ['ECONNRESET', 'ETIMEDOUT', 'ECONNREFUSED', 'ENOTFOUND'],
  backoffMultiplier: 2,
};

/**
 * Calculate delay with exponential backoff
 */
function calculateDelay(retryCount, config) {
  const delay = Math.min(
    config.initialDelay * Math.pow(config.backoffMultiplier, retryCount),
    config.maxDelay
  );
  // Add jitter to prevent thundering herd
  return delay + Math.random() * 100;
}

/**
 * Check if error is retryable
 */
function isRetryableError(error, config) {
  if (!error) return false;

  // Check network errors
  if (error.code && config.retryableErrors.includes(error.code)) {
    return true;
  }

  // Check HTTP status codes
  if (error.response && error.response.status) {
    return config.retryableStatusCodes.includes(error.response.status);
  }

  // Check for timeout errors
  if (error.message && error.message.includes('timeout')) {
    return true;
  }

  return false;
}

/**
 * Retry function with exponential backoff
 */
async function retry(fn, config = DEFAULT_RETRY_CONFIG) {
  const mergedConfig = { ...DEFAULT_RETRY_CONFIG, ...config };
  let lastError;

  for (let attempt = 0; attempt <= mergedConfig.maxRetries; attempt++) {
    try {
      const result = await fn();
      return result;
    } catch (error) {
      lastError = error;

      // Don't retry on the last attempt
      if (attempt === mergedConfig.maxRetries) {
        throw new RetryError(
          `Max retries (${mergedConfig.maxRetries}) exceeded`,
          error,
          attempt
        );
      }

      // Check if error is retryable
      if (!isRetryableError(error, mergedConfig)) {
        throw error;
      }

      // Calculate delay and wait
      const delay = calculateDelay(attempt, mergedConfig);
      console.warn(
        `API call failed (attempt ${attempt + 1}/${mergedConfig.maxRetries + 1}), ` +
        `retrying in ${Math.round(delay)}ms...`,
        error.message
      );

      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }

  throw lastError;
}

/**
 * Fetch with retry wrapper
 */
async function fetchWithRetry(url, options = {}, config = DEFAULT_RETRY_CONFIG) {
  return retry(async () => {
    const response = await fetch(url, options);
    
    if (!response.ok) {
      const error = new Error(`HTTP ${response.status}: ${response.statusText}`);
      error.response = response;
      throw error;
    }
    
    return response;
  }, config);
}

/**
 * Axios-like fetch wrapper with retry
 */
async function request(url, options = {}, config = DEFAULT_RETRY_CONFIG) {
  const mergedOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  const response = await fetchWithRetry(url, mergedOptions, config);

  let data;
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  return {
    data,
    status: response.status,
    statusText: response.statusText,
    headers: response.headers,
  };
}

/**
 * GET request with retry
 */
async function get(url, options = {}, config = DEFAULT_RETRY_CONFIG) {
  return request(url, { ...options, method: 'GET' }, config);
}

/**
 * POST request with retry
 */
async function post(url, data, options = {}, config = DEFAULT_RETRY_CONFIG) {
  return request(url, {
    ...options,
    method: 'POST',
    body: JSON.stringify(data),
  }, config);
}

/**
 * PUT request with retry
 */
async function put(url, data, options = {}, config = DEFAULT_RETRY_CONFIG) {
  return request(url, {
    ...options,
    method: 'PUT',
    body: JSON.stringify(data),
  }, config);
}

/**
 * DELETE request with retry
 */
async function del(url, options = {}, config = DEFAULT_RETRY_CONFIG) {
  return request(url, { ...options, method: 'DELETE' }, config);
}

export {
  retry,
  fetchWithRetry,
  request,
  get,
  post,
  put,
  del,
  RetryError,
  DEFAULT_RETRY_CONFIG,
};
