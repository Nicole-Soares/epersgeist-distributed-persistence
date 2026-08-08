import React, { useState } from 'react';
import { mediumService } from '../services/mediumService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from './ModalProvider';
import { User, Battery, Activity, Moon, Sparkles } from 'lucide-react';

export function MediumCard({ medium, locations, freeSpirits, onRefresh }) {
  const { showAlert } = useModal();
  const [loading, setLoading] = useState(false);

  const [selectedSpiritId, setSelectedSpiritId] = useState('');

  const locName = locations.find(l => l.id === medium.ubicacionId)?.nombre || `Ubicación #${medium.ubicacionId}`;
  const corduraVal = medium.cordura ?? 100;
  const manaVal = medium.mana ?? 0;
  const manaMaxVal = medium.manaMax ?? 100;
  const manaPercent = Math.min(100, Math.max(0, (manaVal / manaMaxVal) * 100));

  const handleDescansar = async () => {
    setLoading(true);
    try {
      await mediumService.descansarMedium(medium.id);
      showAlert(`Medium ${medium.nombre} ha descansado. Maná y conexión recuperados.`, 'DESCANSO COMPLETADO', 'success');
      onRefresh();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR AL DESCANSAR', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleInvocar = async () => {
    if (!selectedSpiritId) return showAlert('Selecciona un espíritu libre para invocar', 'SELECCIÓN REQUERIDA', 'warning');
    setLoading(true);
    const spi = freeSpirits.find(s => s.id === Number(selectedSpiritId));
    try {
      await mediumService.invocarEspiritu(medium.id, selectedSpiritId);
      showAlert(`¡${spi?.nombre || 'Espíritu'} invocado con éxito por ${medium.nombre}! (Costo: 10 Maná)`, 'INVOCACIÓN EXITOSA', 'success');
      onRefresh();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR EN INVOCACIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="hud-panel" style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ background: 'rgba(0,229,255,0.15)', border: '1px solid var(--ice-blue)', borderRadius: '50%', padding: '8px' }}>
            <User size={22} color="var(--ice-blue)" />
          </div>
          <div>
            <h3 className="font-orbitron" style={{ fontSize: '1.1rem', color: '#fff' }}>{medium.nombre}</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>📍 {locName}</span>
          </div>
        </div>
      </div>

      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '4px' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--ice-blue)' }}>
            <Battery size={14} /> Maná Psíquico
          </span>
          <strong>{manaVal} / {manaMaxVal}</strong>
        </div>
        <div style={{ background: 'rgba(0,0,0,0.5)', height: '10px', borderRadius: '5px', overflow: 'hidden', border: '1px solid var(--bg-card-border)' }}>
          <div style={{
            width: `${manaPercent}%`,
            height: '100%',
            background: 'linear-gradient(90deg, #00e5ff, #00ff9d)',
            boxShadow: '0 0 10px var(--ice-blue-glow)',
            transition: 'width 0.5s ease'
          }} />
        </div>
      </div>

      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '4px' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--primary-green)' }}>
            <Activity size={14} /> Nivel de Cordura
          </span>
          <strong>{corduraVal.toFixed(1)}%</strong>
        </div>
        <div style={{ background: 'rgba(0,0,0,0.5)', height: '10px', borderRadius: '5px', overflow: 'hidden', border: '1px solid var(--bg-card-border)' }}>
          <div style={{
            width: `${Math.min(100, Math.max(0, corduraVal))}%`,
            height: '100%',
            background: corduraVal < 30 ? 'var(--demonic-red)' : 'var(--primary-green)',
            boxShadow: corduraVal < 30 ? '0 0 10px var(--demonic-red-glow)' : '0 0 10px var(--primary-green-glow)',
            transition: 'width 0.5s ease'
          }} />
        </div>
      </div>

      <div style={{ borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '12px' }}>
        <h4 style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '8px' }}>
          👻 Espíritus Enlazados ({medium.espiritus?.length || 0}):
        </h4>
        {!medium.espiritus || medium.espiritus.length === 0 ? (
          <p style={{ fontSize: '0.8rem', color: 'var(--text-dim)', italic: 'true' }}>Sin espíritus conectados</p>
        ) : (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
            {medium.espiritus.map(e => (
              <span key={e.id} className={`badge-hud ${e.tipo === 'DEMONIO' ? 'badge-red' : 'badge-green'}`}>
                {e.nombre} ({e.tipo})
              </span>
            ))}
          </div>
        )}
      </div>

      <div style={{ display: 'grid', gap: '10px', marginTop: 'auto' }}>
        <button 
          className="btn-hud" 
          onClick={handleDescansar} 
          disabled={loading}
          style={{ justifyContent: 'center' }}
        >
          <Moon size={14} /> Descansar y Recuperar Maná
        </button>

        <div style={{ display: 'flex', gap: '8px' }}>
          <select
            value={selectedSpiritId}
            onChange={(e) => setSelectedSpiritId(e.target.value)}
            style={{
              flexGrow: 1,
              background: '#0a0e17',
              border: '1px solid var(--bg-card-border)',
              color: 'var(--text-main)',
              fontSize: '0.8rem',
              padding: '6px',
              borderRadius: '6px'
            }}
          >
            <option value="">-- Espíritu a Invocar --</option>
            {freeSpirits.map(s => (
              <option key={s.id} value={s.id}>
                {s.nombre} ({s.tipo})
              </option>
            ))}
          </select>
          <button 
            className="btn-hud btn-hud-purple" 
            onClick={handleInvocar}
            disabled={loading || !selectedSpiritId}
            style={{ padding: '6px 12px' }}
          >
            <Sparkles size={14} /> Invocar
          </button>
        </div>
      </div>

    </div>
  );
}
