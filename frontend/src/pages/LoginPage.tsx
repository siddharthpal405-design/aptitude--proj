import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { BrandMark } from '../components/BrandMark';
import api from '../api/client';
import { AuthResponse } from '../types';
import { Mail, Lock, ArrowRight, AlertCircle } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await api.post<AuthResponse>('/auth/login', { email, password });
      login(res.data.token, res.data.user);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const fillDemoStudent = () => {
    setEmail('student@test.com');
    setPassword('Student@123');
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full bg-white p-6 sm:p-8 rounded-xl border border-slate-200 shadow-sm space-y-6">
        <div className="space-y-2">
          <BrandMark size="md" />
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight pt-2">
            Sign in to your account
          </h1>
          <p className="text-sm text-slate-500">
            Access chapter-wise practice sets, timed mock exams, and streak history.
          </p>
        </div>

        {error && (
          <div className="flex items-start space-x-2.5 p-3.5 rounded-lg bg-rose-50 border border-rose-200 text-rose-800 text-xs sm:text-sm">
            <AlertCircle className="w-4 h-4 mt-0.5 flex-shrink-0 text-rose-600" />
            <span>{error}</span>
          </div>
        )}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="login-email" className="block text-xs font-semibold text-slate-700 mb-1.5">
              Email address
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                id="login-email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                className="w-full pl-10 pr-3.5 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition"
              />
            </div>
          </div>

          <div>
            <label htmlFor="login-password" className="block text-xs font-semibold text-slate-700 mb-1.5">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                id="login-password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                className="w-full pl-10 pr-3.5 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex items-center justify-center space-x-2 py-2.5 px-4 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-slate-900 focus-visible:ring-offset-2 transition disabled:opacity-60"
          >
            {loading ? (
              <span className="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <>
                <span>Sign in</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>

          <button
            type="button"
            onClick={fillDemoStudent}
            className="w-full py-2 px-3 rounded-lg text-xs font-medium text-slate-700 bg-slate-50 border border-slate-200 hover:bg-slate-100 transition"
          >
            Use demo student account (student@test.com)
          </button>
        </form>

        <div className="pt-4 border-t border-slate-100 text-center">
          <p className="text-xs sm:text-sm text-slate-600">
            New to AptiCore?{' '}
            <Link to="/register" className="font-semibold text-blue-600 hover:text-blue-700">
              Create an account
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
