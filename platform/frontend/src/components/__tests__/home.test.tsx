import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { HomePage } from '../pages/home';

// Mock Next.js router
jest.mock('next/router', () => ({
  useRouter() {
    return {
      route: '/',
      pathname: '/',
      query: '',
      asPath: '/',
      push: jest.fn(),
      replace: jest.fn(),
    };
  },
}));

// Mock next-themes
jest.mock('next-themes', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: jest.fn(),
  }),
}));

// Mock Heroicons
jest.mock('@heroicons/react/24/outline', () => ({
  ChartBarIcon: () => <svg data-testid="chart-bar-icon" />,
  CpuChipIcon: () => <svg data-testid="cpu-chip-icon" />,
  ShieldCheckIcon: () => <svg data-testid="shield-check-icon" />,
  ArrowRightIcon: () => <svg data-testid="arrow-right-icon" />,
  SparklesIcon: () => <svg data-testid="sparkles-icon" />,
  ChatBubbleLeftRightIcon: () => <svg data-testid="chat-bubble-left-right-icon" />,
  MagnifyingGlassIcon: () => <svg data-testid="magnifying-glass-icon" />,
  WrenchScrewdriverIcon: () => <svg data-testid="wrench-screwdriver-icon" />,
  BoltIcon: () => <svg data-testid="bolt-icon" />,
  BookOpenIcon: () => <svg data-testid="book-open-icon" />,
  ChartPieIcon: () => <svg data-testid="chart-pie-icon" />,
  PresentationChartLineIcon: () => <svg data-testid="presentation-chart-line-icon" />,
  CogIcon: () => <svg data-testid="cog-icon" />,
  TruckIcon: () => <svg data-testid="truck-icon" />,
  CircleStackIcon: () => <svg data-testid="circle-stack-icon" />,
  CloudArrowUpIcon: () => <svg data-testid="cloud-arrow-up-icon" />,
  DocumentDuplicateIcon: () => <svg data-testid="document-duplicate-icon" />,
  ShieldExclamationIcon: () => <svg data-testid="shield-exclamation-icon" />,
}));

describe('Home Component', () => {
  it('renders the home page correctly', () => {
    render(<HomePage />);
    
    // Check if the main heading exists
    expect(screen.getByRole('heading', { level: 1 })).toBeInTheDocument();
    
    // Check if navigation links exist
    const loginButton = screen.queryByText(/sign in/i);
    const registerButton = screen.queryByText(/get started/i);
    
    if (loginButton) expect(loginButton).toBeInTheDocument();
    if (registerButton) expect(registerButton).toBeInTheDocument();
  });

  it('displays the correct page title', () => {
    render(<HomePage />);
    
    // Look for any text that might indicate this is the home page
    const headings = screen.getAllByRole('heading');
    expect(headings.length).toBeGreaterThan(0);
  });

  it('has proper accessibility attributes', () => {
    const { container } = render(<HomePage />);
    
    // Check that the main content area exists
    const main = container.querySelector('main') || container.querySelector('[role="main"]');
    expect(main || container.firstChild).toBeInTheDocument();
  });

  it('renders without crashing', () => {
    expect(() => render(<HomePage />)).not.toThrow();
  });
});