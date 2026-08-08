import React, { useState } from 'react';
import { mediumService } from '../services/mediumService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from './ModalProvider';
import { Swords, Flame, Sparkles, User, Crosshair } from 'lucide-react';

export function ExorcismChamber({ mediums, spirits, onRefresh }) {
  const { showAlert } = useModal();
  const [exorcistId, setExorcistId] = useState('');

  const [victimId, setVictimId] = useState('');
  const [loading, setLoading] = useState(false);
  const [battleLog, setBattleLog] = useState([]);

  const exorcistMedium = mediums.find(m => m.id === Number(exorcistId));
  const victimMedium = mediums.find(m => m.id === Number(victimId));

  const handleExorcism = async () => {
    if (!exorcistId || !victimId) return showAlert('Selecciona un Medium Exorcista y un Medium Poseído.', 'SELECCIÓN INCOMPLETA', 'warning');
    if (exorcistId === victimId) return showAlert('El exorcista y la víctima no pueden ser la misma persona.', 'CONFLICTO DE OBJETIVOS', 'warning');


    setLoading(true);
    setBattleLog([
      `⚡ Iniciando ritual de exorcismo con ${exorcistMedium?.nombre}...`,
      `✝️ Invocando legión de Ángeles contra los espíritus de ${victimMedium?.nombre}...`
    ]);

    try {
      await mediumService.exorcizar(exorcistId, victimId);

      setTimeout(() => {
        setBattleLog(prev => [
          ...prev,
          `🔥 Exorcismo ejecutado con éxito.`,
          `✨ Los ángeles atacaron a las entidades hostiles. Sanidad restaurada.`
        ]);
        onRefresh();
        setLoading(false);
      }, 800);

    } catch (err) {
      setBattleLog(prev => [
        ...prev,
        `❌ FALLO EN EL RITUAL: ${formatErrorMessage(err)}`
      ]);
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
      
      {/* EXORCISM CONTROLS */}
      <div className="hud-panel" style={{ padding: '24px' }}>
        <h2 className="font-orbitron glow-text-red" style={{ fontSize: '1.2rem', color: 'var(--demonic-red)', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '10px' }}>
          <Swords size={24} /> CÁMARA DE RITUALES Y EXORCISMOS
        </h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
          Enfrenta las fuerzas celestiales del Medium Exorcista contra los demonios que poseen a la víctima en la misma ubicación.
        </p>

        <div style={{ display: 'grid', gap: '16px' }}>
          
          <div style={{ background: 'rgba(0, 255, 157, 0.05)', border: '1px solid var(--primary-green)', padding: '12px', borderRadius: '8px' }}>
            <label style={{ fontSize: '0.85rem', color: 'var(--primary-green)', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '6px' }}>
              <User size={16} /> 1. Medium Exorcista (Debe poseer Ángeles):
            </label>
            <select
              value={exorcistId}
              onChange={(e) => setExorcistId(e.target.value)}
              style={{ width: '100%', background: '#0a0e17', border: '1px solid var(--primary-green)', color: '#fff', padding: '8px', borderRadius: '4px' }}
            >
              <option value="">-- Seleccionar Exorcista --</option>
              {mediums.map(m => (
                <option key={m.id} value={m.id}>
                  {m.nombre} (Espíritus: {m.espiritus?.length || 0})
                </option>
              ))}
            </select>
          </div>

          <div style={{ background: 'rgba(255, 42, 95, 0.05)', border: '1px solid var(--demonic-red)', padding: '12px', borderRadius: '8px' }}>
            <label style={{ fontSize: '0.85rem', color: 'var(--demonic-red)', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '6px' }}>
              <Crosshair size={16} /> 2. Medium Poseído (Víctima):
            </label>
            <select
              value={victimId}
              onChange={(e) => setVictimId(e.target.value)}
              style={{ width: '100%', background: '#0a0e17', border: '1px solid var(--demonic-red)', color: '#fff', padding: '8px', borderRadius: '4px' }}
            >
              <option value="">-- Seleccionar Víctima --</option>
              {mediums.filter(m => m.id !== Number(exorcistId)).map(m => (
                <option key={m.id} value={m.id}>
                  {m.nombre} (Espíritus: {m.espiritus?.length || 0})
                </option>
              ))}
            </select>
          </div>

          <button
            className="btn-hud btn-hud-danger"
            onClick={handleExorcism}
            disabled={loading || !exorcistId || !victimId}
            style={{ width: '100%', padding: '14px', fontSize: '1rem', justifyContent: 'center', marginTop: '10px' }}
          >
            <Flame size={20} /> Iniciar Ritual de Purificación
          </button>
        </div>
      </div>

      {/* BATTLE LOG */}
      <div className="hud-panel scanline-bg" style={{ padding: '24px', display: 'flex', flexDirection: 'column' }}>
        <h3 className="font-orbitron" style={{ fontSize: '1.05rem', color: 'var(--ice-blue)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Sparkles size={18} /> Registro de Combate Espiritual
        </h3>

        <div style={{
          flexGrow: 1,
          background: 'rgba(5, 8, 14, 0.9)',
          border: '1px solid var(--bg-card-border)',
          borderRadius: '8px',
          padding: '16px',
          fontFamily: 'var(--font-mono)',
          fontSize: '0.85rem',
          color: 'var(--text-main)',
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
          minHeight: '280px'
        }}>
          {battleLog.length === 0 ? (
            <p style={{ margin: 'auto', color: 'var(--text-muted)' }}>Esperando inicio de ritual...</p>
          ) : (
            battleLog.map((log, idx) => (
              <div key={idx} style={{
                color: log.includes('FALLO') ? 'var(--demonic-red)' : log.includes('éxito') ? 'var(--primary-green)' : 'var(--text-main)'
              }}>
                {log}
              </div>
            ))
          )}
        </div>
      </div>

    </div>
  );
}
