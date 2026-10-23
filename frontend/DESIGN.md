# Student Academic Management System - DESIGN.md

## Design System Overview

This document serves as the single source of truth for the visual design language, component architecture, and implementation guidelines for the Student Academic and Course Management System.

**Design Philosophy**: Modern, clean, professional academic aesthetic that prioritizes clarity, accessibility, and data density for administrative workflows.

---

## Color System

### Design Tokens (CSS Custom Properties)

```css
:root {
  /* BRAND COLORS */
  --color-brand-50: #eff6ff;
  --color-brand-100: #dbeafe;
  --color-brand-200: #bfdbfe;
  --color-brand-300: #93c5fd;
  --color-brand-400: #60a5fa;
  --color-brand-500: #3b82f6;
  --color-brand-600: #2563eb;
  --color-brand-700: #1d4ed8;
  --color-brand-800: #1e40af;
  --color-brand-900: #1e3a8a;
  --color-brand-950: #172554;

  /* NEUTRAL / GRAYSCALE */
  --color-neutral-0: #ffffff;
  --color-neutral-50: #fafafa;
  --color-neutral-100: #f5f5f5;
  --color-neutral-200: #e5e5e5;
  --color-neutral-300: #d4d4d4;
  --color-neutral-400: #a3a3a3;
  --color-neutral-500: #737373;
  --color-neutral-600: #525252;
  --color-neutral-700: #404040;
  --color-neutral-800: #262626;
  --color-neutral-900: #171717;
  --color-neutral-950: #0a0a0a;

  /* SEMANTIC COLORS - Success */
  --color-success-50: #f0fdf4;
  --color-success-100: #dcfce7;
  --color-success-500: #22c55e;
  --color-success-600: #16a34a;
  --color-success-700: #15803d;
  --color-success-900: #14532d;

  /* SEMANTIC COLORS - Warning */
  --color-warning-50: #fffbeb;
  --color-warning-100: #fef3c7;
  --color-warning-500: #f59e0b;
  --color-warning-600: #d97706;
  --color-warning-700: #b45309;
  --color-warning-900: #78350f;

  /* SEMANTIC COLORS - Error */
  --color-error-50: #fef2f2;
  --color-error-100: #fee2e2;
  --color-error-500: #ef4444;
  --color-error-600: #dc2626;
  --color-error-700: #b91c1c;
  --color-error-900: #7f1d1d;

  /* SEMANTIC COLORS - Info */
  --color-info-50: #f0f9ff;
  --color-info-100: #e0f2fe;
  --color-info-500: #0ea5e9;
  --color-info-600: #0284c7;
  --color-info-700: #0369a1;
  --color-info-900: #0c4a6e;

  /* FUNCTIONAL COLOR ALIASES */
  --color-bg-primary: var(--color-neutral-0);
  --color-bg-secondary: var(--color-neutral-50);
  --color-bg-tertiary: var(--color-neutral-100);
  --color-bg-inverse: var(--color-neutral-900);
  --color-bg-brand: var(--color-brand-50);
  --color-bg-brand-hover: var(--color-brand-100);

  --color-text-primary: var(--color-neutral-900);
  --color-text-secondary: var(--color-neutral-600);
  --color-text-tertiary: var(--color-neutral-400);
  --color-text-inverse: var(--color-neutral-0);
  --color-text-brand: var(--color-brand-600);
  --color-text-brand-hover: var(--color-brand-700);
  --color-text-on-brand: var(--color-neutral-0);
  --color-text-success: var(--color-success-700);
  --color-text-warning: var(--color-warning-700);
  --color-text-error: var(--color-error-700);
  --color-text-info: var(--color-info-700);

  --color-border-primary: var(--color-neutral-200);
  --color-border-secondary: var(--color-neutral-300);
  --color-border-brand: var(--color-brand-300);
  --color-border-focus: var(--color-brand-500);
  --color-border-error: var(--color-error-500);
  --color-border-success: var(--color-success-500);

  --color-interactive-primary: var(--color-brand-500);
  --color-interactive-primary-hover: var(--color-brand-600);
  --color-interactive-primary-active: var(--color-brand-700);
  --color-interactive-secondary: var(--color-neutral-100);
  --color-interactive-secondary-hover: var(--color-neutral-200);
  --color-interactive-secondary-active: var(--color-neutral-300);
  --color-interactive-ghost: transparent;
  --color-interactive-ghost-hover: var(--color-neutral-100);
}

[data-theme="dark"] {
  --color-bg-primary: var(--color-neutral-950);
  --color-bg-secondary: var(--color-neutral-900);
  --color-bg-tertiary: var(--color-neutral-800);
  --color-bg-inverse: var(--color-neutral-50);
  --color-bg-brand: var(--color-brand-950);
  --color-bg-brand-hover: var(--color-brand-900);

  --color-text-primary: var(--color-neutral-50);
  --color-text-secondary: var(--color-neutral-400);
  --color-text-tertiary: var(--color-neutral-500);
  --color-text-inverse: var(--color-neutral-950);
  --color-text-brand: var(--color-brand-400);
  --color-text-brand-hover: var(--color-brand-300);

  --color-border-primary: var(--color-neutral-800);
  --color-border-secondary: var(--color-neutral-700);
  --color-border-brand: var(--color-brand-800);

  --color-interactive-primary: var(--color-brand-400);
  --color-interactive-primary-hover: var(--color-brand-300);
  --color-interactive-primary-active: var(--color-brand-200);
  --color-interactive-secondary: var(--color-neutral-800);
  --color-interactive-secondary-hover: var(--color-neutral-700);
  --color-interactive-secondary-active: var(--color-neutral-600);
  --color-interactive-ghost-hover: var(--color-neutral-800);
}
```

