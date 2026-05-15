/**
 * Standardized Form System
 * Provides consistent form components and validation patterns
 * Ensures form consistency across the application
 */

import tokens from './tokens.js';
import semanticTokens from './semanticTokens.js';

// Form field configurations
export const formConfig = {
  // Input field sizes
  sizes: {
    sm: {
      height: tokens.spacing[8],
      padding: `${tokens.spacing[2]} ${tokens.spacing[3]}`,
      fontSize: tokens.typography.fontSize.sm,
    },
    md: {
      height: tokens.spacing[10],
      padding: `${tokens.spacing[3]} ${tokens.spacing[4]}`,
      fontSize: tokens.typography.fontSize.base,
    },
    lg: {
      height: tokens.spacing[12],
      padding: `${tokens.spacing[4]} ${tokens.spacing[5]}`,
      fontSize: tokens.typography.fontSize.lg,
    },
  },
  
  // Form field states
  states: {
    default: {
      borderColor: semanticTokens.colors.border.medium,
      backgroundColor: semanticTokens.colors.surface.card,
      boxShadow: 'none',
    },
    focus: {
      borderColor: semanticTokens.colors.interactive.default,
      backgroundColor: semanticTokens.colors.surface.card,
      boxShadow: `0 0 0 3px ${tokens.colors.accent[100]}`,
    },
    error: {
      borderColor: semanticTokens.colors.status.error,
      backgroundColor: semanticTokens.colors.surface.card,
      boxShadow: `0 0 0 3px ${tokens.colors.error[100]}`,
    },
    success: {
      borderColor: semanticTokens.colors.status.success,
      backgroundColor: semanticTokens.colors.surface.card,
      boxShadow: `0 0 0 3px ${tokens.colors.success[100]}`,
    },
    disabled: {
      borderColor: semanticTokens.colors.border.light,
      backgroundColor: tokens.colors.ink[100],
      boxShadow: 'none',
      opacity: 0.6,
    },
  },
  
  // Form spacing
  spacing: {
    labelBottom: tokens.spacing[2],
    inputBottom: tokens.spacing[3],
    helperTextTop: tokens.spacing[2],
    groupVertical: tokens.spacing[4],
    groupHorizontal: tokens.spacing[6],
  },
  
  // Form typography
  typography: {
    label: {
      fontSize: tokens.typography.fontSize.sm,
      fontWeight: tokens.typography.fontWeight.medium,
      color: semanticTokens.colors.text.primary,
    },
    helperText: {
      fontSize: tokens.typography.fontSize.xs,
      fontWeight: tokens.typography.fontWeight.normal,
      color: semanticTokens.colors.text.secondary,
    },
    errorText: {
      fontSize: tokens.typography.fontSize.xs,
      fontWeight: tokens.typography.fontWeight.normal,
      color: semanticTokens.colors.status.error,
    },
    placeholder: {
      fontSize: tokens.typography.fontSize.base,
      fontWeight: tokens.typography.fontWeight.normal,
      color: semanticTokens.colors.text.tertiary,
    },
  },
  
  // Form border radius
  borderRadius: {
    input: semanticTokens.borderRadius.input,
    button: semanticTokens.borderRadius.button,
    group: semanticTokens.borderRadius.card,
  },
};

