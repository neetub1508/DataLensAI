# OAuth2 Social Login Setup

This document explains how to set up social login with Google, Facebook, and GitHub for the Data Lens AI platform.

## Prerequisites

You need to create OAuth2 applications with each provider and obtain client credentials.

## Google OAuth2 Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Set the application type to "Web application"
6. Add authorized redirect URIs:
   - `http://localhost:8000/api/v1/login/oauth2/code/google` (for development)
   - `https://yourdomain.com/api/v1/login/oauth2/code/google` (for production)
7. Copy the Client ID and Client Secret

## Facebook OAuth2 Setup

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app or select an existing one
3. Add "Facebook Login" product
4. In Facebook Login settings, add valid OAuth redirect URIs:
   - `http://localhost:8000/api/v1/login/oauth2/code/facebook` (for development)
   - `https://yourdomain.com/api/v1/login/oauth2/code/facebook` (for production)
5. Copy the App ID and App Secret from Basic Settings

## GitHub OAuth2 Setup

1. Go to GitHub Settings → Developer settings → OAuth Apps
2. Click "New OAuth App"
3. Fill in the application details:
   - Application name: Data Lens AI
   - Homepage URL: `http://localhost:3000` (for development)
   - Authorization callback URL: `http://localhost:8000/api/v1/login/oauth2/code/github`
4. Copy the Client ID and Client Secret

## Environment Configuration

Add the following environment variables to your system or `.env` file:

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here

# Facebook OAuth2
FACEBOOK_CLIENT_ID=your_facebook_app_id_here
FACEBOOK_CLIENT_SECRET=your_facebook_app_secret_here

# GitHub OAuth2
GITHUB_CLIENT_ID=your_github_client_id_here
GITHUB_CLIENT_SECRET=your_github_client_secret_here
```

## Backend Configuration

The OAuth2 configuration is already set up in `application.yml`. The backend will automatically pick up the environment variables.

## Frontend Configuration

The frontend is configured to redirect users to the appropriate OAuth2 authorization endpoints. Make sure the `NEXT_PUBLIC_API_URL` environment variable is set correctly:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000
```

## Testing Social Login

1. Start the backend server: `cd platform && ./dev-start.sh`
2. Start the frontend server: `cd platform/frontend && npm run dev`
3. Navigate to `http://localhost:3000/register` or `http://localhost:3000/login`
4. Click on any of the social login buttons (Google, Facebook, GitHub)
5. Complete the OAuth2 flow with the respective provider
6. You should be redirected back to the application and automatically logged in

## Troubleshooting

### Common Issues

1. **"redirect_uri_mismatch" error**: Ensure the redirect URIs in your OAuth2 app settings exactly match the ones used by the backend.

2. **"invalid_client" error**: Check that your client ID and client secret are correctly set in the environment variables.

3. **CORS issues**: Make sure the frontend domain is added to the CORS allowed origins in the backend configuration.

4. **Database errors**: Ensure the database migration V6 has been applied to add the OAuth2 fields to the users table.

### Debug Tips

- Check the browser developer tools for any JavaScript errors
- Check the backend logs for OAuth2 related errors
- Verify that the callback URL in the OAuth2 app settings matches the backend endpoint
- Test the OAuth2 flow step by step using the browser network tab

## Security Considerations

1. Always use HTTPS in production
2. Keep your client secrets secure and never expose them in frontend code
3. Regularly rotate your OAuth2 credentials
4. Validate the redirect URIs to prevent open redirect attacks
5. Consider implementing rate limiting for OAuth2 endpoints