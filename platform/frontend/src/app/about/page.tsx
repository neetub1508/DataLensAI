'use client'

import { PublicLayout } from '@/components/layouts/public-layout'
import { 
  UserGroupIcon,
  LightBulbIcon,
  HeartIcon,
  GlobeAltIcon,
  ShieldCheckIcon,
  SparklesIcon,
  ChartBarIcon,
  CogIcon
} from '@heroicons/react/24/outline'

export default function AboutPage() {
  return (
    <PublicLayout>
      <div className="min-h-screen">
        {/* Hero Section */}
        <section className="relative bg-gradient-to-r from-green-600 to-green-800 text-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
            <div className="text-center">
              <h1 className="text-4xl md:text-6xl font-bold mb-6">
                About Data Lens AI
              </h1>
              <p className="text-xl md:text-2xl mb-8 text-green-100 max-w-3xl mx-auto">
                We're on a mission to democratize data analytics and make AI-powered insights accessible to every organization.
              </p>
            </div>
          </div>
        </section>

        {/* Mission Section */}
        <section className="py-24 bg-white dark:bg-gray-800">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
              <div>
                <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-6">
                  Our Mission
                </h2>
                <p className="text-lg text-gray-600 dark:text-gray-400 mb-6">
                  Data Lens AI was founded with a simple yet powerful vision: to transform how organizations understand and leverage their data. We believe that every business, regardless of size or technical expertise, should have access to enterprise-grade analytics and AI-powered insights.
                </p>
                <p className="text-lg text-gray-600 dark:text-gray-400">
                  Our platform combines cutting-edge artificial intelligence with intuitive design, making complex data analysis as simple as asking a question. We're committed to empowering decision-makers with the tools they need to drive meaningful business outcomes.
                </p>
              </div>
              <div className="relative">
                <div className="bg-gradient-to-br from-green-100 to-green-200 dark:from-green-900 dark:to-green-800 rounded-2xl p-8">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 text-center">
                      <ChartBarIcon className="h-8 w-8 text-green-600 mx-auto mb-2" />
                      <div className="text-2xl font-bold text-gray-900 dark:text-white">10K+</div>
                      <div className="text-sm text-gray-600 dark:text-gray-400">Data Points</div>
                    </div>
                    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 text-center">
                      <UserGroupIcon className="h-8 w-8 text-green-600 mx-auto mb-2" />
                      <div className="text-2xl font-bold text-gray-900 dark:text-white">500+</div>
                      <div className="text-sm text-gray-600 dark:text-gray-400">Organizations</div>
                    </div>
                    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 text-center">
                      <GlobeAltIcon className="h-8 w-8 text-green-600 mx-auto mb-2" />
                      <div className="text-2xl font-bold text-gray-900 dark:text-white">50+</div>
                      <div className="text-sm text-gray-600 dark:text-gray-400">Countries</div>
                    </div>
                    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 text-center">
                      <SparklesIcon className="h-8 w-8 text-green-600 mx-auto mb-2" />
                      <div className="text-2xl font-bold text-gray-900 dark:text-white">99.9%</div>
                      <div className="text-sm text-gray-600 dark:text-gray-400">Uptime</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Values Section */}
        <section className="py-24 bg-gray-50 dark:bg-gray-900">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16">
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
                Our Values
              </h2>
              <p className="text-xl text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
                The principles that guide everything we do at Data Lens AI.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center mb-6">
                  <LightBulbIcon className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Innovation First
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  We constantly push the boundaries of what's possible with AI and data analytics, staying ahead of industry trends to deliver cutting-edge solutions.
                </p>
              </div>

              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center mb-6">
                  <ShieldCheckIcon className="h-6 w-6 text-green-600 dark:text-green-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Security & Privacy
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  Your data security is our top priority. We implement enterprise-grade security measures and maintain the highest standards of data privacy.
                </p>
              </div>

              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900 rounded-lg flex items-center justify-center mb-6">
                  <HeartIcon className="h-6 w-6 text-purple-600 dark:text-purple-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Customer Success
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  Our success is measured by yours. We're committed to providing exceptional support and ensuring every customer achieves their data goals.
                </p>
              </div>

              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900 rounded-lg flex items-center justify-center mb-6">
                  <UserGroupIcon className="h-6 w-6 text-orange-600 dark:text-orange-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Collaboration
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  We believe in the power of teamwork and collaboration, both within our organization and with our customers and partners.
                </p>
              </div>

              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-red-100 dark:bg-red-900 rounded-lg flex items-center justify-center mb-6">
                  <GlobeAltIcon className="h-6 w-6 text-red-600 dark:text-red-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Global Impact
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  We strive to make a positive impact on businesses and communities worldwide through democratized access to powerful data insights.
                </p>
              </div>

              <div className="bg-white dark:bg-gray-800 p-8 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
                <div className="w-12 h-12 bg-indigo-100 dark:bg-indigo-900 rounded-lg flex items-center justify-center mb-6">
                  <CogIcon className="h-6 w-6 text-indigo-600 dark:text-indigo-400" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                  Continuous Improvement
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  We're committed to continuous learning and improvement, constantly evolving our platform based on user feedback and technological advances.
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* Story Section */}
        <section className="py-24 bg-white dark:bg-gray-800">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="max-w-4xl mx-auto text-center">
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-8">
                Our Story
              </h2>
              <div className="space-y-6 text-lg text-gray-600 dark:text-gray-400">
                <p>
                  Data Lens AI was born from a simple observation: while data has become the most valuable asset for modern businesses, the tools to unlock its potential remained complex, expensive, and accessible only to large enterprises with dedicated teams of data scientists.
                </p>
                <p>
                  Founded in 2024 by a team of experienced data scientists, AI researchers, and business leaders, we set out to change this reality. Our founding team combines decades of experience from leading technology companies and has witnessed firsthand the transformative power of data-driven decision making.
                </p>
                <p>
                  Today, Data Lens AI serves organizations across industries and sizes, from innovative startups to Fortune 500 companies. Our platform has processed millions of data points and delivered insights that have driven significant business value for our customers.
                </p>
                <p>
                  As we look to the future, we remain committed to our core mission: making advanced analytics and AI accessible to everyone, empowering organizations to make better decisions faster, and contributing to a more data-informed world.
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* Team Section */}
        <section className="py-24 bg-gray-50 dark:bg-gray-900">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16">
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
                Leadership Team
              </h2>
              <p className="text-xl text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
                Meet the experienced leaders driving Data Lens AI's vision and innovation.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-green-400 to-green-600"></div>
                <div className="p-8">
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Sarah Chen
                  </h3>
                  <p className="text-green-600 dark:text-green-400 mb-4">CEO & Co-Founder</p>
                  <p className="text-gray-600 dark:text-gray-400">
                    Former VP of Data Science at a Fortune 500 company with 15+ years in AI and machine learning.
                  </p>
                </div>
              </div>

              <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-blue-400 to-blue-600"></div>
                <div className="p-8">
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Marcus Rodriguez
                  </h3>
                  <p className="text-blue-600 dark:text-blue-400 mb-4">CTO & Co-Founder</p>
                  <p className="text-gray-600 dark:text-gray-400">
                    Technology veteran with expertise in scalable AI systems and enterprise software architecture.
                  </p>
                </div>
              </div>

              <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
                <div className="h-64 bg-gradient-to-br from-purple-400 to-purple-600"></div>
                <div className="p-8">
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Emily Watson
                  </h3>
                  <p className="text-purple-600 dark:text-purple-400 mb-4">VP of Product</p>
                  <p className="text-gray-600 dark:text-gray-400">
                    Product leader focused on user experience and bringing complex AI capabilities to business users.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="py-24 bg-green-600">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
              Ready to Join Our Journey?
            </h2>
            <p className="text-xl text-green-100 mb-8 max-w-2xl mx-auto">
              Discover how Data Lens AI can transform your organization's relationship with data.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <a
                href="/register"
                className="bg-white text-green-600 px-8 py-4 rounded-lg font-semibold text-lg hover:bg-green-50 transition-colors"
              >
                Get Started Free
              </a>
              <a
                href="/contact"
                className="border-2 border-white text-white px-8 py-4 rounded-lg font-semibold text-lg hover:bg-white hover:text-green-600 transition-colors"
              >
                Contact Us
              </a>
            </div>
          </div>
        </section>
      </div>
    </PublicLayout>
  )
}