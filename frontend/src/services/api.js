import axios from 'axios';

// Base axios instance - proxied to Spring Boot on port 8080 via vite.config.js
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

export default api;
