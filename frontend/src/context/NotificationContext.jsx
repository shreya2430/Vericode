import { createContext, useContext, useState, useEffect, useRef } from 'react';
import { useUser } from './UserContext';

// Provides in-app notification state fed by a live SSE connection.
// Mirrors InAppNotifier (Observer) → InAppChannel (Bridge) → SseEmitterRegistry on the backend.
const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);
  const { user } = useUser();
  const esRef = useRef(null);

  useEffect(() => {
    // Close any existing connection first
    if (esRef.current) {
      esRef.current.close();
      esRef.current = null;
    }

    // No user = no SSE connection needed
    if (!user) return;

    const es = new EventSource(`/api/notifications/stream?username=${user.username}`);
    esRef.current = es;

    es.addEventListener('notification', (e) => {
      const id = Date.now() + Math.random();
      setNotifications((prev) => [...prev, { id, message: e.data }]);
      // Auto-dismiss after 5 seconds
      setTimeout(() => dismiss(id), 5000);
    });

    es.onerror = () => {
      // EventSource reconnects automatically on error; just clean up the ref
      // if the connection was explicitly closed (readyState CLOSED = 2)
      if (es.readyState === EventSource.CLOSED) {
        esRef.current = null;
      }
    };

    return () => {
      es.close();
      esRef.current = null;
    };
  }, [user]);

  function dismiss(id) {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  }

  // Manual notify() kept for local use (e.g. showing frontend-only messages)
  function notify(message) {
    const id = Date.now() + Math.random();
    setNotifications((prev) => [...prev, { id, message }]);
    setTimeout(() => dismiss(id), 5000);
  }

  return (
    <NotificationContext.Provider value={{ notifications, notify, dismiss }}>
      {children}
    </NotificationContext.Provider>
  );
}

export const useNotifications = () => useContext(NotificationContext);
