import { createContext, useContext, useState } from 'react';

// Provides in-app notification state - mirrors InAppNotifier (Observer pattern)
const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);

  // placeholder - will push messages when PR state changes
  const notify = (message) => {
    setNotifications((prev) => [...prev, message]);
  };

  return (
    <NotificationContext.Provider value={{ notifications, notify }}>
      {children}
    </NotificationContext.Provider>
  );
}

export const useNotifications = () => useContext(NotificationContext);
