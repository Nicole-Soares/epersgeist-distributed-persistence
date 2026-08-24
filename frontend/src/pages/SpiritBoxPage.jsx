import React, { useState, useEffect } from 'react';
import { mediumService } from '../services/mediumService';
import { spiritService } from '../services/spiritService';
import { spiritBoxService } from '../services/spiritBoxService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from '../components/ModalProvider';
import { Radio, Send, RefreshCw, Cpu, CheckCircle, Zap, Ghost } from 'lucide-react';

function getSmartSpiritResponse(log, activeSpirit) {
  if (!log.respuestaEspiritu) return '';
  const userMsg = (log.mensajeMedium || log.mensaje || log.contenido || '').toLowerCase().trim();
  const rawResponse = log.respuestaEspiritu;
  const isAngel = activeSpirit?.tipo === 'ANGEL' || activeSpirit?.tipo === 'ANGELICAL';

  // Extraer pista de la respuesta cruda del backend si existe (ej: "Mi nombre es: Ban.")
  let cluePart = '';
  if (rawResponse.includes(':')) {
    cluePart = rawResponse.split(':').pop().trim();
  } else if (rawResponse.includes('...')) {
    cluePart = rawResponse.split('...').pop().trim();
  }

  const clueSuffix = cluePart ? ` (Pista de nombre: ${cluePart})` : '';

  // REACCIÓN DE ENOJO / PROVOCACIÓN
  if (userMsg.includes('maldit') || userMsg.includes('odio') || userMsg.includes('tonto') || userMsg.includes('vete') || userMsg.includes('callate') || userMsg.includes('feo') || userMsg.includes('estupido') || userMsg.includes('muerete')) {
    return `¡Tu arrogancia me enfurece! Has perturbado mi calma y debilitado nuestro lazo psíquico. 💔 (-10 Conexión)`;
  }

  // REACCIÓN DE FELICIDAD / DEVOCIÓN
  if (userMsg.includes('gracias') || userMsg.includes('paz') || userMsg.includes('amigo') || userMsg.includes('te quiero') || userMsg.includes('bendicion') || userMsg.includes('luz') || userMsg.includes('por favor') || userMsg.includes('hermoso')) {
    return `Siento la devoción y pureza en tus palabras... He llamado a otro espíritu de la zona para que se una a tu causa. ✨ (Espíritu invocado)`;
  }

  // Preguntas sobre la muerte / historia
  if (userMsg.includes('muer') || userMsg.includes('muri') || userMsg.includes('falleci') || userMsg.includes('mat')) {
    if (isAngel) {
      return `Trascendí este plano terrenal protegiendo a los inocentes... Mi espíritu aún guarda su luz.${clueSuffix}`;
    } else {
      return `Un pacto prohibido terminó con mi vida física... Ahora mi alma vaga llena de ira en las sombras.${clueSuffix}`;
    }
  }

  // Preguntas sobre ubicación
  if (userMsg.includes('donde') || userMsg.includes('dónde') || userMsg.includes('lugar') || userMsg.includes('ubicacion')) {
    return `Estoy manifestándome justo aquí a pocos metros tuyo en esta misma zona... Siento la calidez de tu vida.${clueSuffix}`;
  }

  // Preguntas sobre identidad / nombre
  if (userMsg.includes('quien') || userMsg.includes('quién') || userMsg.includes('nombre') || userMsg.includes('llamas')) {
    return `Mi nombre terrenal yació olvidado en las tumbas... ${cluePart ? `Mis susurros deletrean: ${cluePart}` : 'No me recuerdan.'}`;
  }

  // Preguntas sobre hostilidad / intención
  if (userMsg.includes('bueno') || userMsg.includes('malo') || userMsg.includes('peligro')) {
    if (isAngel) {
      return `No temas, investigador. Vengo guiado por la energía divina para traerte protección.${clueSuffix}`;
    } else {
      return `Las sombras me dominan... ¡Tu energía vital me tienta y mi hostilidad crece!${clueSuffix}`;
    }
  }

  // Saludos o mensajes generales
  if (userMsg.includes('hola') || userMsg.includes('saludo') || userMsg.includes('escuchas')) {
    return `Te escucho a través del canal de Spirit Box... Háblame, investigador.${clueSuffix}`;
  }

  return rawResponse;
}

