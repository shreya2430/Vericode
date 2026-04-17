import api from './api';

// Maps to REST endpoints on PRController / ReviewController

export const prService = {
  // PRController: /api/pullrequests
  getAll:   ()     => api.get('/pullrequests'),
  getById:  (id)   => api.get(`/pullrequests/${id}`),
  submit:   (data) => api.post('/pullrequests', data),
  delete:   (id)   => api.delete(`/pullrequests/${id}`),
  check:    (id)   => api.get(`/pullrequests/${id}/check`),

  // ReviewController: /api/reviews
  submitForReview: (id)                          => api.post(`/reviews/${id}/submit`),
  approve:         (id, reviewer)                => api.post(`/reviews/${id}/approve`,         { reviewer }),
  requestChanges:  (id, reviewer)                => api.post(`/reviews/${id}/request-changes`, { reviewer }),
  merge:           (id, reviewer)                => api.post(`/reviews/${id}/merge`,           { reviewer }),
  comment:         (id, author, lineNumber, content) =>
    api.post(`/reviews/${id}/comment`, { author, lineNumber: String(lineNumber), content }),
  undo:       () => api.post('/reviews/undo'),
  getHistory: () => api.get('/reviews/history'),
};