### WCAG AA Contrast Compliance

| Combination | Ratio | Status |
|-------------|-------|--------|
| Primary Text on Primary BG | 16.9:1 | AAA |
| Secondary Text on Primary BG | 7.0:1 | AAA |
| Brand 500 on White | 4.5:1 | AA |
| White on Brand 600 | 4.7:1 | AA |
| Error 600 on White | 5.5:1 | AA |
| Success 600 on White | 4.7:1 | AA |

---

## Typography System

### Font Families

```css
:root {
  --font-family-sans: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  --font-family-mono: 'JetBrains Mono', 'Fira Code', ui-monospace, SFMono-Regular, Menlo, monospace;
  --font-family-display: 'Inter Tight', var(--font-family-sans);
}
```

### Type Scale (Mobile-first, Fluid)

```css
:root {
  --text-xs: clamp(0.7rem, 0.68rem + 0.1vw, 0.75rem);
  --text-sm: clamp(0.8125rem, 0.78rem + 0.16vw, 0.875rem);
  --text-base: clamp(0.9375rem, 0.89rem + 0.24vw, 1rem);
  --text-lg: clamp(1.0625rem, 1.0rem + 0.31vw, 1.125rem);
  --text-xl: clamp(1.2rem, 1.12rem + 0.4vw, 1.25rem);
  --text-2xl: clamp(1.4rem, 1.28rem + 0.6vw, 1.5rem);
  --text-3xl: clamp(1.7rem, 1.52rem + 0.9vw, 1.875rem);
  --text-4xl: clamp(2.1rem, 1.85rem + 1.25vw, 2.25rem);
  --text-5xl: clamp(2.6rem, 2.25rem + 1.75vw, 3rem);

  --leading-tight: 1.1;
  --leading-snug: 1.375;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;
  --leading-loose: 2;

  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
}
```

### Semantic Typography Tokens

```css
:root {
  --text-display: var(--text-4xl);
  --text-display-weight: var(--font-weight-bold);
  --text-display-leading: var(--leading-tight);

  --text-h1: var(--text-3xl);
  --text-h1-weight: var(--font-weight-bold);
  --text-h1-leading: var(--leading-tight);

  --text-h2: var(--text-2xl);
  --text-h2-weight: var(--font-weight-semibold);
  --text-h2-leading: var(--leading-snug);

  --text-h3: var(--text-xl);
  --text-h3-weight: var(--font-weight-semibold);
  --text-h3-leading: var(--leading-snug);

  --text-title: var(--text-lg);
  --text-title-weight: var(--font-weight-semibold);
  --text-title-leading: var(--leading-normal);

  --text-body: var(--text-base);
  --text-body-weight: var(--font-weight-normal);
  --text-body-leading: var(--leading-relaxed);

  --text-body-sm: var(--text-sm);
  --text-body-sm-weight: var(--font-weight-normal);
  --text-body-sm-leading: var(--leading-normal);

  --text-label: var(--text-sm);
  --text-label-weight: var(--font-weight-medium);
  --text-label-leading: var(--leading-normal);

  --text-caption: var(--text-xs);
  --text-caption-weight: var(--font-weight-normal);
  --text-caption-leading: var(--leading-normal);

  --text-data: var(--text-sm);
  --text-data-weight: var(--font-weight-medium);
  --text-data-family: var(--font-family-mono);
  --text-data-leading: var(--leading-normal);

  --text-button: var(--text-sm);
  --text-button-weight: var(--font-weight-medium);
}
```

