import { Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts';
import { getRoleWorkspacePath } from '@/utils';

/**
 * DashboardPage acts as an authoritative, role-aware dispatcher.
 * Rather than exposing predictable /admin, /student URLs, it resolves
 * the authenticated user's role to its designated opaque workspace route.
 */
export function DashboardPage() {
  const { user } = useAuth();
  return <Navigate to={getRoleWorkspacePath(user?.role)} replace />;
}

