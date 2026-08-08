import React, { useState } from 'react';
import { seedService } from '../services/seedService';
import { formatErrorMessage } from '../services/errorHandler';
import { Database, CheckCircle2, AlertCircle } from 'lucide-react';

export function SeedDataButton({ onDataSeeded }) {
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(null);

  const handleSeedDatabase = async () => {
    setLoading(true);
    setStatus('Poblando base de datos con cazadores de fantasmas y ubicaciones...');
    try {
      await seedService.seedDatabase();
      setStatus('¡Base de datos sembrada con éxito!');
      if (onDataSeeded) onDataSeeded();
    } catch (err) {
      console.error(err);
      setStatus(formatErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', gap: '10px' }}>
      <button 
        className="btn-hud btn-hud-purple" 
        onClick={handleSeedDatabase} 
        disabled={loading}
      >
        <Database size={16} />
        {loading ? 'Cargando Semilla...' : 'Cargar Datos Semilla'}
      </button>

      {status && (
        <div style={{ 
          fontSize: '0.8rem', 
          color: status.includes('Error') || status.includes('No se pudo') ? 'var(--demonic-red)' : 'var(--primary-green)',
          display: 'flex', 
          alignItems: 'center',
          gap: '4px' 
        }}>
          {status.includes('Error') || status.includes('No se pudo') ? <AlertCircle size={14} /> : <CheckCircle2 size={14} />}
          {status}
        </div>
      )}
    </div>
  );
}
