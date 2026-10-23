# Student Academic Management System

A modern, comprehensive Student Academic and Course Management System built with React 18, TypeScript, Vite, and Tailwind CSS v4.

## Features

- **Role-Based Access Control**: ADMIN, FACULTY, STUDENT, DEPARTMENT_HEAD, REGISTRAR
- **Complete CRUD Operations**: Students, Faculty, Courses, Enrollments, Attendance, Assessments, Marks
- **Modern UI/UX**: Clean, professional academic aesthetic with dark mode support
- **Responsive Design**: Mobile-first approach with Tailwind CSS v4
- **Accessibility**: WCAG 2.2 AA compliant
- **Data Tables**: Sorting, filtering, pagination, row selection
- **Form Validation**: React Hook Form + Zod
- **State Management**: TanStack Query for server state
- **Toast Notifications**: Real-time feedback system

## Tech Stack

- **Frontend**: React 18 + TypeScript
- **Build Tool**: Vite 5
- **Styling**: Tailwind CSS v4
- **Routing**: React Router v6
- **Forms**: React Hook Form + Zod
- **Data Fetching**: TanStack Query v5
- **UI Components**: Headless UI + Heroicons
- **Utilities**: clsx + tailwind-merge

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

```bash
# Clone the repository
git clone <repository-url>
cd student-academic-system

# Install dependencies
npm install

# Start development server
npm run dev
```

### Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@university.edu | password123 |
| Faculty | faculty@university.edu | password123 |
| Student | student@university.edu | password123 |
| Dept Head | hod@university.edu | password123 |
| Registrar | registrar@university.edu | password123 |

## Project Structure

```
src/
├── components/
│   ├── ui/           # Primitive UI components (Button, Input, Table, Modal, etc.)
│   ├── layout/       # Layout components (Sidebar, Header, Breadcrumbs)
│   ├── navigation/   # Navigation components (NavLink, Stepper, Breadcrumbs)
│   ├── data-display/ # Data-heavy components (DataTable, StatCard)
│   ├── forms/        # Form components (FormField, DatePicker, FileUpload)
│   └── feedback/     # Feedback components (Alert, Toast, Modal, EmptyState)
├── contexts/         # React Context providers (Auth, Theme, Toast)
├── hooks/            # Custom React hooks
├── pages/            # Page components
├── services/         # API services
├── types/            # TypeScript type definitions
├── utils/            # Utility functions
├── App.tsx           # Main app with routing
└── main.tsx          # Entry point
```

## Design System

See [DESIGN.md](DESIGN.md) for complete design system documentation including:

- Color system with semantic tokens
- Typography scale
- Spacing system
- Component specifications
- Responsive breakpoints
- Accessibility standards
- Dark mode implementation
- Role-based UI variations

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## License

MIT