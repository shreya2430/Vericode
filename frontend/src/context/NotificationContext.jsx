import { createContext, useContext, useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { useUser } from './UserContext';

// Provides in-app notification state fed by two real-time connections:
//   1. SSE  → targets the logged-in user (InAppNotifier → InAppChannel → SseEmitterRegistry)
//   2. STOMP WebSocket → broadcasts to all connected users (WebSocketNotifier → WebSocketChannel)
const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);
  const { user } = useUser();
  const esRef = useRef(null);
  const stompRef = useRef(null);

  useEffect(() => {
    // Close existing connections first
    if (esRef.current) {
      esRef.current.close();
      esRef.current = null;
    }
    if (stompRef.current) {
      stompRef.current.deactivate();
      stompRef.current = null;
    }

    if (!user) return;

    // --- SSE: targeted in-app notifications for this user ---
    const es = new EventSource(`/api/notifications/stream?username=${user.username}`);
    esRef.current = es;

    es.addEventListener('notification', (e) => {
      addNotification(e.data);
    });

    es.onerror = () => {
      if (es.readyState === EventSource.CLOSED) {
        esRef.current = null;
      }
    };

    // --- STOMP WebSocket: broadcast updates to all connected users ---
    const wsUrl = `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws`;
    const client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe('/topic/pr-updates', (frame) => {
          const payload = JSON.parse(frame.body);
          addNotification(payload.message);
        });
      },
    });
    client.activate();
    stompRef.current = client;

    return () => {
      es.close();
      esRef.current = null;
      client.deactivate();
      stompRef.current = null;
    };
  }, [user]);

  function addNotification(message) {
    const id = Date.now() + Math.random();
    setNotifications((prev) => [...prev, { id, message }]);
    setTimeout(() => dismiss(id), 5000);
  }

  function dismiss(id) {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  }

  // Manual notify() kept for local frontend-only messages
  function notify(message) {
    addNotification(message);
  }

  return (
    <NotificationContext.Provider value={{ notifications, notify, dismiss }}>
      {children}
    </NotificationContext.Provider>
  );
}

export const useNotifications = () => useContext(NotificationContext);
