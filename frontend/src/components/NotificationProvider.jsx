import React, { createContext, useContext, useState, useCallback } from 'react';

const NotificationContext = createContext(null);

export function useNotification() {
  const ctx = useContext(NotificationContext);
  if (!ctx) throw new Error('useNotification must be used inside NotificationProvider');
  return ctx;
}

let _id = 0;

export function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);

  const show = useCallback((message, type = 'info', duration = 4000) => {
    const id = ++_id;
    setNotifications(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      setNotifications(prev => prev.filter(n => n.id !== id));
    }, duration);
  }, []);

  const dismiss = useCallback((id) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  }, []);

  const showSuccess = useCallback((msg) => show(msg, 'success'), [show]);
  const showError   = useCallback((msg) => show(msg, 'error',   6000), [show]);
  const showWarning = useCallback((msg) => show(msg, 'warning', 5000), [show]);
  const showInfo    = useCallback((msg) => show(msg, 'info'), [show]);

  return (
    <NotificationContext.Provider value={{ show, showSuccess, showError, showWarning, showInfo }}>
      {children}
      <NotificationContainer notifications={notifications} onDismiss={dismiss} />
    </NotificationContext.Provider>
  );
}

const THEMES = {
  success: {
    borderColor: '#00ff9d',
    iconBg: 'rgba(0,255,157,0.15)',
    glow: '0 0 24px rgba(0,255,157,0.25)',
    icon: '✓',
    label: 'OPERACIÓN EXITOSA',
    barColor: '#00ff9d',
  },
  error: {
    borderColor: '#ff2a5f',
    iconBg: 'rgba(255,42,95,0.15)',
    glow: '0 0 24px rgba(255,42,95,0.3)',
    icon: '✕',
    label: 'ALERTA DEL SISTEMA',
    barColor: '#ff2a5f',
  },
  warning: {
    borderColor: '#f59e0b',
    iconBg: 'rgba(245,158,11,0.15)',
    glow: '0 0 24px rgba(245,158,11,0.2)',
    icon: '⚠',
    label: 'ADVERTENCIA',
    barColor: '#f59e0b',
  },
  info: {
    borderColor: '#00e5ff',
    iconBg: 'rgba(0,229,255,0.12)',
    glow: '0 0 24px rgba(0,229,255,0.2)',
    icon: 'ℹ',
    label: 'INFORMACIÓN',
    barColor: '#00e5ff',
  },
};

function NotificationContainer({ notifications, onDismiss }) {
  if (notifications.length === 0) return null;

  return (
    <div style={{
      position: 'fixed',
      bottom: '24px',
      right: '24px',
      zIndex: 9999,
      display: 'flex',
      flexDirection: 'column-reverse',
      gap: '12px',
      maxWidth: '420px',
      width: '100%',
      pointerEvents: 'none',
    }}>
      {notifications.map(n => (
        <NotificationToast key={n.id} notification={n} onDismiss={onDismiss} />
      ))}
    </div>
  );
}

function NotificationToast({ notification, onDismiss }) {
  const theme = THEMES[notification.type] || THEMES.info;

  return (
    <div
      style={{
        pointerEvents: 'all',
        background: 'linear-gradient(135deg, rgba(10,14,23,0.97) 0%, rgba(14,18,30,0.97) 100%)',
        border: `1px solid ${theme.borderColor}`,
        borderLeft: `4px solid ${theme.borderColor}`,
        borderRadius: '12px',
        boxShadow: theme.glow + ', 0 8px 32px rgba(0,0,0,0.6)',
        backdropFilter: 'blur(16px)',
        overflow: 'hidden',
        animation: 'slideInRight 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)',
        fontFamily: 'var(--font-inter, Inter, sans-serif)',
      }}
    >
      {/* Header bar */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        padding: '10px 14px 8px',
        borderBottom: `1px solid ${theme.borderColor}22`,
      }}>
        <div style={{
          width: '28px',
          height: '28px',
          borderRadius: '8px',
          background: theme.iconBg,
          border: `1px solid ${theme.borderColor}55`,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontSize: '14px',
          color: theme.borderColor,
          fontWeight: 'bold',
          flexShrink: 0,
        }}>
          {theme.icon}
        </div>
        <span style={{
          fontSize: '0.68rem',
          fontFamily: 'var(--font-orbitron, monospace)',
          letterSpacing: '2px',
          color: theme.borderColor,
          fontWeight: 700,
          flexGrow: 1,
        }}>
          {theme.label}
        </span>
        <button
          onClick={() => onDismiss(notification.id)}
          style={{
            background: 'none',
            border: 'none',
            color: 'rgba(255,255,255,0.4)',
            cursor: 'pointer',
            fontSize: '16px',
            lineHeight: 1,
            padding: '2px 4px',
            borderRadius: '4px',
            transition: 'color 0.2s',
          }}
          onMouseEnter={e => e.currentTarget.style.color = '#fff'}
          onMouseLeave={e => e.currentTarget.style.color = 'rgba(255,255,255,0.4)'}
        >
          ×
        </button>
      </div>

      {/* Message body */}
      <div style={{ padding: '10px 14px 14px' }}>
        <p style={{
          margin: 0,
          fontSize: '0.88rem',
          color: 'rgba(255,255,255,0.88)',
          lineHeight: 1.5,
          wordBreak: 'break-word',
        }}>
          {notification.message}
        </p>
      </div>

      {/* Progress bar */}
      <div style={{
        height: '3px',
        background: `${theme.borderColor}33`,
      }}>
        <div style={{
          height: '100%',
          background: `linear-gradient(90deg, ${theme.borderColor}99, ${theme.borderColor})`,
          animation: `shrinkBar ${notification.type === 'error' ? '6s' : notification.type === 'warning' ? '5s' : '4s'} linear forwards`,
          transformOrigin: 'left',
        }} />
      </div>
    </div>
  );
}
