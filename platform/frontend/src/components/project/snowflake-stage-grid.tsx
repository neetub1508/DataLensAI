'use client'

import { formatDistanceToNow } from 'date-fns'
import { SnowflakeStage } from '@/types/project'

interface SnowflakeStageGridProps {
  stages: SnowflakeStage[]
  isLoading: boolean
  onRefresh: () => void
}

export default function SnowflakeStageGrid({ stages, isLoading, onRefresh }: SnowflakeStageGridProps) {
  
  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString)
      if (isNaN(date.getTime())) {
        return 'Unknown'
      }
      return formatDistanceToNow(date, { addSuffix: true })
    } catch (error) {
      return 'Unknown'
    }
  }

  const getStageTypeIcon = (stageType: string) => {
    if (stageType === 'External') {
      return (
        <svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
        </svg>
      )
    } else {
      return (
        <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
        </svg>
      )
    }
  }

  const getStageTypeColor = (stageType: string) => {
    if (stageType === 'External') {
      return 'bg-blue-100 text-blue-800 dark:bg-blue-800 dark:text-blue-100'
    } else {
      return 'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100'
    }
  }

  if (isLoading) {
    return (
      <div className="bg-white dark:bg-gray-800 shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
            <p className="mt-4 text-sm text-gray-500 dark:text-gray-400">Loading Snowflake stages...</p>
          </div>
        </div>
      </div>
    )
  }

  if (stages.length === 0) {
    return (
      <div className="bg-white dark:bg-gray-800 shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="text-center py-12">
            <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <h4 className="mt-2 text-lg font-medium text-gray-900 dark:text-white">
              No Snowflake stages found
            </h4>
            <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
              No stages were found for this project. Try refreshing from Snowflake or check your connection settings.
            </p>
            <div className="mt-6">
              <button 
                onClick={onRefresh}
                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
              >
                Refresh from Snowflake
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white dark:bg-gray-800 shadow rounded-lg">
      <div className="px-4 py-5 sm:p-6">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
            Snowflake Stages ({stages.length})
          </h3>
        </div>
        
        {/* Grid Layout */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {stages.map((stage, index) => (
            <div 
              key={`${stage.stage_name}-${stage.stage_schema}-${index}`}
              className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 border border-gray-200 dark:border-gray-600 hover:shadow-md transition-shadow"
            >
              {/* Stage Header */}
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center space-x-2">
                  {getStageTypeIcon(stage.stage_type)}
                  <div className="flex-1 min-w-0">
                    <h4 className="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {stage.stage_name}
                    </h4>
                  </div>
                </div>
                
                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100">
                  Active
                </span>
              </div>

              {/* Stage Details */}
              <div className="space-y-2 mb-4">
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-500 dark:text-gray-400">Type:</span>
                  <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStageTypeColor(stage.stage_type)}`}>
                    {stage.stage_type}
                  </span>
                </div>
                
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-500 dark:text-gray-400">Database:</span>
                  <span className="text-xs text-gray-900 dark:text-white font-mono truncate max-w-24" title={stage.stage_database}>
                    {stage.stage_database}
                  </span>
                </div>
                
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-500 dark:text-gray-400">Schema:</span>
                  <span className="text-xs text-gray-900 dark:text-white font-mono truncate max-w-24" title={stage.stage_schema}>
                    {stage.stage_schema}
                  </span>
                </div>
                
                {stage.stage_location && (
                  <div className="mt-2">
                    <span className="text-xs text-gray-500 dark:text-gray-400">Location:</span>
                    <div className="text-xs text-gray-900 dark:text-white font-mono mt-1 break-all bg-white dark:bg-gray-800 p-2 rounded border">
                      {stage.stage_location}
                    </div>
                  </div>
                )}
              </div>

              {/* Stage Comment */}
              {stage.comment && (
                <div className="mb-3">
                  <p className="text-xs text-gray-600 dark:text-gray-300 line-clamp-2" title={stage.comment}>
                    {stage.comment}
                  </p>
                </div>
              )}

              {/* Stage Footer */}
              <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400 pt-3 border-t border-gray-200 dark:border-gray-600">
                <div className="flex items-center space-x-1">
                  <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  <span className="truncate max-w-16" title={stage.owner}>
                    {stage.owner}
                  </span>
                </div>
                
                <div className="flex items-center space-x-1">
                  <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>{stage.created ? formatDate(stage.created) : 'Unknown'}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}