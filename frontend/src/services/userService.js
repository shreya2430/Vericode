import api from './api';

export async function loginUser(username, password) {
  const response = await api.post('/users/login', { username, password });
  return response.data;
}

export async function registerUser(name, username, email, password) {
  const response = await api.post('/users/register', { name, username, email, password });
  return response.data;
}

export async function updateUser(id, name, username) {
  const response = await api.put(`/users/${id}`, { name, username });
  return response.data;
}
