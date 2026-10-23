import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter, Button, Badge, Avatar } from '@/components/ui';
import { useAuth, useToast } from '@/contexts';
import {
  UserCircleIcon,
  KeyIcon,
  ShieldCheckIcon,
  EnvelopeIcon,
  BuildingOfficeIcon,
  CheckCircleIcon,
  PhoneIcon,
  MapPinIcon,
} from '@heroicons/react/24/outline';

export function ProfilePage() {
  const { user, updateUser } = useAuth();
  const { showToast } = useToast();

  const [firstName, setFirstName] = useState(user?.firstName || 'User');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [email] = useState(user?.email || '');
  const [phone, setPhone] = useState('+1 (555) 019-2834');
  const [officeLocation, setOfficeLocation] = useState('Science Building, Room 402');
  const [savingProfile, setSavingProfile] = useState(false);

  // Password fields
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [savingPassword, setSavingPassword] = useState(false);

  const handleUpdateProfile = (e: React.FormEvent) => {
    e.preventDefault();
    setSavingProfile(true);
    setTimeout(() => {
      updateUser({ firstName, lastName });
      setSavingProfile(false);
      showToast({
        type: 'success',
        title: 'Profile Updated',
        message: 'Your personal information has been successfully saved.',
      });
    }, 400);
  };

  const handleChangePassword = (e: React.FormEvent) => {
    e.preventDefault();
    if (newPassword.length < 6) {
      showToast({
        type: 'error',
        title: 'Validation Error',
        message: 'New password must be at least 6 characters.',
      });
      return;
    }
    if (newPassword !== confirmPassword) {
      showToast({
        type: 'error',
        title: 'Mismatch',
        message: 'New password and confirmation do not match.',
      });
      return;
    }

    setSavingPassword(true);
    setTimeout(() => {
      setSavingPassword(false);
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      showToast({
        type: 'success',
        title: 'Password Changed',
        message: 'Security credentials updated successfully.',
      });
    }, 400);
  };

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Header Profile Banner */}
      <Card className="overflow-hidden border-none shadow-md bg-gradient-to-r from-brand-900 via-indigo-950 to-slate-900 text-white p-6 sm:p-8">
        <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6">
          <Avatar
            name={`${firstName} ${lastName}`}
            size="xl"
            className="ring-4 ring-white/20 shadow-xl"
          />
          <div className="text-center sm:text-left flex-1">
            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2.5">
              <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
                {firstName} {lastName}
              </h1>
              <Badge variant="brand" size="sm" className="bg-white/10 text-white border-white/20">
                {user?.role?.replace('_', ' ')}
              </Badge>
            </div>
            <p className="text-indigo-200 text-sm mt-1 flex items-center justify-center sm:justify-start gap-1.5">
              <EnvelopeIcon className="h-4 w-4" /> {email}
            </p>
            <p className="text-xs text-indigo-300/80 mt-2">
              Member since Jan 2024 • Academic Portal Access
            </p>
          </div>
        </div>
      </Card>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Left Column (2 spans): Details & Password */}
        <div className="lg:col-span-2 space-y-6">
          {/* Personal Information */}
          <Card>
            <form onSubmit={handleUpdateProfile}>
              <CardHeader>
                <div className="flex items-center gap-2">
                  <UserCircleIcon className="h-5 w-5 text-brand-600" />
                  <CardTitle>Personal Information</CardTitle>
                </div>
                <CardDescription>Update your contact and identification details.</CardDescription>
              </CardHeader>

              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      First Name
                    </label>
                    <input
                      type="text"
                      value={firstName}
                      onChange={(e) => setFirstName(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Last Name
                    </label>
                    <input
                      type="text"
                      value={lastName}
                      onChange={(e) => setLastName(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Email Address
                    </label>
                    <input
                      type="email"
                      value={email}
                      disabled
                      className="w-full px-3.5 py-2 text-sm bg-slate-100 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 rounded-xl text-slate-500 cursor-not-allowed"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Phone Number
                    </label>
                    <input
                      type="tel"
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                    Campus Office / Address
                  </label>
                  <input
                    type="text"
                    value={officeLocation}
                    onChange={(e) => setOfficeLocation(e.target.value)}
                    className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                  />
                </div>
              </CardContent>

              <CardFooter>
                <Button type="submit" loading={savingProfile}>
                  Save Changes
                </Button>
              </CardFooter>
            </form>
          </Card>

          {/* Change Password */}
          <Card>
            <form onSubmit={handleChangePassword}>
              <CardHeader>
                <div className="flex items-center gap-2">
                  <KeyIcon className="h-5 w-5 text-indigo-600" />
                  <CardTitle>Change Password</CardTitle>
                </div>
                <CardDescription>Ensure your account uses a strong, unique password.</CardDescription>
              </CardHeader>

              <CardContent className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                    Current Password
                  </label>
                  <input
                    type="password"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    placeholder="Enter existing password"
                    className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                    required
                  />
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      New Password
                    </label>
                    <input
                      type="password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="Minimum 6 characters"
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Confirm New Password
                    </label>
                    <input
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      placeholder="Re-type new password"
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                      required
                    />
                  </div>
                </div>
              </CardContent>

              <CardFooter>
                <Button type="submit" loading={savingPassword} variant="primary">
                  Update Password
                </Button>
              </CardFooter>
            </form>
          </Card>
        </div>

        {/* Right Column (1 span): Role Permissions & Security Info */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <ShieldCheckIcon className="h-5 w-5 text-emerald-500" />
                <CardTitle>Role Permissions</CardTitle>
              </div>
            </CardHeader>

            <CardContent className="space-y-2.5 text-xs">
              <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800">
                <span className="font-bold text-slate-800 dark:text-slate-200 block mb-1">
                  Active Role: {user?.role}
                </span>
                <p className="text-slate-500 dark:text-slate-400">
                  You have authorized administrative access to manage courses, enrollments, grading policies, and academic transcripts.
                </p>
              </div>

              <div className="space-y-2 pt-2">
                <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                  <CheckCircleIcon className="h-4 w-4 text-emerald-500 shrink-0" />
                  <span>Student records read & write</span>
                </div>
                <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                  <CheckCircleIcon className="h-4 w-4 text-emerald-500 shrink-0" />
                  <span>Course catalog editing</span>
                </div>
                <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                  <CheckCircleIcon className="h-4 w-4 text-emerald-500 shrink-0" />
                  <span>Attendance management</span>
                </div>
                <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                  <CheckCircleIcon className="h-4 w-4 text-emerald-500 shrink-0" />
                  <span>Grade entry & evaluation</span>
                </div>
                <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
                  <CheckCircleIcon className="h-4 w-4 text-emerald-500 shrink-0" />
                  <span>Official transcript generation</span>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <BuildingOfficeIcon className="h-5 w-5 text-indigo-500" />
                <CardTitle>Department Affiliation</CardTitle>
              </div>
            </CardHeader>

            <CardContent className="text-xs text-slate-600 dark:text-slate-400 space-y-2">
              <p className="font-semibold text-slate-800 dark:text-slate-200">
                Department of Computer Science & Engineering
              </p>
              <p>Faculty & Academic Staff Member</p>
              <p className="text-[11px] text-slate-400">
                Contact the Registrar if you require department assignment modification.
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

export default ProfilePage;
