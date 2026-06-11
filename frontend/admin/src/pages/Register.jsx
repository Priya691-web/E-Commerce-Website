import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { Button, Input, Card } from '../design-system/index.js';
import adminApiClient from '../core/api/client.js';

export default function Register() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [adminKey, setAdminKey] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !phone || !password || !confirmPassword || !adminKey) {
      setError('Please fill in all required fields.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    setSubmitting(true);
    try {
      // Register via centralized API client for consistent error handling and retry logic
      const registerResponse = await adminApiClient.post('/register', {
        fullName,
        email,
        phone,
        password,
        confirmPassword,
        adminKey,
      });

      if (registerResponse.data?.success) {
        // Auto-login after successful registration
        const loginResult = await login(email, password);
        if (loginResult.ok) {
          navigate('/admin/dashboard', { replace: true });
        } else {
          setError('Registration successful but auto-login failed. Please login manually.');
          navigate('/admin/login', { replace: true });
        }
      } else {
        setError(registerResponse.data?.message || 'Registration failed.');
      }
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Something went wrong. Please try again.';
      setError(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4 bg-[var(--color-bg)] relative overflow-hidden">
      {/* Premium background effects */}
      <div className="absolute inset-0 pointer-events-none" style={{
        backgroundImage: 'radial-gradient(circle at 20% 30%, rgba(184, 149, 110, 0.08), transparent 50%), radial-gradient(circle at 80% 70%, rgba(74, 69, 62, 0.05), transparent 50%)'
      }} />
      <div className="absolute inset-0 bg-gradient-to-br from-[var(--color-bg)] via-[var(--color-bg-secondary)] to-[var(--color-bg-tertiary)] backdrop-blur-xl" aria-hidden="true" />
      
      <Card className="relative w-full container-md card section-md">
        <div className="flex flex-col items-center gap-3 mb-8 text-center">
          <span className="badge badge-primary">Create Access</span>
          <h1 className="text-h1">Admin Registration</h1>
          <p className="text-body max-w-md">
            Provision a new admin for the control room
          </p>
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-lg bg-[var(--color-error)]/10 text-[var(--color-error)] text-sm border border-[var(--color-error)]/20">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="fullName" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Full Name
            </label>
            <Input
              id="fullName"
              type="text"
              autoComplete="name"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="John Doe"
              disabled={submitting}
            />
          </div>

          <div>
            <label htmlFor="email" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Email Address
            </label>
            <Input
              id="email"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@example.com"
              disabled={submitting}
            />
          </div>

          <div>
            <label htmlFor="phone" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Phone Number
            </label>
            <Input
              id="phone"
              type="tel"
              autoComplete="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="9876543210"
              disabled={submitting}
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Password
            </label>
            <Input
              id="password"
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              disabled={submitting}
              minLength={8}
            />
          </div>

          <div>
            <label htmlFor="confirmPassword" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Confirm Password
            </label>
            <Input
              id="confirmPassword"
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••"
              disabled={submitting}
              minLength={8}
            />
          </div>

          <div>
            <label htmlFor="adminKey" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Admin Secret Key
            </label>
            <Input
              id="adminKey"
              type="password"
              value={adminKey}
              onChange={(e) => setAdminKey(e.target.value)}
              placeholder="Enter admin secret key"
              disabled={submitting}
            />
            <p className="text-body-sm text-[var(--color-text-muted)] mt-2">
              Contact the system owner for the admin secret key
            </p>
          </div>

          <Button
            type="submit"
            disabled={submitting}
            variant="primary"
            className="w-full"
          >
            {submitting ? 'Creating account…' : 'Create Admin Account'}
          </Button>
        </form>

        <p className="text-center text-body-sm mt-8 text-[var(--color-text-secondary)]">
          Already have an account?{' '}
          <Link to="/login" className="font-semibold text-[var(--color-primary)] hover:text-[var(--color-primary-hover)] underline-offset-4 hover:underline transition-colors">
            Sign in
          </Link>
        </p>
      </Card>
    </div>
  );
}
