/**
 * Schema Validation for API Requests/Responses
 * Provides type-safe API communication with Zod-like validation
 */

class SchemaValidator {
  /**
   * Validate data against schema
   */
  static validate(data, schema, options = {}) {
    try {
      const result = schema.safeParse(data);
      
      if (!result.success) {
        const errors = this.formatErrors(result.error);
        throw new ValidationError('Schema validation failed', errors);
      }
      
      return result.data;
    } catch (error) {
      if (error instanceof ValidationError) {
        throw error;
      }
      throw new ValidationError('Validation error', error.message);
    }
  }

  /**
   * Format Zod errors
   */
  static formatErrors(error) {
    const errors = {};
    
    error.errors.forEach((err) => {
      const path = err.path.join('.') || 'root';
      errors[path] = err.message;
    });
    
    return errors;
  }

  /**
   * Create a schema validator
   */
  static createSchema(shape) {
    return {
      safeParse: (data) => {
        try {
          const result = this.parseShape(data, shape);
          return { success: true, data: result };
        } catch (error) {
          return { 
            success: false, 
            error: { 
              errors: [{ 
                path: error.path || [], 
                message: error.message 
              }] 
            } 
          };
        }
      }
    };
  }

  /**
   * Parse data against shape
   */
  static parseShape(data, shape) {
    const result = {};
    
    for (const [key, def] of Object.entries(shape)) {
      if (def.required && data[key] === undefined) {
        throw { path: [key], message: `${key} is required` };
      }
      
      if (data[key] !== undefined) {
        result[key] = this.validateType(data[key], def);
      }
    }
    
    return result;
  }

  /**
   * Validate type
   */
  static validateType(value, def) {
    const { type, validate } = def;
    
    switch (type) {
      case 'string':
        if (typeof value !== 'string') {
          throw { path: [], message: `Expected string, got ${typeof value}` };
        }
        break;
      case 'number':
        if (typeof value !== 'number') {
          throw { path: [], message: `Expected number, got ${typeof value}` };
        }
        break;
      case 'boolean':
        if (typeof value !== 'boolean') {
          throw { path: [], message: `Expected boolean, got ${typeof value}` };
        }
        break;
      case 'array':
        if (!Array.isArray(value)) {
          throw { path: [], message: `Expected array, got ${typeof value}` };
        }
        break;
      case 'object':
        if (typeof value !== 'object' || value === null || Array.isArray(value)) {
          throw { path: [], message: `Expected object, got ${typeof value}` };
        }
        break;
    }
    
    if (validate && !validate(value)) {
      throw { path: [], message: `Validation failed for ${type}` };
    }
    
    return value;
  }
}

/**
 * Custom ValidationError
 */
class ValidationError extends Error {
  constructor(message, errors = {}) {
    super(message);
    this.name = 'ValidationError';
    this.errors = errors;
  }
}

// Common schema shapes
export const schemas = {
  // User schemas
  user: SchemaValidator.createSchema({
    id: { type: 'number', required: true },
    email: { type: 'string', required: true, validate: (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) },
    name: { type: 'string', required: true },
    role: { type: 'string', required: false },
  }),
  
  // Product schemas
  product: SchemaValidator.createSchema({
    id: { type: 'number', required: true },
    name: { type: 'string', required: true },
    description: { type: 'string', required: false },
    price: { type: 'number', required: true, validate: (v) => v >= 0 },
    categoryId: { type: 'number', required: true },
    imageUrl: { type: 'string', required: false },
    stockQuantity: { type: 'number', required: false, validate: (v) => v >= 0 },
  }),
  
  // Order schemas
  order: SchemaValidator.createSchema({
    id: { type: 'number', required: true },
    userId: { type: 'number', required: true },
    totalAmount: { type: 'number', required: true, validate: (v) => v >= 0 },
    status: { type: 'string', required: true },
    createdAt: { type: 'string', required: false },
  }),
  
  // Cart schemas
  cartItem: SchemaValidator.createSchema({
    id: { type: 'number', required: true },
    productId: { type: 'number', required: true },
    quantity: { type: 'number', required: true, validate: (v) => v > 0 },
    sizeLabel: { type: 'string', required: false },
  }),
  
  // API response schemas
  apiResponse: SchemaValidator.createSchema({
    success: { type: 'boolean', required: true },
    data: { type: 'object', required: false },
    error: { type: 'string', required: false },
  }),
  
  // Pagination schemas
  pagination: SchemaValidator.createSchema({
    page: { type: 'number', required: true, validate: (v) => v > 0 },
    limit: { type: 'number', required: true, validate: (v) => v > 0 },
    total: { type: 'number', required: true, validate: (v) => v >= 0 },
  }),
};

export default SchemaValidator;
export { ValidationError };
