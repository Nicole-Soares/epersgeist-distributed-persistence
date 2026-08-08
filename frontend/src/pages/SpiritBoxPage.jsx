import React, { useState, useEffect } from 'react';
import { mediumService } from '../services/mediumService';
import { spiritBoxService } from '../services/spiritBoxService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from '../components/ModalProvider';
import { Radio, Send, RefreshCw, HelpCircle, FileText, Cpu, CheckCircle } from 'lucide-react';

export function SpiritBoxPage({ mediums, locations }) {
  const { showAlert } = useModal();
  const [selectedMediumId, setSelectedMediumId] = useState('');

  const [messageInput, setMessageInput] = useState('');
  const [chatLogs, setChatLogs] = useState([]);
  const [activeComm, setActiveComm] = useState(null);
  const [conjectureName, setConjectureName] = useState('');
  const [templateInput, setTemplateInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [showTemplateModal, setShowTemplateModal] = useState(false);

  const selectedMedium = mediums.find(m => m.id === Number(selectedMediumId));
  const locationId = selectedMedium?.ubicacionId;

  useEffect(() => {
    if (selectedMediumId) {
      loadChatAndActiveStatus();
    } else {
      setChatLogs([]);
      setActiveComm(null);
    }
  }, [selectedMediumId]);

  const loadChatAndActiveStatus = async () => {
    if (!selectedMediumId) return;
    try {
      const logs = await spiritBoxService.getHistorialChat(selectedMediumId);
      setChatLogs(logs || []);

      if (locationId) {
        const comm = await spiritBoxService.getComunicacionActiva(selectedMediumId, locationId);
        setActiveComm(comm);
      }
    } catch (e) {
      console.warn("Error cargando mensajería:", e);
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!selectedMediumId || !messageInput.trim()) return;

    setLoading(true);
    try {
      await mediumService.enviarMensajeMedium(selectedMediumId, messageInput.trim());
      setMessageInput('');
      
      setTimeout(() => {
        loadChatAndActiveStatus();
        setLoading(false);
      }, 700);

    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR AL ENVIAR MENSAJE', 'error');
      setLoading(false);
    }
  };

  const handleSendConjecture = async () => {
    if (!selectedMediumId || !conjectureName.trim()) return showAlert('Ingresa la conjetura del nombre.', 'DATOS INCOMPLETOS', 'warning');
    setLoading(true);
    try {
      await mediumService.identificarEspiritu(selectedMediumId, conjectureName.trim());
      showAlert(`Conjetura "${conjectureName}" transmitida a servicio_probabilidad vía Kafka.`, 'CONJETURA TRANSMITIDA', 'success');
      setConjectureName('');
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR EN TRANSMISIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleAddTemplate = async () => {
    if (!templateInput.trim()) return;
    try {
      await spiritBoxService.agregarPlantilla(templateInput.trim());
      showAlert('¡Plantilla agregada correctamente en servicio_mensajeria!', 'PLANTILLA GUARDADA', 'success');
      setTemplateInput('');
      setShowTemplateModal(false);
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR AL CREAR PLANTILLA', 'error');
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
      
      {/* WALKIE-TALKIE CHAT */}
      <div className="hud-panel scanline-bg" style={{ padding: '24px', display: 'flex', flexDirection: 'column', height: '650px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid rgba(0,255,157,0.2)', pb: '12px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Radio size={24} color="var(--primary-green)" className="glow-text-green" />
            <div>
              <h2 className="font-orbitron glow-text-green" style={{ fontSize: '1.2rem', color: 'var(--primary-green)' }}>
                📻 SPIRIT BOX CONSOLE (KAFKA & MONGO)
              </h2>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                Canal de comunicación psíquica directa con los espíritus de la zona
              </span>
            </div>
          </div>

          <button className="btn-hud btn-hud-blue" onClick={loadChatAndActiveStatus}>
            <RefreshCw size={14} /> Recargar Chat
          </button>
        </div>

        {/* MEDIUM SELECTOR & COMM STATUS */}
        <div style={{ display: 'flex', gap: '16px', marginBottom: '16px', alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{ flexGrow: 1 }}>
            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'block', marginBottom: '4px' }}>
              Investigador Operador:
            </label>
            <select
              value={selectedMediumId}
              onChange={(e) => setSelectedMediumId(e.target.value)}
              style={{
                width: '100%',
                background: '#0a0e17',
                border: '1px solid var(--bg-card-border)',
                color: '#fff',
                padding: '8px 12px',
                borderRadius: '6px',
                fontFamily: 'var(--font-orbitron)'
              }}
            >
              <option value="">-- Seleccionar Medium --</option>
              {mediums.map(m => (
                <option key={m.id} value={m.id}>
                  {m.nombre} ({locations.find(l => l.id === m.ubicacionId)?.nombre || `Zona #${m.ubicacionId}`})
                </option>
              ))}
            </select>
          </div>

          <div style={{ alignSelf: 'flex-end', paddingBottom: '4px' }}>
            {activeComm ? (
              <span className="badge-hud badge-green" style={{ padding: '8px 12px' }}>
                <CheckCircle size={14} /> Enlace Psíquico Activo (Espíritu #{activeComm.espirituId})
              </span>
            ) : (
              <span className="badge-hud badge-purple" style={{ padding: '8px 12px' }}>
                <Cpu size={14} /> Esperando Señal / Sin espíritu activo
              </span>
            )}
          </div>
        </div>

        {/* CHAT LOGS */}
        <div style={{
          flexGrow: 1,
          overflowY: 'auto',
          background: 'rgba(5, 8, 14, 0.9)',
          border: '1px solid var(--bg-card-border)',
          borderRadius: '8px',
          padding: '16px',
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
          marginBottom: '16px'
        }}>
          {!selectedMediumId ? (
            <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Radio size={40} style={{ opacity: 0.3, marginBottom: '12px' }} />
              <p>Selecciona un Medium para sintonizar el canal de Spirit Box.</p>
            </div>
          ) : chatLogs.length === 0 ? (
            <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
              <p>No hay mensajes grabados en el historial de MongoDB para este Medium.</p>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>Transmite un mensaje abajo para iniciar la sesión.</span>
            </div>
          ) : (
            chatLogs.map((log, idx) => (
              <div key={log.id || idx} style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                <div style={{ alignSelf: 'flex-start', background: 'rgba(0, 229, 255, 0.1)', border: '1px solid var(--ice-blue)', borderRadius: '8px', padding: '8px 14px', maxWidth: '80%' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--ice-blue)', fontWeight: 'bold', display: 'block' }}>
                    🎤 MEDIUM #{log.mediumId} [{log.fecha ? new Date(log.fecha).toLocaleTimeString() : 'Ahora'}]
                  </span>
                  <span style={{ color: '#fff', fontSize: '0.95rem' }}>{log.contenido || log.mensaje}</span>
                </div>

                {log.respuestaEspiritu && (
                  <div style={{ alignSelf: 'flex-end', background: 'rgba(176, 38, 255, 0.15)', border: '1px solid var(--spectral-purple)', borderRadius: '8px', padding: '8px 14px', maxWidth: '80%' }}>
                    <span style={{ fontSize: '0.75rem', color: 'var(--spectral-purple)', fontWeight: 'bold', display: 'block' }}>
                      👻 ESPÍRITU RESPUESTA:
                    </span>
                    <span className="glow-text-purple" style={{ color: '#fff', fontSize: '1rem', fontStyle: 'italic' }}>
                      "{log.respuestaEspiritu}"
                    </span>
                  </div>
                )}
              </div>
            ))
          )}
        </div>

        {/* INPUT FORM */}
        <form onSubmit={handleSendMessage} style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            placeholder={selectedMediumId ? "Habla al Spirit Box (ej: ¿Dónde estás? ¿Quién eres?)..." : "Selecciona un Medium..."}
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            disabled={!selectedMediumId || loading}
            style={{
              flexGrow: 1,
              background: '#0a0e17',
              border: '1px solid var(--primary-green)',
              color: '#fff',
              padding: '12px 16px',
              borderRadius: '6px',
              fontSize: '0.95rem'
            }}
          />
          <button type="submit" className="btn-hud" disabled={!selectedMediumId || !messageInput.trim() || loading} style={{ padding: '0 20px' }}>
            <Send size={16} /> Transmitir
          </button>
        </form>
      </div>

      {/* SIDEBAR: PROBABILITY & TEMPLATES */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div className="hud-panel" style={{ padding: '20px' }}>
          <h3 className="font-orbitron" style={{ color: 'var(--ice-blue)', fontSize: '1rem', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <HelpCircle size={18} /> Radar de Identificación
          </h3>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
            Envía una conjetura. El <code>servicio_probabilidad</code> evaluará la coincidencia vía Kafka.
          </p>

          <div style={{ display: 'grid', gap: '10px' }}>
            <input
              type="text"
              placeholder="Nombre arriesgado..."
              value={conjectureName}
              onChange={(e) => setConjectureName(e.target.value)}
              style={{ background: '#0a0e17', border: '1px solid var(--bg-card-border)', color: '#fff', padding: '8px 12px', borderRadius: '4px', fontSize: '0.85rem' }}
            />
            <button className="btn-hud btn-hud-blue" onClick={handleSendConjecture} disabled={loading || !selectedMediumId || !conjectureName.trim()} style={{ justifyContent: 'center' }}>
              Evaluar Conjetura
            </button>
          </div>
        </div>

        <div className="hud-panel" style={{ padding: '20px' }}>
          <h3 className="font-orbitron" style={{ color: 'var(--primary-green)', fontSize: '1rem', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FileText size={18} /> Plantillas de Respuestas
          </h3>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
            Agrega respuestas personalizadas al <code>servicio_mensajeria</code> usando placeholders:
          </p>
          <ul style={{ fontSize: '0.75rem', color: 'var(--ice-blue)', paddingLeft: '16px', marginBottom: '14px' }}>
            <li><code>{'{INICIAL}'}</code> - Inicial del espíritu</li>
            <li><code>{'{LONGITUD}'}</code> - Cantidad de letras</li>
          </ul>

          <button className="btn-hud" onClick={() => setShowTemplateModal(true)} style={{ width: '100%', justifyContent: 'center' }}>
            + Nueva Plantilla
          </button>
        </div>
      </div>

      {showTemplateModal && (
        <div className="modal-overlay">
          <div className="hud-panel" style={{ width: '400px', padding: '24px' }}>
            <h3 className="font-orbitron glow-text-green" style={{ color: 'var(--primary-green)', marginBottom: '16px' }}>
              Nueva Plantilla de Respuesta
            </h3>
            <textarea
              rows="4"
              placeholder="Ej: Mi nombre empieza con la letra {INICIAL} y tiene {LONGITUD} letras..."
              value={templateInput}
              onChange={(e) => setTemplateInput(e.target.value)}
              style={{ width: '100%', background: '#0a0e17', border: '1px solid var(--primary-green)', color: '#fff', padding: '12px', borderRadius: '6px', fontSize: '0.9rem', marginBottom: '16px' }}
            />
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button className="btn-hud btn-hud-danger" onClick={() => setShowTemplateModal(false)}>Cancelar</button>
              <button className="btn-hud" onClick={handleAddTemplate}>Guardar Plantilla</button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