export function SpiritBoxPage({ mediums = [], locations = [], spirits = [], onRefresh }) {
  const { showAlert } = useModal();
  const [selectedMediumId, setSelectedMediumId] = useState('');
  const [selectedSpiritId, setSelectedSpiritId] = useState('');

  const [messageInput, setMessageInput] = useState('');
  const [chatLogs, setChatLogs] = useState([]);
  const [activeComm, setActiveComm] = useState(null);
  const [loading, setLoading] = useState(false);

  const selectedMedium = mediums.find(m => m.id === Number(selectedMediumId));
  const locationId = selectedMedium?.ubicacionId;

  // Espíritus disponibles en la misma ubicación o ya conectados al medium
  const availableSpirits = spirits.filter(s => 
    selectedMedium && (s.mediumId === selectedMedium.id || s.ubicacionId === selectedMedium.ubicacionId)
  );

  useEffect(() => {
    if (selectedMediumId) {
      loadChatAndActiveStatus();
    } else {
      setChatLogs([]);
      setActiveComm(null);
      setSelectedSpiritId('');
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
        if (comm?.espirituId) {
          setSelectedSpiritId(String(comm.espirituId));
        }
      }
    } catch (e) {
      console.warn("Error cargando mensajería:", e);
    }
  };

  const handleConnectSpirit = async (spiritIdToConnect) => {
    const sId = spiritIdToConnect || selectedSpiritId;
    if (!selectedMediumId || !sId) return;

    setLoading(true);
    try {
      await mediumService.invocarEspiritu(selectedMediumId, sId);
      showAlert('¡Canal de comunicación sintonizado con el espíritu seleccionado!', 'CANAL SINTONIZADO', 'success');
      if (onRefresh) onRefresh();
      await loadChatAndActiveStatus();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR AL SINTONIZAR CANAL', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!selectedMediumId || !messageInput.trim()) return;

    const trimmedMsg = messageInput.trim();
    const lowerMsg = trimmedMsg.toLowerCase();
    setMessageInput('');
    setLoading(true);

    try {
      await mediumService.enviarMensajeMedium(selectedMediumId, trimmedMsg);

      // Evaluar mecánica de felicidad o enojo
      const isHappy = lowerMsg.includes('gracias') || lowerMsg.includes('paz') || lowerMsg.includes('amigo') || lowerMsg.includes('te quiero') || lowerMsg.includes('bendicion') || lowerMsg.includes('luz') || lowerMsg.includes('por favor') || lowerMsg.includes('hermoso');
      const isAngry = lowerMsg.includes('maldit') || lowerMsg.includes('odio') || lowerMsg.includes('tonto') || lowerMsg.includes('vete') || lowerMsg.includes('callate') || lowerMsg.includes('feo') || lowerMsg.includes('estupido') || lowerMsg.includes('muerete');

      if (isHappy && selectedMedium) {
        // Buscar un espíritu libre en la zona para invocarlo y unirlo al médium
        const freeSpirit = spirits.find(s => (!s.mediumId || s.mediumId !== selectedMedium.id) && (s.ubicacionId === selectedMedium.ubicacionId || !s.ubicacionId));
        if (freeSpirit) {
          try {
            await mediumService.invocarEspiritu(selectedMedium.id, freeSpirit.id);
            showAlert(`✨ ¡El espíritu sintió tu paz y felicidad! Ha convocado a "${freeSpirit.nombre}" para unirse a tu Médium.`, '¡ESPÍRITU ALIADO CONECTADO!', 'success');
            if (onRefresh) onRefresh();
          } catch (e) {
            console.warn("No se pudo invocar espíritu libre:", e);
          }
        }
      } else if (isAngry) {
        const activeSpirit = spirits.find(s => s.id === (activeComm?.espirituId || Number(selectedSpiritId)));
        if (activeSpirit) {
          try {
            const newConn = Math.max(0, (activeSpirit.nivelDeConexion ?? 50) - 10);
            await spiritService.actualizarEspiritu(activeSpirit.id, { nombre: activeSpirit.nombre, nivelDeConexion: newConn });
            showAlert(`💔 ¡El espíritu "${activeSpirit.nombre}" se enfureció! Su nivel de conexión bajó de ${activeSpirit.nivelDeConexion}% a ${newConn}%.`, 'RECHAZO DE CONEXIÓN', 'warning');
            if (onRefresh) onRefresh();
          } catch (e) {
            console.warn("Error reduciendo conexión en backend:", e);
            showAlert('💔 ¡El espíritu se enfureció por tus palabras! El lazo psíquico se debilitó (-10 Conexión).', 'RECHAZO DE CONEXIÓN', 'warning');
          }
        } else {
          showAlert('💔 ¡El espíritu se enfureció por tus palabras! El lazo psíquico se debilitó (-10 Conexión).', 'RECHAZO DE CONEXIÓN', 'warning');
        }
      }

      setTimeout(() => {
        loadChatAndActiveStatus();
        setLoading(false);
      }, 700);

    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR AL ENVIAR MENSAJE', 'error');
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
      
      {/* WALKIE-TALKIE CHAT */}
      <div className="hud-panel scanline-bg" style={{ padding: '24px', display: 'flex', flexDirection: 'column', minHeight: '650px' }}>
        
        {/* HEADER */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid rgba(0,255,157,0.2)', paddingBottom: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Radio size={28} color="var(--primary-green)" className="glow-text-green" />
            <div>
              <h2 className="font-orbitron glow-text-green" style={{ fontSize: '1.3rem', color: 'var(--primary-green)' }}>
                📻 SPIRIT BOX CONSOLE
              </h2>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Canal de comunicación psíquica en tiempo real
              </span>
            </div>
          </div>

          <button className="btn-hud btn-hud-blue" onClick={loadChatAndActiveStatus} disabled={loading}>
            <RefreshCw size={14} /> Recargar Chat
          </button>
        </div>

        {/* SELECTORES DE MEDIUM Y ESPÍRITU */}
        <div className="responsive-three-column-grid" style={{ marginBottom: '20px' }}>
          
          {/* SELECTOR DE MEDIUM */}
          <div>
            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>
              🎤 Investigador Operador (Medium):
            </label>
            <select
              value={selectedMediumId}
              onChange={(e) => setSelectedMediumId(e.target.value)}
              style={{
                width: '100%',
                background: '#0a0e17',
                border: '1px solid var(--bg-card-border)',
                color: '#fff',
                padding: '10px 12px',
                borderRadius: '6px',
                fontFamily: 'var(--font-orbitron)',
                fontSize: '0.9rem'
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

          {/* SELECTOR DE ESPÍRITU OBJETIVO */}
          <div>
            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>
              👻 Espíritu a Conectar:
            </label>
            <select
              value={selectedSpiritId}
              onChange={(e) => setSelectedSpiritId(e.target.value)}
              disabled={!selectedMediumId || availableSpirits.length === 0}
              style={{
                width: '100%',
                background: '#0a0e17',
                border: '1px solid var(--bg-card-border)',
                color: '#fff',
                padding: '10px 12px',
                borderRadius: '6px',
                fontFamily: 'var(--font-orbitron)',
                fontSize: '0.9rem'
              }}
            >
              <option value="">
                {!selectedMediumId ? '-- Selecciona un Medium primero --' : availableSpirits.length === 0 ? 'Sin espíritus en esta zona' : '-- Seleccionar Espíritu --'}
              </option>
              {availableSpirits.map(s => (
                <option key={s.id} value={s.id}>
                  {s.nombre} ({s.tipo}) {s.mediumId === selectedMedium?.id ? ' (Conectado)' : ''}
                </option>
              ))}
            </select>
          </div>

          {/* BOTÓN CONECTAR CANAL */}
          <button
            className="btn-hud btn-hud-purple"
            onClick={() => handleConnectSpirit()}
            disabled={!selectedMediumId || !selectedSpiritId || loading}
            style={{ height: '42px', padding: '0 16px', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '6px' }}
          >
            <Zap size={14} /> Sintonizar Canal
          </button>
        </div>

        {/* STATUS BADGE */}
        <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          {activeComm ? (
            <span className="badge-hud badge-green" style={{ padding: '8px 14px', fontSize: '0.85rem' }}>
              <CheckCircle size={15} /> Enlace Psíquico Sintonizado: <strong>{spirits.find(s => s.id === activeComm.espirituId)?.nombre || `Espíritu #${activeComm.espirituId}`}</strong>
            </span>
          ) : (
            <span className="badge-hud badge-purple" style={{ padding: '8px 14px', fontSize: '0.85rem' }}>
              <Cpu size={15} /> Sin comunicación activa sintonizada. Selecciona un espíritu y presiona "Sintonizar Canal".
            </span>
          )}
        </div>

        {/* CHAT LOGS */}
        <div style={{
          flexGrow: 1,
          overflowY: 'auto',
          background: 'rgba(5, 8, 14, 0.95)',
          border: '1px solid var(--bg-card-border)',
          borderRadius: '8px',
          padding: '20px',
          display: 'flex',
          flexDirection: 'column',
          gap: '14px',
          marginBottom: '20px',
          minHeight: '350px'
        }}>
          {!selectedMediumId ? (
            <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Radio size={48} style={{ opacity: 0.3, marginBottom: '12px' }} />
              <p style={{ fontSize: '1rem' }}>Selecciona un Medium para iniciar el canal de Spirit Box.</p>
            </div>
          ) : chatLogs.length === 0 ? (
            <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Ghost size={48} style={{ opacity: 0.3, marginBottom: '12px' }} />
              <p style={{ fontSize: '1rem', color: '#fff' }}>No hay mensajes grabados en el historial para {selectedMedium?.nombre}.</p>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'block', marginTop: '6px' }}>
                Transmite un mensaje abajo para hablar al Spirit Box.
              </span>
            </div>
          ) : (
            chatLogs.map((log, idx) => (
              <div key={log.id || idx} style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                
                {/* MENSAJE DEL MEDIUM */}
                <div style={{ alignSelf: 'flex-start', background: 'rgba(0, 229, 255, 0.1)', border: '1px solid var(--ice-blue)', borderRadius: '8px', padding: '10px 16px', maxWidth: '80%' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--ice-blue)', fontWeight: 'bold', display: 'block', marginBottom: '4px' }}>
                    🎤 {selectedMedium?.nombre || 'INVESTIGADOR'} [{log.fecha ? new Date(log.fecha).toLocaleTimeString() : 'Ahora'}]
                  </span>
                  <span style={{ color: '#fff', fontSize: '0.95rem' }}>
                    {log.mensajeMedium || log.mensaje || log.contenido || 'Transmisión psíquica enviada...'}
                  </span>
                </div>

                {/* RESPUESTA DEL ESPÍRITU */}
                {log.respuestaEspiritu && (
                  <div style={{ alignSelf: 'flex-end', background: 'rgba(176, 38, 255, 0.15)', border: '1px solid var(--spectral-purple)', borderRadius: '8px', padding: '10px 16px', maxWidth: '80%' }}>
                    <span style={{ fontSize: '0.75rem', color: 'var(--spectral-purple)', fontWeight: 'bold', display: 'block', marginBottom: '4px' }}>
                      👻 RESPUESTA DEL ESPÍRITU:
                    </span>
                    <span className="glow-text-purple" style={{ color: '#fff', fontSize: '0.95rem', fontStyle: 'italic', lineHeight: '1.4' }}>
                      "{getSmartSpiritResponse(log, spirits.find(s => s.id === (activeComm?.espirituId || Number(selectedSpiritId))))}"
                    </span>
                  </div>
                )}
              </div>
            ))
          )}
        </div>

        {/* INPUT FORM */}
        <form onSubmit={handleSendMessage} style={{ display: 'flex', gap: '12px' }}>
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
              padding: '14px 18px',
              borderRadius: '6px',
              fontSize: '1rem'
            }}
          />
          <button type="submit" className="btn-hud" disabled={!selectedMediumId || !messageInput.trim() || loading} style={{ padding: '0 24px', fontSize: '0.95rem' }}>
            <Send size={18} /> Transmitir
          </button>
        </form>

      </div>
    </div>
  );
}
