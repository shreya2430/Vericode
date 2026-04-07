import { useNotifications } from '../context/NotificationContext';
import './NotificationBanner.css';

// Renders in-app toast notifications pushed from the backend via SSE.
// Each toast auto-dismisses after 5 seconds or can be closed manually.
function NotificationBanner() {
  const { notifications, dismiss } = useNotifications();

  if (notifications.length === 0) return null;

  return (
    <div className="notif-stack">
      {notifications.map((n) => (
        <div key={n.id} className="notif-toast">
          <span className="notif-message">{n.message}</span>
          <button className="notif-close" onClick={() => dismiss(n.id)} aria-label="Dismiss">
            ×
          </button>
        </div>
      ))}
    </div>
  );
}

export default NotificationBanner;
