// api.js
// All requests go through the API Gateway (port 8080)
// The token is attached automatically to every request

import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', // API Gateway URL
});

// Attach the Keycloak token to every request
// When you integrate real Keycloak, replace 'fake-jwt-token' with keycloak.token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token') || 'fake-jwt-token';
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
