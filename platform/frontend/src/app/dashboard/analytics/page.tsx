import { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Analytics - Data Lens AI',
  description: 'Analytics dashboard for your data insights',
}

export default function AnalyticsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Analytics</h1>
        <p className="text-gray-600">
          View detailed analytics and insights from your data.
        </p>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <div className="bg-white p-6 rounded-lg shadow border">
          <h3 className="text-lg font-medium mb-2">Total Views</h3>
          <p className="text-3xl font-bold text-blue-600">0</p>
          <p className="text-sm text-gray-500">No data available</p>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow border">
          <h3 className="text-lg font-medium mb-2">Active Users</h3>
          <p className="text-3xl font-bold text-green-600">0</p>
          <p className="text-sm text-gray-500">No data available</p>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow border">
          <h3 className="text-lg font-medium mb-2">Conversion Rate</h3>
          <p className="text-3xl font-bold text-purple-600">0%</p>
          <p className="text-sm text-gray-500">No data available</p>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow border">
          <h3 className="text-lg font-medium mb-2">Revenue</h3>
          <p className="text-3xl font-bold text-orange-600">$0</p>
          <p className="text-sm text-gray-500">No data available</p>
        </div>
      </div>
      
      <div className="bg-white p-6 rounded-lg shadow border">
        <h2 className="text-xl font-medium mb-4">Analytics coming soon</h2>
        <p className="text-gray-600">
          Advanced analytics features are currently in development. 
          Connect your data sources to start seeing insights here.
        </p>
      </div>
    </div>
  )
}