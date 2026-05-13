<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.UUID" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Login");
    request.setAttribute("_pageCSS", "auth-v2");
    request.setAttribute("_noNavbar", "true");
    request.setAttribute("_noFooter", "true");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body class="auth-body">
    <!-- SPLIT SCREEN LOGIN -->
    <div class="auth-container">
        <!-- LEFT SIDE - BRANDING -->
        <div class="auth-branding">
            <div class="branding-content">
                <div class="brand-header">
                    <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="brand-logo">
                    <h1 class="brand-title">Welcome Back</h1>
                    <p class="brand-subtitle">Sign in to access your personalized fashion journey</p>
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
                            <h3>Personalized Style</h3>
                            <p>Get recommendations based on your fashion preferences</p>
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
                            <h3>Track Orders</h3>
                            <p>Real-time order tracking and delivery updates</p>
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
                            <p>Earn points and unlock exclusive member benefits</p>
                        </div>
                    </div>
                </div>
                
                <div class="brand-testimonial">
                    <div class="testimonial-content">
                        <blockquote>"FashionStore has completely transformed my shopping experience with their personalized recommendations and fast delivery."</blockquote>
                        <cite>- Sarah K., Premium Member</cite>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT SIDE - LOGIN FORM -->
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
                        <span>Secure Login</span>
                    </div>
                    <div class="trust-badge">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                        <span>SSL Encrypted</span>
                    </div>
                </div>

                <!-- Login Form -->
                <form class="auth-form" id="login-form" action="<%= request.getContextPath() %>/login" method="post">
                    <input type="hidden" name="csrf_token" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>" />
                    <input type="hidden" name="device_info" id="device-info" />
                    
                    <div class="form-header">
                        <h2>Sign In</h2>
                        <p>Welcome back! Please enter your details.</p>
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

                    <!-- Password Field -->
                    <div class="form-group">
                        <label for="password" class="form-label">Password</label>
                        <div class="input-wrapper">
                            <input type="password" id="password" name="password" class="form-input" 
                                   placeholder="Enter your password" 
                                   autocomplete="current-password" required>
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

                    <!-- Remember Me & Forgot Password -->
                    <div class="form-options">
                        <label class="checkbox-wrapper">
                            <input type="checkbox" id="remember-me" name="remember-me" value="true">
                            <span class="checkbox-mark"></span>
                            <span class="checkbox-label">Remember me</span>
                        </label>
                        <a href="<%= request.getContextPath() %>/forgot-password" class="forgot-password">Forgot password?</a>
                    </div>

                    <!-- Login Button -->
                    <button type="submit" class="btn btn-primary btn-full btn-lg" id="login-btn">
                        <span class="btn-text">Sign In</span>
                        <svg class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                            <polyline points="10 17 15 12 10 7"/>
                            <line x1="15" y1="12" x2="3" y2="12"/>
                        </svg>
                    </button>

                    <!-- Divider -->
                    <div class="auth-divider">
                        <span>OR</span>
                    </div>

                    <!-- Social Login -->
                    <div class="social-login">
                        <button type="button" class="social-btn google-btn" data-provider="google">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                            </svg>
                            <span>Continue with Google</span>
                        </button>
                        
                        <button type="button" class="social-btn facebook-btn" data-provider="facebook">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/>
                            </svg>
                            <span>Continue with Facebook</span>
                        </button>
                    </div>

                    <!-- Sign Up Link -->
                    <div class="auth-footer">
                        <p>New to FashionStore? <a href="<%= request.getContextPath() %>/register">Create account</a></p>
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
                        <p>Your login is secured with 256-bit SSL encryption</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- OTP Modal (for 2FA) -->
    <div class="otp-modal" id="otp-modal" style="display: none;">
        <div class="otp-modal-content">
            <div class="otp-modal-header">
                <h3>Two-Factor Authentication</h3>
                <p>Enter the 6-digit code sent to your email</p>
            </div>
            
            <div class="otp-inputs">
                <input type="text" class="otp-input" maxlength="1" data-index="0">
                <input type="text" class="otp-input" maxlength="1" data-index="1">
                <input type="text" class="otp-input" maxlength="1" data-index="2">
                <input type="text" class="otp-input" maxlength="1" data-index="3">
                <input type="text" class="otp-input" maxlength="1" data-index="4">
                <input type="text" class="otp-input" maxlength="1" data-index="5">
            </div>
            
            <div class="otp-actions">
                <button type="button" class="btn btn-outline" id="resend-otp">Resend Code</button>
                <button type="button" class="btn btn-primary" id="verify-otp">Verify</button>
            </div>
            
            <div class="otp-timer">
                <span>Resend code in <span id="otp-timer">30</span>s</span>
            </div>
        </div>
    </div>

    <!-- Loading Overlay -->
    <div class="auth-loading" id="auth-loading" style="display: none;">
        <div class="loading-spinner"></div>
        <p>Signing you in...</p>
    </div>

    <script>
    // FashionStore Login V2 - Premium Authentication Experience
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
            otpTimer: 30,
            maxOtpAttempts: 3
        };

        // DOM Elements
        let elements = {};
        let otpTimer = null;
        let otpAttempts = 0;

        // Initialize
        function init() {
            cacheElements();
            bindEvents();
            collectDeviceInfo();
            initializePasswordStrength();
            initializeSocialLogin();
            initializeOTP();
        }

        // Cache DOM elements
        function cacheElements() {
            elements = {
                // Form elements
                loginForm: document.getElementById('login-form'),
                emailInput: document.getElementById('email'),
                passwordInput: document.getElementById('password'),
                rememberMe: document.getElementById('remember-me'),
                loginBtn: document.getElementById('login-btn'),
                
                // Validation elements
                emailError: document.getElementById('email-error'),
                passwordError: document.getElementById('password-error'),
                authError: document.getElementById('auth-error'),
                authSuccess: document.getElementById('auth-success'),
                
                // Password strength
                passwordStrength: document.getElementById('password-strength'),
                strengthFill: document.getElementById('strength-fill'),
                strengthText: document.getElementById('strength-text'),
                passwordToggle: document.getElementById('password-toggle'),
                
                // Social login
                socialBtns: document.querySelectorAll('.social-btn'),
                
                // OTP modal
                otpModal: document.getElementById('otp-modal'),
                otpInputs: document.querySelectorAll('.otp-input'),
                verifyOtpBtn: document.getElementById('verify-otp'),
                resendOtpBtn: document.getElementById('resend-otp'),
                otpTimer: document.getElementById('otp-timer'),
                
                // Loading
                authLoading: document.getElementById('auth-loading'),
                deviceInfo: document.getElementById('device-info')
            };
        }

        // Bind events
        function bindEvents() {
            // Form submission
            if (elements.loginForm) {
                elements.loginForm.addEventListener('submit', handleLoginSubmit);
            }

            // Input validation
            if (elements.emailInput) {
                elements.emailInput.addEventListener('blur', validateEmail);
                elements.emailInput.addEventListener('input', clearEmailError);
            }

            if (elements.passwordInput) {
                elements.passwordInput.addEventListener('input', handlePasswordInput);
                elements.passwordInput.addEventListener('blur', validatePassword);
                elements.passwordInput.addEventListener('input', clearPasswordError);
            }

            // Password toggle
            if (elements.passwordToggle) {
                elements.passwordToggle.addEventListener('click', togglePasswordVisibility);
            }

            // Social login
            elements.socialBtns.forEach(btn => {
                btn.addEventListener('click', handleSocialLogin);
            });

            // OTP inputs
            elements.otpInputs.forEach((input, index) => {
                input.addEventListener('input', (e) => handleOtpInput(e, index));
                input.addEventListener('keydown', (e) => handleOtpKeydown(e, index));
                input.addEventListener('paste', handleOtpPaste);
            });

            // OTP actions
            if (elements.verifyOtpBtn) {
                elements.verifyOtpBtn.addEventListener('click', verifyOTP);
            }
            if (elements.resendOtpBtn) {
                elements.resendOtpBtn.addEventListener('click', resendOTP);
            }

            // Auto-focus first input
            if (elements.emailInput) {
                elements.emailInput.focus();
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

        // Email validation
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

        // Password validation
        function validatePassword() {
            const password = elements.passwordInput.value;

            if (!password) {
                showFieldError(elements.passwordError, 'Password is required');
                return false;
            }

            if (password.length < 6) {
                showFieldError(elements.passwordError, 'Password must be at least 6 characters');
                return false;
            }

            clearFieldError(elements.passwordError);
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
        function togglePasswordVisibility() {
            const input = elements.passwordInput;
            const isPassword = input.type === 'password';
            
            input.type = isPassword ? 'text' : 'password';
            elements.passwordToggle.setAttribute('aria-pressed', !isPassword);
            
            // Update icon
            const svg = elements.passwordToggle.querySelector('svg');
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
            console.log(`Initiating ${provider} login`);
            
            // For demo purposes, just show a message
            setTimeout(() => {
                hideLoading();
                showAuthSuccess(`${provider.charAt(0).toUpperCase() + provider.slice(1)} login is not yet implemented`);
            }, 1000);
        }

        // OTP functionality
        function initializeOTP() {
            // OTP is handled in bindEvents
        }

        function handleOtpInput(e, index) {
            const input = e.target;
            const value = input.value;
            
            // Only allow numbers
            input.value = value.replace(/[^0-9]/g, '');
            
            if (input.value && index < elements.otpInputs.length - 1) {
                elements.otpInputs[index + 1].focus();
            }
            
            // Auto-verify when all inputs are filled
            const allFilled = Array.from(elements.otpInputs).every(inp => inp.value.length === 1);
            if (allFilled) {
                setTimeout(() => verifyOTP(), 100);
            }
        }

        function handleOtpKeydown(e, index) {
            const input = e.target;
            
            if (e.key === 'Backspace' && !input.value && index > 0) {
                elements.otpInputs[index - 1].focus();
            } else if (e.key === 'ArrowLeft' && index > 0) {
                e.preventDefault();
                elements.otpInputs[index - 1].focus();
            } else if (e.key === 'ArrowRight' && index < elements.otpInputs.length - 1) {
                e.preventDefault();
                elements.otpInputs[index + 1].focus();
            }
        }

        function handleOtpPaste(e) {
            e.preventDefault();
            const pastedData = e.clipboardData.getData('text');
            const digits = pastedData.replace(/[^0-9]/g, '').slice(0, 6);
            
            digits.split('').forEach((digit, index) => {
                if (index < elements.otpInputs.length) {
                    elements.otpInputs[index].value = digit;
                }
            });
            
            // Focus the last filled input
            const lastIndex = Math.min(digits.length, elements.otpInputs.length) - 1;
            if (lastIndex >= 0) {
                elements.otpInputs[lastIndex].focus();
            }
        }

        function verifyOTP() {
            const otp = Array.from(elements.otpInputs).map(input => input.value).join('');
            
            if (otp.length !== 6) {
                showAuthError('Please enter all 6 digits');
                return;
            }

            showLoading();
            
            // In a real implementation, this would verify the OTP with the server
            console.log('Verifying OTP:', otp);
            
            // For demo purposes, just accept any 6-digit code
            setTimeout(() => {
                hideLoading();
                hideOTPModal();
                showAuthSuccess('OTP verified successfully!');
            }, 1000);
        }

        function resendOTP() {
            if (otpTimer) return;
            
            otpAttempts = 0;
            startOTPTimer();
            
            // In a real implementation, this would send a new OTP
            console.log('Resending OTP');
            
            showAuthSuccess('New OTP sent to your email');
        }

        function startOTPTimer() {
            let timeLeft = CONFIG.otpTimer;
            
            otpTimer = setInterval(() => {
                timeLeft--;
                elements.otpTimer.textContent = timeLeft;
                
                if (timeLeft <= 0) {
                    clearInterval(otpTimer);
                    otpTimer = null;
                    elements.resendOtpBtn.disabled = false;
                    elements.resendOtpBtn.textContent = 'Resend Code';
                } else {
                    elements.resendOtpBtn.disabled = true;
                    elements.resendOtpBtn.textContent = `Resend Code (${timeLeft}s)`;
                }
            }, 1000);
        }

        function showOTPModal() {
            elements.otpModal.style.display = 'flex';
            elements.otpInputs[0].focus();
            startOTPTimer();
        }

        function hideOTPModal() {
            elements.otpModal.style.display = 'none';
            if (otpTimer) {
                clearInterval(otpTimer);
                otpTimer = null;
            }
            // Clear OTP inputs
            elements.otpInputs.forEach(input => input.value = '');
        }

        // Form submission
        function handleLoginSubmit(e) {
            e.preventDefault();
            
            // Validate form
            const isEmailValid = validateEmail();
            const isPasswordValid = validatePassword();
            
            if (!isEmailValid || !isPasswordValid) {
                return;
            }

            // Show loading
            showLoading();
            
            // Submit form
            elements.loginForm.submit();
        }

        // Loading states
        function showLoading() {
            elements.authLoading.style.display = 'flex';
            elements.loginBtn.disabled = true;
            elements.loginBtn.querySelector('.btn-text').textContent = 'Signing in...';
        }

        function hideLoading() {
            elements.authLoading.style.display = 'none';
            elements.loginBtn.disabled = false;
            elements.loginBtn.querySelector('.btn-text').textContent = 'Sign In';
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
