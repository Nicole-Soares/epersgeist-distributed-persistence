import React, { createContext, useContext, useState, useCallback } from 'react';
import { ShieldAlert, CheckCircle, Info, AlertTriangle, X } from 'lucide-react';

const ModalContext = createContext(null);

export function useModal() {
  const ctx = useContext(ModalContext);
  if (!ctx) {
    throw new Error('useModal debe usarse dentro de un ModalProvider');
  }
  return ctx;
}

export function ModalProvider({ children }) {
  const [modalState, setModalState] = useState({
    isOpen: false,
    title: 'NOTIFICACIÓN DEL SISTEMA',
    message: '',
    type: 'error', // 'error', 'success', 'warning', 'info'
    onConfirm: null
  });

  const showAlert = useCallback((message, title = null, type = 'error', onConfirm = null) => {
    let defaultTitle = 'ALERTA DEL SISTEMA';
    if (type === 'success') defaultTitle = 'OPERACIÓN EXITOSA';
    if (type === 'warning') defaultTitle = 'ADVERTENCIA TÁCTICA';
    if (type === 'info') defaultTitle = 'INFORMACIÓN DE TELEMETRÍA';

    setModalState({
      isOpen: true,
      title: title || defaultTitle,
      message: typeof message === 'string' ? message : String(message),
      type,
      onConfirm
    });
  }, []);

  const closeModal = useCallback(() => {
    setModalState(prev => {
      if (prev.onConfirm) prev.onConfirm();
      return { ...prev, isOpen: false };
    });
  }, []);

  const getTheme = () => {
    switch (modalState.type) {
      case 'success':
        return {
          color: 'var(--primary-green)',
          borderColor: 'rgba(0, 255, 157, 0.4)',
          glow: '0 0 30px rgba(0, 255, 157, 0.25)',
          bgHeader: 'rgba(0, 255, 157, 0.1)',
          icon: <CheckCircle size={24} color="var(--primary-green)" />,
          btnClass: 'btn-hud'
        };
      case 'warning':
        return {
          color: 'var(--amber-gold)',
          borderColor: 'rgba(255, 183, 0, 0.4)',
          glow: '0 0 30px rgba(255, 183, 0, 0.25)',
          bgHeader: 'rgba(255, 183, 0, 0.1)',
          icon: <AlertTriangle size={24} color="var(--amber-gold)" />,
          btnClass: 'btn-hud-blue'
        };
      case 'info':
        return {
          color: 'var(--ice-blue)',
          borderColor: 'rgba(0, 229, 255, 0.4)',
          glow: '0 0 30px rgba(0, 229, 255, 0.25)',
          bgHeader: 'rgba(0, 229, 255, 0.1)',
          icon: <Info size={24} color="var(--ice-blue)" />,
          btnClass: 'btn-hud-blue'
        };
      case 'error':
      default:
        return {
          color: 'var(--demonic-red)',
          borderColor: 'rgba(255, 42, 95, 0.4)',
          glow: '0 0 30px rgba(255, 42, 95, 0.3)',
          bgHeader: 'rgba(255, 42, 95, 0.12)',
          icon: <ShieldAlert size={24} color="var(--demonic-red)" />,
          btnClass: 'btn-hud-danger'
        };
    }
  };

  const theme = getTheme();

  return (
    <ModalContext.Provider value={{ showAlert }}>
      {children}

      {modalState.isOpen && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(5, 8, 15, 0.82)',
          backdropFilter: 'blur(8px)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 10000,
          padding: '20px',
          animation: 'fadeIn 0.2s ease-out'
        }}>
          <div className="hud-panel" style={{
            maxWidth: '480px',
            width: '100%',
            backgroundColor: '#0a0e17',
            borderColor: theme.color,
            boxShadow: theme.glow,
            borderRadius: '12px',
            overflow: 'hidden',
            animation: 'scaleUp 0.2s ease-out'
          }}>
            {/* Modal Header */}
            <div style={{
              padding: '16px 20px',
              backgroundColor: theme.bgHeader,
              borderBottom: `1px solid ${theme.borderColor}`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '12px'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                {theme.icon}
                <h3 className="font-orbitron" style={{
                  fontSize: '0.95rem',
                  letterSpacing: '1px',
                  color: theme.color,
                  margin: 0
                }}>
                  {modalState.title}
                </h3>
              </div>
              <button
                onClick={closeModal}
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: 'var(--text-muted)',
                  cursor: 'pointer',
                  padding: '4px',
                  borderRadius: '4px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  transition: 'color 0.2s'
                }}
                onMouseEnter={e => e.currentTarget.style.color = '#fff'}
                onMouseLeave={e => e.currentTarget.style.color = 'var(--text-muted)'}
              >
                <X size={18} />
              </button>
            </div>

            {/* Modal Content */}
            <div style={{ padding: '24px 20px', color: 'var(--text-main)', fontSize: '0.95rem', lineHeight: '1.6' }}>
              <p style={{ margin: 0 }}>{modalState.message}</p>
            </div>

            {/* Modal Footer */}
            <div style={{
              padding: '12px 20px 16px',
              display: 'flex',
              justifyContent: 'flex-end',
              borderTop: '1px solid rgba(255, 255, 255, 0.05)'
            }}>
              <button
                className={`btn-hud ${theme.btnClass}`}
                onClick={closeModal}
                style={{
                  padding: '8px 24px',
                  fontSize: '0.85rem',
                  letterSpacing: '1px',
                  cursor: 'pointer'
                }}
              >
                ENTENDIDO
              </button>
            </div>
          </div>
        </div>
      )}
    </ModalContext.Provider>
  );
}
