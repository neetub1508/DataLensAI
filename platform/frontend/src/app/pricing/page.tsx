'use client'

import Link from 'next/link'
import { CheckIcon } from '@heroicons/react/24/outline'
import { PublicLayout } from '@/components/layouts/public-layout'

const plans = [
  {
    name: 'Free',
    price: 0,
    description: 'Perfect for getting started with basic analytics',
    features: [
      'Up to 1,000 data points',
      'Basic visualizations',
      '5 dashboards',
      'Email support',
      'Community access',
      'Export to PDF'
    ],
    buttonText: 'Get Started Free',
    buttonHref: '/register',
    popular: false,
    buttonClass: 'bg-green-600 text-white hover:bg-green-700'
  },
  {
    name: 'Pro',
    price: 20,
    description: 'Advanced features for growing businesses',
    features: [
      'Up to 100,000 data points',
      'Advanced visualizations',
      'Unlimited dashboards',
      'AI-powered insights',
      'Priority email support',
      'Export to multiple formats',
      'API access',
      'Team collaboration (up to 5 users)',
      'Custom themes',
      'Advanced filters'
    ],
    buttonText: 'Start Pro Trial',
    buttonHref: '/register?plan=pro',
    popular: true,
    buttonClass: 'bg-white text-green-600 hover:bg-green-50'
  },
  {
    name: 'Max',
    price: 100,
    description: 'Enterprise-grade solution for large organizations',
    features: [
      'Unlimited data points',
      'All visualization types',
      'Unlimited dashboards',
      'Advanced AI & ML models',
      '24/7 phone & email support',
      'Custom integrations',
      'Full API access',
      'Unlimited team members',
      'White-label options',
      'Advanced security features',
      'Custom data connectors',
      'Dedicated account manager',
      'SLA guarantee',
      'On-premise deployment option'
    ],
    buttonText: 'Contact Sales',
    buttonHref: '/contact?plan=max',
    popular: false,
    buttonClass: 'bg-green-600 text-white hover:bg-green-700'
  }
]

export default function PricingPage() {
  return (
    <PublicLayout>
      <div className="bg-gray-50 dark:bg-gray-900">
      {/* Header Section */}
      <div className="bg-white dark:bg-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl md:text-5xl font-bold text-gray-900 dark:text-white mb-4">
              Simple, Transparent Pricing
            </h1>
            <p className="text-xl text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
              Choose the perfect plan for your analytics needs. Start free and scale as you grow.
            </p>
          </div>
        </div>
      </div>

      {/* Pricing Cards */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 lg:gap-12">
          {plans.map((plan) => (
            <div
              key={plan.name}
              className={`relative bg-white dark:bg-gray-800 rounded-2xl shadow-sm border ${
                plan.popular 
                  ? 'border-green-500 ring-2 ring-green-500 ring-opacity-50' 
                  : 'border-gray-200 dark:border-gray-700'
              }`}
            >
              {plan.popular && (
                <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                  <span className="bg-green-600 text-white px-4 py-2 rounded-full text-sm font-semibold">
                    Most Popular
                  </span>
                </div>
              )}
              
              <div className="p-8">
                <div className="text-center">
                  <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                    {plan.name}
                  </h3>
                  <div className="mb-4">
                    <span className="text-5xl font-bold text-gray-900 dark:text-white">
                      ${plan.price}
                    </span>
                    <span className="text-gray-600 dark:text-gray-400 text-lg">
                      /month
                    </span>
                  </div>
                  <p className="text-gray-600 dark:text-gray-400 mb-8">
                    {plan.description}
                  </p>
                </div>

                <ul className="space-y-4 mb-8">
                  {plan.features.map((feature, index) => (
                    <li key={index} className="flex items-start">
                      <CheckIcon className="h-5 w-5 text-green-500 mt-0.5 mr-3 flex-shrink-0" />
                      <span className="text-gray-700 dark:text-gray-300">
                        {feature}
                      </span>
                    </li>
                  ))}
                </ul>

                <Link
                  href={plan.buttonHref}
                  className={`w-full py-3 px-6 rounded-lg font-semibold text-center transition-colors block ${plan.buttonClass}`}
                >
                  {plan.buttonText}
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* FAQ Section */}
      <div className="bg-white dark:bg-gray-800">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-4">
              Frequently Asked Questions
            </h2>
          </div>
          
          <div className="space-y-8">
            <div>
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                Can I change plans anytime?
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Yes, you can upgrade or downgrade your plan at any time. Changes take effect immediately, and we'll prorate any charges.
              </p>
            </div>
            
            <div>
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                Is there a free trial for paid plans?
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Yes, both Pro and Max plans come with a 14-day free trial. No credit card required to start.
              </p>
            </div>
            
            <div>
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                What payment methods do you accept?
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                We accept all major credit cards, PayPal, and bank transfers for enterprise accounts.
              </p>
            </div>
            
            <div>
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                Do you offer discounts for annual plans?
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Yes, save 20% when you pay annually. Contact our sales team for custom enterprise pricing.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-green-600">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
          <h2 className="text-3xl font-bold text-white mb-4">
            Ready to Get Started?
          </h2>
          <p className="text-xl text-green-100 mb-8 max-w-2xl mx-auto">
            Join thousands of businesses using Data Lens AI to transform their data into insights.
          </p>
          <Link
            href="/register"
            className="bg-white text-green-600 px-8 py-4 rounded-lg font-semibold text-lg hover:bg-green-50 transition-colors inline-block"
          >
            Start Your Free Trial
          </Link>
        </div>
      </div>
      </div>
    </PublicLayout>
  )
}