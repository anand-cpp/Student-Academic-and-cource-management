import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { User, UserRole } from '@/types';
import { api } from '@/services/api';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (usernameOrEmail: string, password: string) => Promise<void>;
  logout: () => void;
  hasRole: (roles: UserRole[]) => boolean;
  updateUser: (user: Partial<User>) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('accessToken');
      const storedUser = localStorage.getItem('user');

      if (!storedToken || !storedUser) {
        if (mounted) {
          setUser(null);
          setIsLoading(false);
        }
        return;
      }

      try {
        const res = await fetch('/api/auth/me', {
          headers: {
            Authorization: `Bearer ${storedToken}`,
          },
        });

        if (res.ok) {
          const verifiedUser = await res.json();
          if (mounted) {
            const finalUser: User = {
              id: String(verifiedUser.id),
              email: verifiedUser.email,
              username: verifiedUser.username,
              fullName: verifiedUser.fullName,
              firstName: verifiedUser.fullName?.split(' ')[0] || 'User',
              lastName: verifiedUser.fullName?.split(' ').slice(1).join(' ') || '',
              role: verifiedUser.role,
              studentId: verifiedUser.studentId,
              facultyId: verifiedUser.facultyId,
            };
            setUser(finalUser);
            localStorage.setItem('user', JSON.stringify(finalUser));
          }
        } else if (res.status === 401 || res.status === 403) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          if (mounted) setUser(null);
        } else {
          if (mounted) setUser(JSON.parse(storedUser));
        }
      } catch {
        try {
          if (mounted) setUser(JSON.parse(storedUser));
        } catch {
          localStorage.removeItem('user');
          if (mounted) setUser(null);
        }
      } finally {
        if (mounted) setIsLoading(false);
      }
    };

    initializeAuth();
    return () => {
      mounted = false;
    };
  }, []);

  const login = useCallback(async (usernameOrEmail: string, password: string) => {
    const loggedInUser = await api.auth.login(usernameOrEmail, password);
    setUser(loggedInUser);
  }, []);

  const logout = useCallback(() => {
    api.auth.logout();
    setUser(null);
  }, []);

  const hasRole = useCallback(
    (roles: UserRole[]) => {
      if (!user) return false;
      return roles.includes(user.role);
    },
    [user]
  );

  const updateUser = useCallback(
    (updates: Partial<User>) => {
      if (user) {
        const updated = { ...user, ...updates };
        setUser(updated);
        localStorage.setItem('user', JSON.stringify(updated));
      }
    },
    [user]
  );

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        logout,
        hasRole,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

export default AuthContext;
