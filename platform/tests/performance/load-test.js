import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
export let errorRate = new Rate('errors');

// Test configuration
export let options = {
  stages: [
    { duration: '2m', target: 10 }, // Ramp up to 10 users
    { duration: '5m', target: 10 }, // Maintain 10 users
    { duration: '2m', target: 20 }, // Ramp up to 20 users
    { duration: '5m', target: 20 }, // Maintain 20 users
    { duration: '2m', target: 0 },  // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests should be below 1s
    http_req_failed: ['rate<0.05'],    // Error rate should be below 5%
    errors: ['rate<0.05'],             // Custom error rate should be below 5%
  },
};

const BASE_URL = __ENV.API_URL || 'http://backend-app:8000';

// Test data
const testUsers = [
  { email: 'user1@test.com', password: 'password123' },
  { email: 'user2@test.com', password: 'password123' },
  { email: 'user3@test.com', password: 'password123' },
];

export function setup() {
  // Setup test data if needed
  console.log('Setting up performance test environment...');
  
  // Health check
  let healthRes = http.get(`${BASE_URL}/health`);
  if (healthRes.status !== 200) {
    console.warn('Health check failed, continuing anyway...');
  }
  
  return { baseUrl: BASE_URL };
}

export default function (data) {
  // Test scenarios
  testHomePage(data.baseUrl);
  sleep(1);
  
  testAuthEndpoints(data.baseUrl);
  sleep(1);
  
  testApiEndpoints(data.baseUrl);
  sleep(1);
}

function testHomePage(baseUrl) {
  // Test static pages
  let responses = http.batch([
    { method: 'GET', url: `${baseUrl}/` },
    { method: 'GET', url: `${baseUrl}/about` },
    { method: 'GET', url: `${baseUrl}/pricing` },
  ]);
  
  responses.forEach((res, index) => {
    let result = check(res, {
      'home page status is 200 or 404': (r) => r.status === 200 || r.status === 404,
      'home page response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    errorRate.add(!result);
  });
}

function testAuthEndpoints(baseUrl) {
  // Test registration endpoint
  let registrationPayload = JSON.stringify({
    email: `test-${Date.now()}@example.com`,
    password: 'password123',
  });
  
  let registerRes = http.post(`${baseUrl}/api/auth/register`, registrationPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  let registerResult = check(registerRes, {
    'register endpoint responds': (r) => r.status === 201 || r.status === 400 || r.status === 404,
    'register response time < 1000ms': (r) => r.timings.duration < 1000,
  });
  
  errorRate.add(!registerResult);
  
  // Test login endpoint
  let loginPayload = JSON.stringify({
    email: 'test@example.com',
    password: 'password123',
  });
  
  let loginRes = http.post(`${baseUrl}/api/auth/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  let loginResult = check(loginRes, {
    'login endpoint responds': (r) => r.status === 200 || r.status === 401 || r.status === 404,
    'login response time < 1000ms': (r) => r.timings.duration < 1000,
  });
  
  errorRate.add(!loginResult);
}

function testApiEndpoints(baseUrl) {
  // Test public API endpoints
  let apiResponses = http.batch([
    { method: 'GET', url: `${baseUrl}/api/health` },
    { method: 'GET', url: `${baseUrl}/api/version` },
  ]);
  
  apiResponses.forEach((res, index) => {
    let result = check(res, {
      'api endpoint responds': (r) => r.status === 200 || r.status === 404,
      'api response time < 300ms': (r) => r.timings.duration < 300,
    });
    
    errorRate.add(!result);
  });
  
  // Test blog endpoints
  let blogRes = http.get(`${baseUrl}/api/blog/posts`);
  let blogResult = check(blogRes, {
    'blog endpoint responds': (r) => r.status === 200 || r.status === 404,
    'blog response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  errorRate.add(!blogResult);
}

export function teardown(data) {
  console.log('Cleaning up performance test environment...');
  
  // Final health check
  let finalHealthRes = http.get(`${data.baseUrl}/health`);
  if (finalHealthRes.status === 200) {
    console.log('Application is still healthy after performance test');
  }
}