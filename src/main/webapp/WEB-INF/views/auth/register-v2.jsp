<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Register");
    request.setAttribute("_pageCSS", "auth-v2");
    request.setAttribute("_noNavbar", "true");
    request.setAttribute("_noFooter", "true");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body class="auth-body">
    <!-- SPLIT SCREEN REGISTRATION -->
    <div class="auth-container">
        <!-- LEFT SIDE - BRANDING -->
        <div class="auth-branding">
            <div class="branding-content">
                <div class="brand-header">
                    <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="brand-logo">
                    <h1 class="brand-title">Join FashionStore</h1>
                    <p class="brand-subtitle">Create your account and start your personalized fashion journey</p>
                </div>
                
                <div class="brand-features">
                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                <circle cx="12" cy="7" r="4"/>
                            </svg>
                        </div>
                        <div class="feature-content">
                            <h3>Personal Account</h3>
                            <p>Track orders, save favorites, and get personalized recommendations</p>
                        </div>
                    </div>
                    
                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 21.23 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                            </svg>
                        </div>
                        <div class="feature-content">
                            <h3>Loyalty Rewards</h3>
                            <p>Earn points with every purchase and unlock exclusive benefits</p>
                        </div>
                    </div>
                    
                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M9 11l3 3L22 4"/>
                                <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
                            </svg>
                        </div>
                        <div class="feature-content">
                            <h3>Fast Checkout</h3>
                            <p>Save addresses and payment methods for quick checkout</p>
                        </div>
                    </div>
                </div>
                
                <div class="brand-stats">
                    <div class="stat-item">
                        <div class="stat-number">2M+</div>
                        <div class="stat-label">Happy Customers</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">500+</div>
                        <div class="stat-label">Fashion Brands</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">24/7</div>
                        <div class="stat-label">Customer Support</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT SIDE - REGISTRATION FORM -->
        <div class="auth-form-container">
            <div class="auth-form-wrapper">
                <!-- Mobile Logo (hidden on desktop) -->
                <div class="auth-mobile-logo">
                    <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="mobile-brand-logo">
                </div>

                <!-- Trust Indicators -->
                <div class="auth-trust-badges">
                    <div class="trust-badge">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                        <span>Secure Registration</span>
                    </div>
                    <div class="trust-badge">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                        <span>SSL Encrypted</span>
                    </div>
                </div>

                <!-- Registration Form -->
                <form class="auth-form" id="register-form" action="<%= request.getContextPath() %>/register" method="post">
                    <input type="hidden" name="csrf_token" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>" />
                    <input type="hidden" name="device_info" id="device-info" />
                    
                    <div class="form-header">
                        <h2>Create Account</h2>
                        <p>Join us and start your fashion journey today</p>
                    </div>

                    <!-- Error Messages -->
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="auth-error" id="auth-error">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"/>
                                <line x1="15" y1="9" x2="9" y2="15"/>
                                <line x1="9" y1="9" x2="15" y2="15"/>
                            </svg>
                            <span><%= request.getAttribute("error") %></span>
                        </div>
                    <% } %>

                    <!-- Success Messages -->
                    <% if (request.getAttribute("success") != null) { %>
                        <div class="auth-success" id="auth-success">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                                <polyline points="22,4 12,14.01 9,11.01"/>
                            </svg>
                            <span><%= request.getAttribute("success") %></span>
                        </div>
                    <% } %>

                    <!-- Name Fields -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="firstName" class="form-label">First Name</label>
                            <div class="input-wrapper">
                                <input type="text" id="firstName" name="firstName" class="form-input" 
                                       placeholder="Enter your first name" 
                                       autocomplete="given-name" required
                                       value="<%= request.getAttribute("firstName") != null ? request.getAttribute("firstName") : "" %>">
                                <div class="input-icon">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                        <circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </div>
                            </div>
                            <div class="form-error" id="firstName-error"></div>
                        </div>
                        
                        <div class="form-group">
                            <label for="lastName" class="form-label">Last Name</label>
                            <div class="input-wrapper">
                                <input type="text" id="lastName" name="lastName" class="form-input" 
                                       placeholder="Enter your last name" 
                                       autocomplete="family-name" required
                                       value="<%= request.getAttribute("lastName") != null ? request.getAttribute("lastName") : "" %>">
                                <div class="input-icon">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                        <circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </div>
                            </div>
                            <div class="form-error" id="lastName-error"></div>
                        </div>
                    </div>

                    <!-- Email Field -->
                    <div class="form-group">
                        <label for="email" class="form-label">Email Address</label>
                        <div class="input-wrapper">
                            <input type="email" id="email" name="email" class="form-input" 
                                   placeholder="Enter your email address" 
                                   autocomplete="email" required
                                   value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
                            <div class="input-icon">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                    <polyline points="22,6 12,13 2,6"/>
                                </svg>
                            </div>
                        </div>
                        <div class="form-error" id="email-error"></div>
                    </div>

                    <!-- Phone Field -->
                    <div class="form-group">
                        <label for="phone" class="form-label">Phone Number</label>
                        <div class="input-wrapper">
                            <input type="tel" id="phone" name="phone" class="form-input" 
                                   placeholder="Enter your phone number" 
                                   autocomplete="tel" required
                                   value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>">
                            <div class="input-icon">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                                </svg>
                            </div>
                        </div>
                        <div class="form-error" id="phone-error"></div>
                    </div>

                    <!-- Gender Field -->
                    <div class="form-group">
                        <label for="gender" class="form-label">Gender</label>
                        <div class="radio-group">
                            <label class="radio-option">
                                <input type="radio" name="gender" value="Male" <%= "Male".equals(request.getAttribute("gender")) ? "checked" : "" %>>
                                <span class="radio-mark"></span>
                                <span class="radio-label">Male</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="gender" value="Female" <%= "Female".equals(request.getAttribute("gender")) ? "checked" : "" %>>
                                <span class="radio-mark"></span>
                                <span class="radio-label">Female</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="gender" value="Other" <%= "Other".equals(request.getAttribute("gender")) ? "checked" : "" %>>
                                <span class="radio-mark"></span>
                                <span class="radio-label">Other</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="gender" value="Prefer not to say" <%= "Prefer not to say".equals(request.getAttribute("gender")) ? "checked" : "" %>>
                                <span class="radio-mark"></span>
                                <span class="radio-label">Prefer not to say</span>
                            </label>
                        </div>
                        <div class="form-error" id="gender-error"></div>
                    </div>

                    <!-- Password Fields -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-wrapper">
                                <input type="password" id="password" name="password" class="form-input" 
                                       placeholder="Create a strong password" 
                                       autocomplete="new-password" required>
                                <button type="button" class="password-toggle" id="password-toggle" aria-label="Show password">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                        <circle cx="12" cy="12" r="3"/>
                                    </svg>
                                </button>
                            </div>
                            <div class="form-error" id="password-error"></div>
                            
                            <!-- Password Strength Meter -->
                            <div class="password-strength" id="password-strength" style="display: none;">
                                <div class="strength-bar">
                                    <div class="strength-fill" id="strength-fill"></div>
                                </div>
                                <span class="strength-text" id="strength-text"></span>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                            <div class="input-wrapper">
                                <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" 
                                       placeholder="Confirm your password" 
                                       autocomplete="new-password" required>
                                <button type="button" class="password-toggle" id="confirm-password-toggle" aria-label="Show confirm password">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                        <circle cx="12" cy="12" r="3"/>
                                    </svg>
                                </button>
                            </div>
                            <div class="form-error" id="confirmPassword-error"></div>
                        </div>
                    </div>

                    <!-- Address Field -->
                    <div class="form-group">
                        <label for="address" class="form-label">Delivery Address</label>
                        <div class="input-wrapper">
                            <input type="text" id="address" name="address" class="form-input" 
                                   placeholder="Enter your delivery address" 
                                   autocomplete="street-address"
                                   value="<%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %>">
                            <div class="input-icon">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                                    <circle cx="12" cy="10" r="3"/>
                                </svg>
                            </div>
                        </div>
                        <div class="form-hint">You can add more addresses after registration</div>
                        <div class="form-error" id="address-error"></div>
                    </div>

                    <!-- Terms and Conditions -->
                    <div class="form-group">
                        <label class="checkbox-wrapper">
                            <input type="checkbox" id="terms" name="terms" value="true" required>
                            <span class="checkbox-mark"></span>
                            <span class="checkbox-label">I agree to the <a href="<%= request.getContextPath() %>/policy/terms-of-service" target="_blank">Terms of Service</a> and <a href="<%= request.getContextPath() %>/policy/privacy-policy" target="_blank">Privacy Policy</a></span>
                        </label>
                        <div class="form-error" id="terms-error"></div>
                    </div>

                    <!-- Marketing Consent -->
                    <div class="form-group">
                        <label class="checkbox-wrapper">
                            <input type="checkbox" id="marketing" name="marketing" value="true">
                            <span class="checkbox-mark"></span>
                            <span class="checkbox-label">Send me exclusive offers and fashion updates via email</span>
                        </label>
                    </div>

                    <!-- Register Button -->
                    <button type="submit" class="btn btn-primary btn-full btn-lg" id="register-btn">
                        <span class="btn-text">Create Account</span>
                        <svg class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="8.5" cy="7" r="4"/>
                            <line x1="20" y1="8" x2="20" y2="14"/>
                            <line x1="23" y1="11" x2="17" y2="11"/>
                        </svg>
                    </button>

                    <!-- Divider -->
                    <div class="auth-divider">
                        <span>OR</span>
                    </div>

                    <!-- Social Registration -->
                    <div class="social-login">
                        <button type="button" class="social-btn google-btn" data-provider="google">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                            </svg>
                            <span>Sign up with Google</span>
                        </button>
                        
                        <button type="button" class="social-btn facebook-btn" data-provider="facebook">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/>
                            </svg>
                            <span>Sign up with Facebook</span>
                        </button>
                    </div>

                    <!-- Login Link -->
                    <div class="auth-footer">
                        <p>Already have an account? <a href="<%= request.getContextPath() %>/login">Sign in</a></p>
                    </div>
                </form>

                <!-- Security Notice -->
                <div class="security-notice">
                    <div class="notice-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                    </div>
                    <div class="notice-content">
                        <p>Your information is protected with 256-bit SSL encryption</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Email Verification Modal -->
    <div class="verification-modal" id="verification-modal" style="display: none;">
        <div class="verification-modal-content">
            <div class="verification-header">
                <div class="verification-icon">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 8l7.89 5.26a2 2 0 0 0 2.22 0L21 8M5 19h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z"/>
                    </svg>
                </div>
                <h3>Verify Your Email</h3>
                <p>We've sent a verification link to your email address</p>
            </div>
            
            <div class="verification-email">
                <div class="email-display" id="verification-email">user@example.com</div>
                <p>Click the link in the email to verify your account</p>
            </div>
            
            <div class="verification-actions">
                <button type="button" class="btn btn-outline" id="resend-verification">Resend Email</button>
                <button type="button" class="btn btn-primary" id="continue-to-login">Continue to Login</button>
            </div>
            
            <div class="verification-tips">
                <p>📧 Check your spam folder if you don't see the email</p>
                <p>⏰ The verification link will expire in 24 hours</p>
            </div>
        </div>
    </div>

    <!-- Loading Overlay -->
    <div class="auth-loading" id="auth-loading" style="display: none;">
        <div class="loading-spinner"></div>
        <p>Creating your account...</p>
    </div>

    <script>
    // FashionStore Register V2 - Premium Registration Experience
    (function() {
        'use strict';

        // Configuration
        const CONFIG = {
            passwordStrengthLevels: {
                0: { text: 'Very Weak', color: '#dc3545', width: '20%' },
                1: { text: 'Weak', color: '#fd7e14', width: '40%' },
                2: { text: 'Fair', color: '#ffc107', width: '60%' },
                3: { text: 'Good', color: '#20c997', width: '80%' },
                4: { text: 'Strong', color: '#198754', width: '100%' }
            },
            verificationTimer: 30
        };

        // DOM Elements
        let elements = {};
        let verificationTimer = null;

        // Initialize
        function init() {
            cacheElements();
            bindEvents();
            collectDeviceInfo();
            initializePasswordStrength();
            initializeSocialLogin();
            initializeFormValidation();
        }

        // Cache DOM elements
        function cacheElements() {
            elements = {
                // Form elements
                registerForm: document.getElementById('register-form'),
                firstNameInput: document.getElementById('firstName'),
                lastNameInput: document.getElementById('lastName'),
                emailInput: document.getElementById('email'),
                phoneInput: document.getElementById('phone'),
                passwordInput: document.getElementById('password'),
                confirmPasswordInput: document.getElementById('confirmPassword'),
                addressInput: document.getElementById('address'),
                termsCheckbox: document.getElementById('terms'),
                marketingCheckbox: document.getElementById('marketing'),
                registerBtn: document.getElementById('register-btn'),
                
                // Validation elements
                firstNameError: document.getElementById('firstName-error'),
                lastNameError: document.getElementById('lastName-error'),
                emailError: document.getElementById('email-error'),
                phoneError: document.getElementById('phone-error'),
                passwordError: document.getElementById('password-error'),
                confirmPasswordError: document.getElementById('confirmPassword-error'),
                addressError: document.getElementById('address-error'),
                termsError: document.getElementById('terms-error'),
                authError: document.getElementById('auth-error'),
                authSuccess: document.getElementById('auth-success'),
                
                // Password strength
                passwordStrength: document.getElementById('password-strength'),
                strengthFill: document.getElementById('strength-fill'),
                strengthText: document.getElementById('strength-text'),
                passwordToggle: document.getElementById('password-toggle'),
                confirmPasswordToggle: document.getElementById('confirm-password-toggle'),
                
                // Social login
                socialBtns: document.querySelectorAll('.social-btn'),
                
                // Verification modal
                verificationModal: document.getElementById('verification-modal'),
                verificationEmail: document.getElementById('verification-email'),
                resendVerificationBtn: document.getElementById('resend-verification'),
                continueToLoginBtn: document.getElementById('continue-to-login'),
                
                // Loading
                authLoading: document.getElementById('auth-loading'),
                deviceInfo: document.getElementById('device-info')
            };
        }

        // Bind events
        function bindEvents() {
            // Form submission
            if (elements.registerForm) {
                elements.registerForm.addEventListener('submit', handleRegisterSubmit);
            }

            // Input validation
            if (elements.firstNameInput) {
                elements.firstNameInput.addEventListener('blur', validateFirstName);
                elements.firstNameInput.addEventListener('input', clearFirstNameError);
            }

            if (elements.lastNameInput) {
                elements.lastNameInput.addEventListener('blur', validateLastName);
                elements.lastNameInput.addEventListener('input', clearLastNameError);
            }

            if (elements.emailInput) {
                elements.emailInput.addEventListener('blur', validateEmail);
                elements.emailInput.addEventListener('input', clearEmailError);
            }

            if (elements.phoneInput) {
                elements.phoneInput.addEventListener('blur', validatePhone);
                elements.phoneInput.addEventListener('input', clearPhoneError);
            }

            if (elements.passwordInput) {
                elements.passwordInput.addEventListener('input', handlePasswordInput);
                elements.passwordInput.addEventListener('blur', validatePassword);
                elements.passwordInput.addEventListener('input', clearPasswordError);
            }

            if (elements.confirmPasswordInput) {
                elements.confirmPasswordInput.addEventListener('blur', validateConfirmPassword);
                elements.confirmPasswordInput.addEventListener('input', clearConfirmPasswordError);
            }

            if (elements.addressInput) {
                elements.addressInput.addEventListener('blur', validateAddress);
                elements.addressInput.addEventListener('input', clearAddressError);
            }

            if (elements.termsCheckbox) {
                elements.termsCheckbox.addEventListener('change', validateTerms);
            }

            // Password toggles
            if (elements.passwordToggle) {
                elements.passwordToggle.addEventListener('click', () => togglePasswordVisibility('password'));
            }
            if (elements.confirmPasswordToggle) {
                elements.confirmPasswordToggle.addEventListener('click', () => togglePasswordVisibility('confirmPassword'));
            }

            // Social login
            elements.socialBtns.forEach(btn => {
                btn.addEventListener('click', handleSocialLogin);
            });

            // Verification modal
            if (elements.resendVerificationBtn) {
                elements.resendVerificationBtn.addEventListener('click', resendVerification);
            }
            if (elements.continueToLoginBtn) {
                elements.continueToLoginBtn.addEventListener('click', () => {
                    window.location.href = '<%= request.getContextPath() %>/login';
                });
            }

            // Auto-focus first input
            if (elements.firstNameInput) {
                elements.firstNameInput.focus();
            }
        }

        // Collect device information
        function collectDeviceInfo() {
            const deviceInfo = {
                userAgent: navigator.userAgent,
                language: navigator.language,
                platform: navigator.platform,
                screenResolution: `${screen.width}x${screen.height}`,
                colorDepth: screen.colorDepth,
                timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
                timestamp: new Date().toISOString()
            };

            if (elements.deviceInfo) {
                elements.deviceInfo.value = JSON.stringify(deviceInfo);
            }
        }

        // Validation functions
        function validateFirstName() {
            const firstName = elements.firstNameInput.value.trim();
            
            if (!firstName) {
                showFieldError(elements.firstNameError, 'First name is required');
                return false;
            }

            if (firstName.length < 2) {
                showFieldError(elements.firstNameError, 'First name must be at least 2 characters');
                return false;
            }

            if (!/^[a-zA-Z\s]+$/.test(firstName)) {
                showFieldError(elements.firstNameError, 'First name can only contain letters and spaces');
                return false;
            }

            clearFieldError(elements.firstNameError);
            return true;
        }

        function validateLastName() {
            const lastName = elements.lastNameInput.value.trim();
            
            if (!lastName) {
                showFieldError(elements.lastNameError, 'Last name is required');
                return false;
            }

            if (lastName.length < 2) {
                showFieldError(elements.lastNameError, 'Last name must be at least 2 characters');
                return false;
            }

            if (!/^[a-zA-Z\s]+$/.test(lastName)) {
                showFieldError(elements.lastNameError, 'Last name can only contain letters and spaces');
                return false;
            }

            clearFieldError(elements.lastNameError);
            return true;
        }

        function validateEmail() {
            const email = elements.emailInput.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (!email) {
                showFieldError(elements.emailError, 'Email is required');
                return false;
            }

            if (!emailRegex.test(email)) {
                showFieldError(elements.emailError, 'Please enter a valid email address');
                return false;
            }

            clearFieldError(elements.emailError);
            return true;
        }

        function validatePhone() {
            const phone = elements.phoneInput.value.trim();
            const phoneRegex = /^[6-9]\d{9}$/;

            if (!phone) {
                showFieldError(elements.phoneError, 'Phone number is required');
                return false;
            }

            if (!phoneRegex.test(phone)) {
                showFieldError(elements.phoneError, 'Please enter a valid 10-digit phone number');
                return false;
            }

            clearFieldError(elements.phoneError);
            return true;
        }

        function validatePassword() {
            const password = elements.passwordInput.value;

            if (!password) {
                showFieldError(elements.passwordError, 'Password is required');
                return false;
            }

            if (password.length < 8) {
                showFieldError(elements.passwordError, 'Password must be at least 8 characters');
                return false;
            }

            if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
                showFieldError(elements.passwordError, 'Password must contain at least one uppercase letter, one lowercase letter, and one number');
                return false;
            }

            clearFieldError(elements.passwordError);
            return true;
        }

        function validateConfirmPassword() {
            const password = elements.passwordInput.value;
            const confirmPassword = elements.confirmPasswordInput.value;

            if (!confirmPassword) {
                showFieldError(elements.confirmPasswordError, 'Please confirm your password');
                return false;
            }

            if (password !== confirmPassword) {
                showFieldError(elements.confirmPasswordError, 'Passwords do not match');
                return false;
            }

            clearFieldError(elements.confirmPasswordError);
            return true;
        }

        function validateAddress() {
            const address = elements.addressInput.value.trim();

            if (!address) {
                showFieldError(elements.addressError, 'Address is required');
                return false;
            }

            if (address.length < 10) {
                showFieldError(elements.addressError, 'Please enter a complete address');
                return false;
            }

            clearFieldError(elements.addressError);
            return true;
        }

        function validateTerms() {
            if (!elements.termsCheckbox.checked) {
                showFieldError(elements.termsError, 'You must agree to the Terms of Service and Privacy Policy');
                return false;
            }

            clearFieldError(elements.termsError);
            return true;
        }

        // Password strength meter
        function initializePasswordStrength() {
            // Password strength is handled in handlePasswordInput
        }

        function handlePasswordInput(e) {
            const password = e.target.value;
            
            if (password.length > 0) {
                elements.passwordStrength.style.display = 'block';
                const strength = calculatePasswordStrength(password);
                updatePasswordStrength(strength);
                
                // Also validate confirm password if it has a value
                if (elements.confirmPasswordInput.value) {
                    validateConfirmPassword();
                }
            } else {
                elements.passwordStrength.style.display = 'none';
            }
        }

        function calculatePasswordStrength(password) {
            let score = 0;

            // Length
            if (password.length >= 8) score++;
            if (password.length >= 12) score++;

            // Complexity
            if (/[a-z]/.test(password)) score++;
            if (/[A-Z]/.test(password)) score++;
            if (/[0-9]/.test(password)) score++;
            if (/[^a-zA-Z0-9]/.test(password)) score++;

            return Math.min(score, 4);
        }

        function updatePasswordStrength(level) {
            const config = CONFIG.passwordStrengthLevels[level];
            elements.strengthFill.style.width = config.width;
            elements.strengthFill.style.backgroundColor = config.color;
            elements.strengthText.textContent = config.text;
            elements.strengthText.style.color = config.color;
        }

        // Password visibility toggle
        function togglePasswordVisibility(field) {
            const input = field === 'password' ? elements.passwordInput : elements.confirmPasswordInput;
            const toggle = field === 'password' ? elements.passwordToggle : elements.confirmPasswordToggle;
            const isPassword = input.type === 'password';
            
            input.type = isPassword ? 'text' : 'password';
            toggle.setAttribute('aria-pressed', !isPassword);
            
            // Update icon
            const svg = toggle.querySelector('svg');
            if (isPassword) {
                svg.innerHTML = `
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-5 0-9.09-3.59-10.06-8.36M20.64 7.37A10.07 10.07 0 0 1 12 4c-5 0-9.09 3.59-10.06 8.36M1 12l5 5m-5-5l5 5"/>
                `;
            } else {
                svg.innerHTML = `
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                `;
            }
        }

        // Social login
        function initializeSocialLogin() {
            // Social login is handled in bindEvents
        }

        function handleSocialLogin(e) {
            const provider = e.currentTarget.dataset.provider;
            
            // Show loading
            showLoading();
            
            // In a real implementation, this would redirect to OAuth provider
            console.log(`Initiating ${provider} registration`);
            
            // For demo purposes, just show a message
            setTimeout(() => {
                hideLoading();
                showAuthSuccess(`${provider.charAt(0).toUpperCase() + provider.slice(1)} registration is not yet implemented`);
            }, 1000);
        }

        // Form validation
        function initializeFormValidation() {
            // Form validation is handled in individual field validators
        }

        // Form submission
        function handleRegisterSubmit(e) {
            e.preventDefault();
            
            // Validate all fields
            const validations = [
                validateFirstName(),
                validateLastName(),
                validateEmail(),
                validatePhone(),
                validatePassword(),
                validateConfirmPassword(),
                validateAddress(),
                validateTerms()
            ];
            
            if (validations.includes(false)) {
                return;
            }

            // Show loading
            showLoading();
            
            // Submit form
            elements.registerForm.submit();
        }

        // Verification modal
        function showVerificationModal(email) {
            elements.verificationEmail.textContent = email;
            elements.verificationModal.style.display = 'flex';
            startVerificationTimer();
        }

        function hideVerificationModal() {
            elements.verificationModal.style.display = 'none';
            if (verificationTimer) {
                clearInterval(verificationTimer);
                verificationTimer = null;
            }
        }

        function resendVerification() {
            if (verificationTimer) return;
            
            startVerificationTimer();
            
            // In a real implementation, this would resend the verification email
            console.log('Resending verification email');
            
            showAuthSuccess('Verification email sent successfully!');
        }

        function startVerificationTimer() {
            let timeLeft = CONFIG.verificationTimer;
            
            verificationTimer = setInterval(() => {
                timeLeft--;
                
                if (timeLeft <= 0) {
                    clearInterval(verificationTimer);
                    verificationTimer = null;
                    elements.resendVerificationBtn.disabled = false;
                    elements.resendVerificationBtn.textContent = 'Resend Email';
                } else {
                    elements.resendVerificationBtn.disabled = true;
                    elements.resendVerificationBtn.textContent = `Resend Email (${timeLeft}s)`;
                }
            }, 1000);
        }

        // Loading states
        function showLoading() {
            elements.authLoading.style.display = 'flex';
            elements.registerBtn.disabled = true;
            elements.registerBtn.querySelector('.btn-text').textContent = 'Creating Account...';
        }

        function hideLoading() {
            elements.authLoading.style.display = 'none';
            elements.registerBtn.disabled = false;
            elements.registerBtn.querySelector('.btn-text').textContent = 'Create Account';
        }

        // Error/Success messages
        function showFieldError(errorElement, message) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }

        function clearFieldError(errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }

        function showAuthError(message) {
            if (elements.authError) {
                elements.authError.querySelector('span').textContent = message;
                elements.authError.style.display = 'flex';
            }
            
            // Hide success message
            if (elements.authSuccess) {
                elements.authSuccess.style.display = 'none';
            }
            
            // Hide loading
            hideLoading();
        }

        function showAuthSuccess(message) {
            if (elements.authSuccess) {
                elements.authSuccess.querySelector('span').textContent = message;
                elements.authSuccess.style.display = 'flex';
            }
            
            // Hide error message
            if (elements.authError) {
                elements.authError.style.display = 'none';
            }
            
            // Hide loading
            hideLoading();
        }

        // Initialize on DOM ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
        } else {
            init();
        }
    })();
    </script>
</body>
</html>
