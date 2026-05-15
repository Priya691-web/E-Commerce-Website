/**
 * Validators Module
 * Centralized validation for API requests and responses
 */

/**
 * Validate email format
 */
export function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate password strength
 */
export function isValidPassword(password) {
  // Minimum 8 characters, at least one letter and one number
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
  return passwordRegex.test(password);
}

/**
 * Validate required fields
 */
export function validateRequired(value) {
  return value !== null && value !== undefined && value !== '';
}

/**
 * Validate URL format
 */
export function isValidURL(url) {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

/**
 * Validate phone number format
 */
export function isValidPhone(phone) {
  const phoneRegex = /^\+?[\d\s-()]{10,}$/;
  return phoneRegex.test(phone);
}

/**
 * Validate numeric value
 */
export function isNumeric(value) {
  return !isNaN(parseFloat(value)) && isFinite(value);
}

/**
 * Validate positive number
 */
export function isPositiveNumber(value) {
  return isNumeric(value) && parseFloat(value) > 0;
}

/**
 * Validate integer
 */
export function isInteger(value) {
  return Number.isInteger(Number(value));
}

/**
 * Validate date format
 */
export function isValidDate(date) {
  return !isNaN(Date.parse(date));
}

/**
 * Validate response structure
 */
export function validateResponse(response, requiredFields = []) {
  if (!response || !response.data) {
    return false;
  }
  
  for (const field of requiredFields) {
    if (!(field in response.data)) {
      return false;
    }
  }
  
  return true;
}

/**
 * Sanitize user input
 */
export function sanitizeInput(input) {
  if (typeof input !== 'string') {
    return input;
  }
  
  return input.trim().replace(/[<>]/g, '');
}

/**
 * Validate request data
 */
export function validateRequestData(data, schema) {
  const errors = [];
  
  for (const [field, rules] of Object.entries(schema)) {
    const value = data[field];
    
    if (rules.required && !validateRequired(value)) {
      errors.push(`${field} is required`);
      continue;
    }
    
    if (value !== undefined && value !== null && value !== '') {
      if (rules.type === 'email' && !isValidEmail(value)) {
        errors.push(`${field} must be a valid email`);
      }
      
      if (rules.type === 'password' && !isValidPassword(value)) {
        errors.push(`${field} must be at least 8 characters with letters and numbers`);
      }
      
      if (rules.type === 'url' && !isValidURL(value)) {
        errors.push(`${field} must be a valid URL`);
      }
      
      if (rules.type === 'phone' && !isValidPhone(value)) {
        errors.push(`${field} must be a valid phone number`);
      }
      
      if (rules.type === 'number' && !isNumeric(value)) {
        errors.push(`${field} must be a number`);
      }
      
      if (rules.type === 'positive' && !isPositiveNumber(value)) {
        errors.push(`${field} must be a positive number`);
      }
      
      if (rules.min !== undefined && Number(value) < rules.min) {
        errors.push(`${field} must be at least ${rules.min}`);
      }
      
      if (rules.max !== undefined && Number(value) > rules.max) {
        errors.push(`${field} must be at most ${rules.max}`);
      }
    }
  }
  
  return errors;
}
