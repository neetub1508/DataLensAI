import React from 'react'
import { render, RenderOptions } from '@testing-library/react'
import { ThemeProvider } from 'next-themes'

// Mock providers for testing
const TestProviders: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <ThemeProvider attribute="class" defaultTheme="light">
      {children}
    </ThemeProvider>
  )
}

// Custom render function that includes providers
const customRender = (ui: React.ReactElement, options?: RenderOptions) => {
  return render(ui, {
    wrapper: TestProviders,
    ...options,
  })
}

// Re-export everything
export * from '@testing-library/react'
export { customRender as render }

// Setup for Jest DOM matchers
import '@testing-library/jest-dom'