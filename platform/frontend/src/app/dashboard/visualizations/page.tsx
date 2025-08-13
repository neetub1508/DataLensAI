import { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Visualizations - Data Lens AI',
  description: 'Create and manage data visualizations',
}

export default function VisualizationsPage() {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Visualizations</h1>
          <p className="text-gray-600">
            Create beautiful charts and dashboards from your data.
          </p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
          Create Visualization
        </button>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <div className="bg-white p-4 rounded-lg shadow border hover:shadow-md transition-shadow cursor-pointer">
          <div className="w-full h-32 bg-blue-50 rounded-lg mb-3 flex items-center justify-center">
            <div className="text-blue-400">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="currentColor">
                <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
              </svg>
            </div>
          </div>
          <h3 className="font-medium">Bar Chart</h3>
          <p className="text-sm text-gray-500">Compare values across categories</p>
        </div>
        
        <div className="bg-white p-4 rounded-lg shadow border hover:shadow-md transition-shadow cursor-pointer">
          <div className="w-full h-32 bg-green-50 rounded-lg mb-3 flex items-center justify-center">
            <div className="text-green-400">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/>
              </svg>
            </div>
          </div>
          <h3 className="font-medium">Line Chart</h3>
          <p className="text-sm text-gray-500">Show trends over time</p>
        </div>
        
        <div className="bg-white p-4 rounded-lg shadow border hover:shadow-md transition-shadow cursor-pointer">
          <div className="w-full h-32 bg-purple-50 rounded-lg mb-3 flex items-center justify-center">
            <div className="text-purple-400">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="currentColor">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 6v6l4 2"/>
              </svg>
            </div>
          </div>
          <h3 className="font-medium">Pie Chart</h3>
          <p className="text-sm text-gray-500">Show proportions of a whole</p>
        </div>
        
        <div className="bg-white p-4 rounded-lg shadow border hover:shadow-md transition-shadow cursor-pointer">
          <div className="w-full h-32 bg-orange-50 rounded-lg mb-3 flex items-center justify-center">
            <div className="text-orange-400">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="currentColor">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
            </div>
          </div>
          <h3 className="font-medium">Table</h3>
          <p className="text-sm text-gray-500">Display data in rows and columns</p>
        </div>
      </div>
      
      <div className="bg-white p-6 rounded-lg shadow border">
        <h2 className="text-xl font-medium mb-4">Recent Visualizations</h2>
        <div className="text-center py-8">
          <div className="text-gray-400 mb-4">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor" className="mx-auto">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-600 mb-2">No visualizations yet</h3>
          <p className="text-gray-500 mb-4">
            Create your first visualization to see it here. Connect a data source and start building charts.
          </p>
          <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
            Get Started
          </button>
        </div>
      </div>
    </div>
  )
}