---

## Spacing System

### Base Unit: 4px (0.25rem)

```css
:root {
  --space-0: 0;
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-5: 1.25rem;
  --space-6: 1.5rem;
  --space-7: 1.75rem;
  --space-8: 2rem;
  --space-10: 2.5rem;
  --space-12: 3rem;
  --space-14: 3.5rem;
  --space-16: 4rem;
  --space-20: 5rem;
  --space-24: 6rem;
  --space-32: 8rem;
}
```

### Semantic Spacing Tokens

```css
:root {
  --padding-xs: var(--space-1);
  --padding-sm: var(--space-2);
  --padding-md: var(--space-3);
  --padding-lg: var(--space-4);
  --padding-xl: var(--space-6);
  --padding-2xl: var(--space-8);

  --gap-xs: var(--space-1);
  --gap-sm: var(--space-2);
  --gap-md: var(--space-4);
  --gap-lg: var(--space-6);
  --gap-xl: var(--space-8);

  --page-padding-mobile: var(--space-4);
  --page-padding-tablet: var(--space-6);
  --page-padding-desktop: var(--space-8);

  --container-sm: 640px;
  --container-md: 768px;
  --container-lg: 1024px;
  --container-xl: 1280px;
  --container-2xl: 1536px;
}
```

---

## Border Radius System

```css
:root {
  --radius-none: 0;
  --radius-xs: 0.125rem;
  --radius-sm: 0.25rem;
  --radius-md: 0.375rem;
  --radius-lg: 0.5rem;
  --radius-xl: 0.75rem;
  --radius-2xl: 1rem;
  --radius-full: 9999px;
}
```

---

## Shadow & Elevation System

```css
:root {
  --shadow-xs: 0 1px 2px 0 rgb(0 0 0 / 0.03);
  --shadow-sm: 0 1px 3px 0 rgb(0 0 0 / 0.06), 0 1px 2px -1px rgb(0 0 0 / 0.06);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.07), 0 2px 4px -2px rgb(0 0 0 / 0.07);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.08), 0 4px 6px -4px rgb(0 0 0 / 0.08);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
  --shadow-2xl: 0 25px 50px -12px rgb(0 0 0 / 0.15);
  --shadow-focus: 0 0 0 3px var(--color-brand-500 / 0.25);
  --shadow-focus-error: 0 0 0 3px var(--color-error-500 / 0.25);
  --shadow-inset: inset 0 2px 4px 0 rgb(0 0 0 / 0.05);
}
```

---

## Motion & Animation System

```css
:root {
  --duration-instant: 0ms;
  --duration-fast: 100ms;
  --duration-normal: 200ms;
  --duration-slow: 300ms;
  --duration-slower: 500ms;

  --ease-linear: linear;
  --ease-in: cubic-bezier(0.4, 0, 1, 1);
  --ease-out: cubic-bezier(0, 0, 0.2, 1);
  --ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);
  --ease-spring: cubic-bezier(0.34, 1.56, 0.64, 1);
  --ease-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);

  --transition-fast: var(--duration-fast) var(--ease-out);
  --transition-normal: var(--duration-normal) var(--ease-out);
  --transition-slow: var(--duration-slow) var(--ease-out);
  --transition-spring: var(--duration-slower) var(--ease-spring);

  --transition-colors: color var(--duration-fast) var(--ease-out), background-color var(--duration-fast) var(--ease-out), border-color var(--duration-fast) var(--ease-out);
  --transition-transform: transform var(--duration-normal) var(--ease-out);
  --transition-opacity: opacity var(--duration-fast) var(--ease-out);
  --transition-shadow: box-shadow var(--duration-normal) var(--ease-out);
  --transition-all: all var(--duration-normal) var(--ease-out);
}

@media (prefers-reduced-motion: reduce) {
  :root {
    --duration-fast: 0ms;
    --duration-normal: 0ms;
    --duration-slow: 0ms;
    --duration-slower: 0ms;
  }
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}
```

