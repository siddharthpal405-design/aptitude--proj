import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, StreakSummary } from '../types';
import api from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  streak: StreakSummary | null;
  loading: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  refreshStreak: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [streak, setStreak] = useState<StreakSummary | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchStreak = async () => {
    try {
      const res = await api.get<StreakSummary>('/user/streak');
      setStreak(res.data);
    } catch (e) {
      console.error('Failed to load streak', e);
    }
  };

  useEffect(() => {
    const storedToken = localStorage.getItem('apti_token');
    const storedUser = localStorage.getItem('apti_user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
      fetchStreak();
    }
    setLoading(false);
  }, []);

  const login = (newToken: string, newUser: User) => {
    localStorage.setItem('apti_token', newToken);
    localStorage.setItem('apti_user', JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
    fetchStreak();
  };

  const logout = () => {
    localStorage.removeItem('apti_token');
    localStorage.removeItem('apti_user');
    setToken(null);
    setUser(null);
    setStreak(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        streak,
        loading,
        login,
        logout,
        refreshStreak: fetchStreak,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
