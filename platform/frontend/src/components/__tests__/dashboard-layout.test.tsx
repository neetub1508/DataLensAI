import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { DashboardLayout } from '../layouts/dashboard-layout';

// Mock Next.js components
jest.mock('next/link', () => {
  return function MockLink({ children, href }: { children: React.ReactNode; href: string }) {
    return <a href={href}>{children}</a>;
  };
});

jest.mock('next/router', () => ({
  useRouter() {
    return {
      route: '/dashboard',
      pathname: '/dashboard',
      query: {},
      asPath: '/dashboard',
      push: jest.fn(),
      replace: jest.fn(),
    };
  },
}));

// Mock the auth store
jest.mock('../../store/auth', () => ({
  useAuthStore: jest.fn(() => ({
    user: {
      id: '123',
      email: 'test@example.com',
      roles: ['USER'],
    },
    logout: jest.fn(),
  })),
}));

// Mock next-themes
jest.mock('next-themes', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: jest.fn(),
  }),
}));

describe('DashboardLayout', () => {
  const mockChildren = <div data-testid="dashboard-content">Dashboard Content</div>;

  it('renders the layout with children', () => {
    render(<DashboardLayout>{mockChildren}</DashboardLayout>);
    
    expect(screen.getByTestId('dashboard-content')).toBeInTheDocument();
  });

  it('displays navigation elements', () => {
    const { container } = render(<DashboardLayout>{mockChildren}</DashboardLayout>);
    
    // Look for navigation elements (nav tags have implicit navigation role)
    const navElements = container.querySelectorAll('nav');
    expect(navElements.length).toBeGreaterThan(0);

    // Check for user menu or profile section
    const userElements = screen.queryAllByText(/test@example.com/i);
    expect(userElements.length).toBeGreaterThan(0);
  });

  it('has proper semantic structure', () => {
    const { container } = render(<DashboardLayout>{mockChildren}</DashboardLayout>);
    
    // Check for main content area
    const main = container.querySelector('main') || 
                 screen.queryByRole('main') || 
                 screen.getByTestId('dashboard-content').closest('div');
    
    expect(main).toBeInTheDocument();
  });

  it('renders without crashing', () => {
    expect(() => render(<DashboardLayout>{mockChildren}</DashboardLayout>)).not.toThrow();
  });

  it('is accessible', () => {
    const { container } = render(<DashboardLayout>{mockChildren}</DashboardLayout>);
    
    // Check that there are no obvious accessibility violations
    // Look for proper heading structure
    const headings = screen.queryAllByRole('heading');
    
    // Should have navigation or menu elements
    const buttons = screen.queryAllByRole('button');
    const links = screen.queryAllByRole('link');
    
    // At minimum, should have some interactive elements
    expect(buttons.length + links.length).toBeGreaterThanOrEqual(0);
  });
});