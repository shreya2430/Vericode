import api from './api';

// Maps to REST endpoints on PRController / ReviewController

export const prService = {
  getAll: () => api.get('/pullrequests'),
  getById: (id) => api.get(`/pullrequests/${id}`),
  submit: (data) => api.post('/pullrequests', data),
  approve: (id) => api.post(`/pullrequests/${id}/approve`),
  reject: (id) => api.post(`/pullrequests/${id}/reject`),
  merge: (id) => api.post(`/pullrequests/${id}/merge`),
  comment: (id, data) => api.post(`/pullrequests/${id}/comment`, data),
  getHistory: (id) => api.get(`/pullrequests/${id}/history`),
  undo: (id) => api.post(`/pullrequests/${id}/undo`),
};
