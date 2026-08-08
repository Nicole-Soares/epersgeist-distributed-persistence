import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from './App';
import { NotificationProvider } from './components/NotificationProvider';
import { ModalProvider } from './components/ModalProvider';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <NotificationProvider>
      <ModalProvider>
        <App />
      </ModalProvider>
    </NotificationProvider>
  </React.StrictMode>
);