// Form validation rules
export const validationRules = {
  required: (value) => ({
    valid: value !== null && value !== undefined && value !== '',
    message: 'This field is required',
  }),
  
  email: (value) => ({
    valid: !value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
    message: 'Please enter a valid email address',
  }),
  
  minLength: (min) => (value) => ({
    valid: !value || value.length >= min,
    message: `Minimum ${min} characters required`,
  }),
  
  maxLength: (max) => (value) => ({
    valid: !value || value.length <= max,
    message: `Maximum ${max} characters allowed`,
  }),
  
  pattern: (regex, message) => (value) => ({
    valid: !value || regex.test(value),
    message: message || 'Invalid format',
  }),
  
  numeric: (value) => ({
    valid: !value || !isNaN(value),
    message: 'Please enter a valid number',
  }),
  
  positive: (value) => ({
    valid: !value || (parseFloat(value) > 0),
    message: 'Please enter a positive number',
  }),
  
  min: (min) => (value) => ({
    valid: !value || parseFloat(value) >= min,
    message: `Minimum value is ${min}`,
  }),
  
  max: (max) => (value) => ({
    valid: !value || parseFloat(value) <= max,
    message: `Maximum value is ${max}`,
  }),
  
  url: (value) => ({
    valid: !value || /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/.test(value),
    message: 'Please enter a valid URL',
  }),
  
  phone: (value) => ({
    valid: !value || /^[\d\-\+\(\) ]+$/.test(value),
    message: 'Please enter a valid phone number',
  }),
  
  date: (value) => ({
    valid: !value || !isNaN(Date.parse(value)),
    message: 'Please enter a valid date',
  }),
  
  match: (fieldName) => (value, formData) => ({
    valid: !value || value === formData[fieldName],
    message: `Must match ${fieldName}`,
  }),
};

// Form field types
export const fieldTypes = {
  text: {
    type: 'text',
    component: 'Input',
  },
  
  email: {
    type: 'email',
    component: 'Input',
    validation: [validationRules.required, validationRules.email],
  },
  
  password: {
    type: 'password',
    component: 'Input',
    validation: [validationRules.required, validationRules.minLength(8)],
  },
  
  number: {
    type: 'number',
    component: 'Input',
    validation: [validationRules.numeric],
  },
  
  textarea: {
    type: 'textarea',
    component: 'Textarea',
  },
  
  select: {
    type: 'select',
    component: 'Select',
  },
  
  checkbox: {
    type: 'checkbox',
    component: 'Checkbox',
  },
  
  radio: {
    type: 'radio',
    component: 'RadioGroup',
  },
  
  date: {
    type: 'date',
    component: 'DatePicker',
    validation: [validationRules.date],
  },
  
  file: {
    type: 'file',
    component: 'FileUpload',
  },
  
  switch: {
    type: 'switch',
    component: 'Switch',
  },
  
  slider: {
    type: 'range',
    component: 'Slider',
  },
};

// Form error handling
export const formErrors = {
  // Error display modes
  display: {
    inline: 'inline',
    tooltip: 'tooltip',
    bottom: 'bottom',
    top: 'top',
  },
  
  // Error severity levels
  severity: {
    error: 'error',
    warning: 'warning',
    info: 'info',
  },
  
  // Error icons
  icons: {
    error: '⚠',
    warning: '⚡',
    info: 'ℹ',
    success: '✓',
  },
};

// Form accessibility
export const formAccessibility = {
  // ARIA attributes
  aria: {
    required: 'aria-required',
    invalid: 'aria-invalid',
    describedBy: 'aria-describedby',
    label: 'aria-label',
  },
  
  // Keyboard navigation
  keyboard: {
    submit: 'Enter',
    cancel: 'Escape',
    next: 'Tab',
    previous: 'Shift+Tab',
  },
  
  // Focus management
  focus: {
    auto: true,
    trap: true,
    restore: true,
  },
};

// Form submission states
export const submissionStates = {
  idle: 'idle',
  validating: 'validating',
  submitting: 'submitting',
  success: 'success',
  error: 'error',
};

// Form field metadata
export const fieldMetadata = {
  // Common field attributes
  attributes: {
    autoComplete: 'off',
    spellCheck: false,
  },
  
  // Field grouping
  groups: {
    personal: ['firstName', 'lastName', 'email', 'phone'],
    address: ['address1', 'address2', 'city', 'state', 'zip', 'country'],
    credentials: ['username', 'email', 'password', 'confirmPassword'],
  },
};

export default {
  config: formConfig,
  validation: validationRules,
  fieldTypes,
  errors: formErrors,
  accessibility: formAccessibility,
  submission: submissionStates,
  metadata: fieldMetadata,
};
