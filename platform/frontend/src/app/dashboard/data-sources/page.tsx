import { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Data Sources - Data Lens AI',
  description: 'Manage your data sources and connections',
}

export default function DataSourcesPage() {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Data Sources</h1>
          <p className="text-gray-600">
            Connect and manage your data sources.
          </p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
          Add Data Source
        </button>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <div className="bg-white p-6 rounded-lg shadow border">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <span className="text-blue-600 font-medium">DB</span>
            </div>
            <div className="ml-4">
              <h3 className="font-medium">PostgreSQL</h3>
              <p className="text-sm text-gray-500">Database connection</p>
            </div>
          </div>
          <div className="flex items-center text-sm text-gray-500">
            <span className="w-2 h-2 bg-gray-400 rounded-full mr-2"></span>
            Not configured
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow border">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <span className="text-green-600 font-medium">API</span>
            </div>
            <div className="ml-4">
              <h3 className="font-medium">REST API</h3>
              <p className="text-sm text-gray-500">External API</p>
            </div>
          </div>
          <div className="flex items-center text-sm text-gray-500">
            <span className="w-2 h-2 bg-gray-400 rounded-full mr-2"></span>
            Not configured
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow border">
          <div className="flex items-center mb-4">
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <span className="text-purple-600 font-medium">CSV</span>
            </div>
            <div className="ml-4">
              <h3 className="font-medium">File Upload</h3>
              <p className="text-sm text-gray-500">CSV, Excel files</p>
            </div>
          </div>
          <div className="flex items-center text-sm text-gray-500">
            <span className="w-2 h-2 bg-gray-400 rounded-full mr-2"></span>
            Not configured
          </div>
        </div>
      </div>
      
      <div className="bg-white p-6 rounded-lg shadow border">
        <h2 className="text-xl font-medium mb-4">Getting Started</h2>
        <p className="text-gray-600 mb-4">
          Connect your first data source to start analyzing your data. Data Lens AI supports various data sources including databases, APIs, and file uploads.
        </p>
        <ul className="text-sm text-gray-600 space-y-2">
          <li>• Connect to PostgreSQL, MySQL, or other databases</li>
          <li>• Import data from REST APIs</li>
          <li>• Upload CSV and Excel files</li>
          <li>• Real-time data synchronization</li>
        </ul>
      </div>
    </div>
  )
}