---

## Component Library Architecture

```
src/components/
├── ui/
│   ├── Button/
│   ├── Input/
│   ├── Textarea/
│   ├── Select/
│   ├── Checkbox/
│   ├── Radio/
│   ├── Switch/
│   ├── Label/
│   ├── Card/
│   ├── Badge/
│   ├── Avatar/
│   ├── Dropdown/
│   ├── Modal/
│   ├── Toast/
│   ├── Tooltip/
│   ├── Table/
│   ├── Pagination/
│   ├── Tabs/
│   ├── Accordion/
│   ├── Divider/
│   ├── Spinner/
│   ├── Skeleton/
│   └── index.ts
├── layout/
│   ├── Sidebar/
│   ├── Header/
│   ├── Breadcrumbs/
│   ├── Container/
│   ├── Grid/
│   ├── Stack/
│   └── index.ts
├── navigation/
│   ├── NavLink/
│   ├── NavMenu/
│   ├── Breadcrumb/
│   ├── Pagination/
│   ├── Stepper/
│   └── index.ts
├── data-display/
│   ├── DataTable/
│   ├── StatCard/
│   ├── Progress/
│   ├── Chart/
│   └── index.ts
├── forms/
│   ├── FormField/
│   ├── FormSection/
│   ├── FieldArray/
│   ├── DatePicker/
│   ├── FileUpload/
│   ├── RichTextEditor/
│   └── index.ts
├── feedback/
│   ├── Alert/
│   ├── Toast/
│   ├── Modal/
│   ├── ConfirmDialog/
│   ├── EmptyState/
│   ├── ErrorBoundary/
│   └── index.ts
└── index.ts
```

---

## Component Specifications

### Button Component

**Variants**: primary, secondary, outline, ghost, danger, success
**Sizes**: xs, sm, md, lg, xl
**States**: Default, Hover, Active, Focus, Focus-visible, Disabled, Loading

### Input Component

**Variants**: default, filled, outlined
**Sizes**: sm, md, lg
**States**: Default, Hover, Focus, Focus-visible, Disabled, Invalid, Valid, Loading

### Table Component (Data-Dense)

**Features**: Sorting, filtering, pagination, row selection, striped rows, hover states, loading states, empty states

### Card Component

**Variants**: default, elevated, outlined, filled, interactive
**Sections**: header, body, footer

### Badge Component

**Variants**: default, primary, success, warning, error, info, outline
**Sizes**: sm, md, lg

### Modal Component

**Sizes**: sm, md, lg, xl, full
**Sections**: header, body, footer
**Features**: Focus trap, ESC to close, click overlay to close

---

## Responsive Breakpoint System

```css
:root {
  --breakpoint-sm: 640px;
  --breakpoint-md: 768px;
  --breakpoint-lg: 1024px;
  --breakpoint-xl: 1280px;
  --breakpoint-2xl: 1536px;
}
```

### Responsive Behavior Guidelines

| Component | Mobile (<640px) | Tablet (640-1023px) | Desktop (1024px+) |
|-----------|-----------------|---------------------|-------------------|
| Sidebar | Drawer (off-canvas) | Collapsible (64px) | Full (260px) |
| Tables | Horizontal scroll | Full with sticky cols | Full |
| Forms | Single column | 2-column grid | 2-3 column grid |
| Cards | Single column stack | 2-column grid | 3-4 column grid |
| Modals | Full screen | Centered, max-w-md | Centered, max-w-lg |
| Page Padding | 16px | 24px | 32px |

---

## Accessibility Standards (WCAG 2.2 AA)

1. **Color Contrast**: Minimum 4.5:1 for normal text, 3:1 for large text
2. **Keyboard Navigation**: All interactive elements reachable and operable via keyboard
3. **Focus Indicators**: Visible focus styles on all interactive elements (3px ring minimum)
4. **Semantic HTML**: Proper heading hierarchy (h1-h6), landmarks, ARIA roles
5. **Form Labels**: Every input has an associated label or aria-label
6. **Error Handling**: Errors announced via aria-live, linked to inputs via aria-describedby
7. **Touch Targets**: Minimum 44x44px for touch interfaces
8. **Reduced Motion**: Respect prefers-reduced-motion
9. **Language**: lang attribute on html, lang on foreign phrases
10. **Zoom**: Content functional at 200% zoom without horizontal scrolling

