import React, { useState, useEffect } from 'react';
import { spiritService } from '../services/spiritService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from './ModalProvider';
import { ShieldAlert, Link, Crown, ChevronLeft, ChevronRight } from 'lucide-react';

export function SpiritRoster({ spirits, mediums, locations = [], onRefresh }) {
  const { showAlert } = useModal();
  const [demonsPage, setDemonsPage] = useState(null);

  const [pageNumber, setPageNumber] = useState(0);
  const [selectedMediumId, setSelectedMediumId] = useState('');
  const [dominantSpiritId, setDominantSpiritId] = useState('');
  const [targetDominatedId, setTargetDominatedId] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchDemons();
  }, [pageNumber]);

  const fetchDemons = async () => {
    try {
      const res = await spiritService.getDemoniosPaginados('DESC', pageNumber, 5);
      setDemonsPage(res);
    } catch (e) {
      console.warn("Error consultando demonios paginados:", e);
    }
  };

  const [cardMediums, setCardMediums] = useState({});

  const handleConectar = async (espirituId, medId) => {
    const chosenMediumId = medId || selectedMediumId;
    if (!chosenMediumId) return showAlert('Selecciona un Medium para conectar.', 'SELECCIÓN REQUERIDA', 'warning');
    
    const spiritObj = spirits.find(s => s.id === espirituId);
    const mediumObj = mediums.find(m => m.id === Number(chosenMediumId));
    const spiritName = spiritObj?.nombre || 'Espíritu';
    const mediumName = mediumObj?.nombre || 'Medium';

    // Verificar si el espíritu y el médium están en ubicaciones distintas
    const spiritLocId = spiritObj?.ubicacion?.id || spiritObj?.ubicacionId;
    const mediumLocId = mediumObj?.ubicacion?.id || mediumObj?.ubicacionId;
    if (spiritLocId && mediumLocId && spiritLocId !== mediumLocId) {
      const sLocName = spiritObj?.ubicacion?.nombre || locations.find(l => l.id === spiritLocId)?.nombre || 'su ubicación actual';
      const mLocName = mediumObj?.ubicacion?.nombre || locations.find(l => l.id === mediumLocId)?.nombre || 'su ubicación actual';
      return showAlert(
        `No es posible conectar a ${spiritName} porque se encuentra en "${sLocName}", mientras que ${mediumName} está en "${mLocName}". Para conectar un espíritu, primero debe ser INVOCADO a la ubicación del médium.`,
        'UBICACIÓN DIFERENTE',
        'warning'
      );
    }

    setLoading(true);
    try {
      await spiritService.conectarEspiritu(espirituId, chosenMediumId);
      showAlert(`¡${spiritName} se ha conectado con éxito a ${mediumName}!`, 'CONEXIÓN ESTABLECIDA', 'success');
      onRefresh();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE CONEXIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };


  const handleDominar = async () => {
    if (!dominantSpiritId || !targetDominatedId) return showAlert('Selecciona el Espíritu Dominante y el Espíritu a Dominar.', 'SELECCIÓN REQUERIDA', 'warning');
    setLoading(true);
    const domObj = spirits.find(s => s.id === Number(dominantSpiritId));
    const targetObj = spirits.find(s => s.id === Number(targetDominatedId));
    try {
      await spiritService.dominarEspiritu(targetDominatedId, dominantSpiritId);
      showAlert(`¡El espíritu ${domObj?.nombre || ''} ha dominado exitosamente a ${targetObj?.nombre || ''}!`, 'DOMINACIÓN EXITOSA', 'success');
      onRefresh();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE DOMINACIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };


  return (
    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
      
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 className="font-orbitron glow-text-purple" style={{ fontSize: '1.2rem', color: 'var(--spectral-purple)' }}>
            👻 COMPENDIO DE ESPÍRITUS (Ángeles & Demonios)
          </h2>

          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Medium Destino:</span>
            <select
              value={selectedMediumId}
              onChange={(e) => setSelectedMediumId(e.target.value)}
              style={{
                background: '#0a0e17',
                border: '1px solid var(--bg-card-border)',
                color: 'var(--text-main)',
                padding: '4px 8px',
                borderRadius: '4px',
                fontSize: '0.85rem'
              }}
            >
              <option value="">-- Seleccionar Medium --</option>
              {mediums.map(m => (
                <option key={m.id} value={m.id}>{m.nombre}</option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
          {spirits.map(s => {
            const isDemon = s.tipo === 'DEMONIO';
            const ownerMedium = mediums.find(m => m.id === s.mediumId || m.id === s.medium?.id);
            return (
              <div key={s.id} className="hud-panel" style={{ padding: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                  <div>
                    <h3 className="font-orbitron" style={{ fontSize: '1rem', color: '#fff' }}>{s.nombre}</h3>
                    <span className={`badge-hud ${isDemon ? 'badge-red' : 'badge-green'}`} style={{ marginTop: '4px' }}>
                      {s.tipo}
                    </span>
                  </div>
                </div>

                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'grid', gap: '4px', margin: '12px 0' }}>
                  <div>Conexión: <strong>{s.nivelDeConexion}%</strong></div>
                  <div>Hostilidad: <strong style={{ color: isDemon ? 'var(--demonic-red)' : 'var(--primary-green)' }}>{s.hostilidad}</strong></div>
                  <div>Ubicación: <strong>{s.ubicacion?.nombre || locations.find(l => l.id === (s.ubicacionId || s.ubicacion?.id))?.nombre || 'En mapa'}</strong></div>
                  <div>Enlazado a Medium: <strong>{ownerMedium ? ownerMedium.nombre : 'Libre 🟢'}</strong></div>
                </div>

                {!s.mediumId && (
                  <div style={{ marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <select
                      value={cardMediums[s.id] || selectedMediumId || ''}
                      onChange={(e) => setCardMediums({ ...cardMediums, [s.id]: e.target.value })}
                      style={{
                        width: '100%',
                        background: '#0a0e17',
                        border: '1px solid var(--bg-card-border)',
                        color: 'var(--text-main)',
                        fontSize: '0.8rem',
                        padding: '6px 8px',
                        borderRadius: '4px'
                      }}
                    >
                      <option value="">-- Seleccionar Médium --</option>
                      {mediums.map(m => (
                        <option key={m.id} value={m.id}>{m.nombre}</option>
                      ))}
                    </select>
                    <button
                      className="btn-hud btn-hud-blue"
                      onClick={() => handleConectar(s.id, cardMediums[s.id] || selectedMediumId)}
                      disabled={loading || !(cardMediums[s.id] || selectedMediumId)}
                      style={{ width: '100%', padding: '6px 12px', fontSize: '0.8rem', justifyContent: 'center' }}
                    >
                      <Link size={14} /> Conectar Médium
                    </button>
                  </div>
                )}

              </div>
            );
          })}
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div className="hud-panel" style={{ padding: '20px' }}>
          <h3 className="font-orbitron" style={{ color: 'var(--demonic-red)', fontSize: '1rem', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <ShieldAlert size={18} /> Ranking de Demonios
          </h3>

          {!demonsPage || !demonsPage.content || demonsPage.content.length === 0 ? (
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No se registraron demonios activados.</p>
          ) : (
            <div style={{ display: 'grid', gap: '8px' }}>
              {demonsPage.content.map(d => (
                <div key={d.id} style={{ background: 'rgba(255, 42, 95, 0.1)', border: '1px solid var(--demonic-red)', padding: '8px 12px', borderRadius: '6px', fontSize: '0.85rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <strong>{d.nombre}</strong>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Hostilidad: {d.hostilidad}</div>
                  </div>
                  <span className="badge-hud badge-red">{d.nivelDeConexion}%</span>
                </div>
              ))}

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' }}>
                <button 
                  className="btn-hud btn-hud-blue"
                  disabled={pageNumber <= 0}
                  onClick={() => setPageNumber(p => Math.max(0, p - 1))}
                  style={{ padding: '4px 8px' }}
                >
                  <ChevronLeft size={14} /> Anterior
                </button>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  Página {pageNumber + 1} de {demonsPage.totalPages || 1}
                </span>
                <button 
                  className="btn-hud btn-hud-blue"
                  disabled={demonsPage.last}
                  onClick={() => setPageNumber(p => p + 1)}
                  style={{ padding: '4px 8px' }}
                >
                  Siguiente <ChevronRight size={14} />
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

    </div>
  );
}
