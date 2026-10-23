import { useNavigate, useLocation } from 'react-router-dom';
import { Card, Button, Badge } from '@/components/ui';
import { useAuth } from '@/contexts';
import { ShieldExclamationIcon, ArrowLeftIcon, LockClosedIcon } from '@heroicons/react/24/outline';
import { UserRole } from '@/types';

interface AccessDeniedPageProps {
  requiredRoles?: UserRole[];
}

export function AccessDeniedPage({ requiredRoles }: AccessDeniedPageProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  return (
    <div className="min-h-[70vh] flex items-center justify-center p-4">
      <Card className="max-w-lg w-full p-6 sm:p-8 text-center border-rose-500/30 dark:border-rose-900 shadow-2xl relative overflow-hidden">
        {/* Ambient Security Warning Glow */}
        <div className="absolute -top-12 left-1/2 -translate-x-1/2 w-48 h-48 bg-rose-500/10 rounded-full blur-2xl pointer-events-none" />

        <div className="mx-auto w-16 h-16 rounded-2xl bg-rose-100 dark:bg-rose-950/60 border border-rose-200 dark:border-rose-800 flex items-center justify-center text-rose-600 dark:text-rose-400 mb-4 shadow-md">
          <ShieldExclamationIcon className="h-8 w-8" />
        </div>

        <Badge variant="danger" size="sm" className="font-mono mb-2">
          HTTP 403 FORBIDDEN • SECURITY ENFORCEMENT
        </Badge>

        <h1 className="text-xl sm:text-2xl font-black text-slate-900 dark:text-slate-100 tracking-tight">
          Access Denied — Insufficient Role Clearance
        </h1>

        <p className="mt-2 text-xs sm:text-sm text-slate-600 dark:text-slate-400 max-w-sm mx-auto">
          Manual URL manipulation or direct navigation cannot elevate privileges. This resource is strictly restricted by backend Spring Security filters.
        </p>

        {/* Audit Details Box */}
        <div className="mt-5 p-3.5 rounded-xl bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-left text-xs space-y-1.5 font-mono">
          <div className="flex justify-between">
            <span className="text-slate-400">Attempted Route:</span>
            <span className="text-rose-500 font-bold">{location.pathname}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-400">Your Identity:</span>
            <span className="text-slate-800 dark:text-slate-200">{user?.username} ({user?.fullName})</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-400">Your Assigned Role:</span>
            <span className="text-amber-500 font-bold">{user?.role}</span>
          </div>
          {requiredRoles && requiredRoles.length > 0 && (
            <div className="flex justify-between">
              <span className="text-slate-400">Required Role(s):</span>
              <span className="text-emerald-500 font-bold">{requiredRoles.join(' • ')}</span>
            </div>
          )}
        </div>

        <div className="mt-6 flex flex-col sm:flex-row items-center justify-center gap-3">
          <Button
            variant="primary"
            fullWidth
            onClick={() => navigate('/dashboard')}
            className="bg-brand-600 hover:bg-brand-700"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-1.5" />
            Return to My Authorized Dashboard
          </Button>
        </div>

        <p className="mt-4 text-[11px] text-slate-400 flex items-center justify-center gap-1 font-mono">
          <LockClosedIcon className="h-3.5 w-3.5" />
          Zero-Trust RBAC • Incident Logged to Audit Trail
        </p>
      </Card>
    </div>
  );
}