---

## Role-Based UI Variations

### Navigation by Role

```typescript
const roleNavigation = {
  ADMIN: [
    { label: 'Dashboard', href: '/dashboard', icon: 'HomeIcon' },
    { label: 'Students', href: '/students', icon: 'UsersIcon' },
    { label: 'Faculty', href: '/faculty', icon: 'AcademicCapIcon' },
    { label: 'Courses', href: '/courses', icon: 'BookOpenIcon' },
    { label: 'Enrollments', href: '/enrollments', icon: 'ClipboardDocumentListIcon' },
    { label: 'Attendance', href: '/attendance', icon: 'CalendarDaysIcon' },
    { label: 'Assessments', href: '/assessments', icon: 'DocumentTextIcon' },
    { label: 'Marks', href: '/marks', icon: 'StarIcon' },
    { label: 'Reports', href: '/reports', icon: 'ChartBarIcon' },
    { label: 'Settings', href: '/settings', icon: 'Cog6ToothIcon' },
  ],
  FACULTY: [
    { label: 'Dashboard', href: '/dashboard', icon: 'HomeIcon' },
    { label: 'My Courses', href: '/courses', icon: 'BookOpenIcon' },
    { label: 'Enrollments', href: '/enrollments', icon: 'ClipboardDocumentListIcon' },
    { label: 'Attendance', href: '/attendance', icon: 'CalendarDaysIcon' },
    { label: 'Assessments', href: '/assessments', icon: 'DocumentTextIcon' },
    { label: 'Marks', href: '/marks', icon: 'StarIcon' },
    { label: 'Reports', href: '/reports', icon: 'ChartBarIcon' },
  ],
  DEPARTMENT_HEAD: [
    { label: 'Dashboard', href: '/dashboard', icon: 'HomeIcon' },
    { label: 'Students', href: '/students', icon: 'UsersIcon' },
    { label: 'Faculty', href: '/faculty', icon: 'AcademicCapIcon' },
    { label: 'Courses', href: '/courses', icon: 'BookOpenIcon' },
    { label: 'Enrollments', href: '/enrollments', icon: 'ClipboardDocumentListIcon' },
    { label: 'Reports', href: '/reports', icon: 'ChartBarIcon' },
  ],
  REGISTRAR: [
    { label: 'Dashboard', href: '/dashboard', icon: 'HomeIcon' },
    { label: 'Students', href: '/students', icon: 'UsersIcon' },
    { label: 'Enrollments', href: '/enrollments', icon: 'ClipboardDocumentListIcon' },
    { label: 'Courses', href: '/courses', icon: 'BookOpenIcon' },
    { label: 'Reports', href: '/reports', icon: 'ChartBarIcon' },
  ],
  STUDENT: [
    { label: 'Dashboard', href: '/dashboard', icon: 'HomeIcon' },
    { label: 'My Courses', href: '/courses', icon: 'BookOpenIcon' },
    { label: 'Attendance', href: '/attendance', icon: 'CalendarDaysIcon' },
    { label: 'Assessments', href: '/assessments', icon: 'DocumentTextIcon' },
    { label: 'Marks', href: '/marks', icon: 'StarIcon' },
    { label: 'Profile', href: '/profile', icon: 'UserIcon' },
  ],
};
```

---

## Dark Mode Implementation

**Strategy**: CSS Custom Properties for all colors, class-based toggle on html element [data-theme="dark"], system preference detection, localStorage persistence.

---

## Implementation Guidelines

1. **Composition over inheritance**: Build complex components by composing primitives
2. **Forward refs**: All interactive components must forwardRef
3. **TypeScript first**: Strict types for all props, no any
4. **Accessibility built-in**: ARIA attributes, keyboard handling, focus management
5. **Design token usage**: Only use CSS custom properties, no hardcoded values
6. **Responsive by default**: Mobile-first, test at all breakpoints
5. **Dark mode ready**: Test all components in both themes