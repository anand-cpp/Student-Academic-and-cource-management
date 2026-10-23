import { UserRole } from '@/types';

/**
 * Stable, non-semantic, collision-free opaque workspace identifiers.
 * Prevents predictable role enumeration in URLs while preserving deep links and bookmarkability.
 * Crucial: Discovering or typing these routes cannot grant privileges; access is strictly
 * verified against server-authenticated roles on every navigation.
 */
export const OPAQUE_ROLE_ROUTES: Record<UserRole, string> = {
  ADMIN: '/app/w-adm89x',
  FACULTY: '/app/w-fac42m',
  STUDENT: '/app/w-stu17k',
  DEPARTMENT_HEAD: '/app/w-dpt63h',
  REGISTRAR: '/app/w-reg55r',
};

export function getRoleWorkspacePath(role?: UserRole): string {
  if (!role || !OPAQUE_ROLE_ROUTES[role]) {
    return OPAQUE_ROLE_ROUTES.ADMIN;
  }
  return OPAQUE_ROLE_ROUTES[role];
}
