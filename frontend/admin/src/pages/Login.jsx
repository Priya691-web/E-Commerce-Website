import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { Button, Input, Card } from '../design-system/index.js';

export default function Login() {
  const { login, user } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // If already authenticated, redirect to dashboard.
  // Using <Navigate> avoids the "Cannot update a component while rendering"
  // warning that navigate() would trigger during render.
  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!email || !password) {
      setError('Please enter both email and password.');
      return;
    }
    setSubmitting(true);
    try {
      const result = await login(email, password);
      if (result.ok) {
        navigate('/admin/dashboard', { replace: true });
      } else {
        setError(result.message || 'Invalid credentials.');
      }
    } catch {
      setError('Something went wrong. Please try again.');
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
      
      <Card className="relative w-full container-md section-md">
        <div className="flex flex-col items-center gap-3 mb-8 text-center">
          <span className="badge badge-primary">Admin Access</span>
          <h1 className="text-h1">Welcome Back</h1>
          <p className="text-body max-w-md">
            Secure entry to the FashionStore control room
          </p>
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-lg bg-[var(--color-error)]/10 text-[var(--color-error)] text-sm border border-[var(--color-error)]/20">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
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
            <label htmlFor="password" className="block text-sm font-semibold text-[var(--color-text-primary)] mb-2">
              Password
            </label>
            <Input
              id="password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              disabled={submitting}
            />
          </div>

          <Button
            type="submit"
            disabled={submitting}
            variant="primary"
            className="w-full"
          >
            {submitting ? 'Signing in…' : 'Sign In'}
          </Button>
        </form>

        <p className="text-center text-body-sm mt-8 text-[var(--color-text-secondary)]">
          Need an admin account?{' '}
          <Link to="/register" className="font-semibold text-[var(--color-primary)] hover:text-[var(--color-primary-hover)] underline-offset-4 hover:underline transition-colors">
            Register as admin
          </Link>
        </p>
      </Card>
    </div>
  );
}
