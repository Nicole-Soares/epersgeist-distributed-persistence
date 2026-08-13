import React, { useState } from 'react';
import { mediumService } from '../services/mediumService';
import { spiritService } from '../services/spiritService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from './ModalProvider';
import { Swords, Flame, Sparkles, User, Crosshair, AlertTriangle, CheckCircle } from 'lucide-react';

export function ExorcismChamber({ mediums, spirits, onRefresh }) {
  const { showAlert } = useModal();
  const [exorcistId, setExorcistId] = useState('');
  const [victimId, setVictimId] = useState('');
  const [loading, setLoading] = useState(false);
  const [battleLog, setBattleLog] = useState([]);

  const exorcistMedium = mediums.find(m => m.id === Number(exorcistId));
  const victimMedium = mediums.find(m => m.id === Number(victimId));

  const exorcistAngels = spirits.filter(s => 
    (s.tipo === 'ANGEL' || s.tipo === 'ANGELICAL') && (s.mediumId === Number(exorcistId) || (exorcistMedium?.espiritus || []).some(e => e.id === s.id))
  );

  const victimDemons = spirits.filter(s => 
    s.tipo === 'DEMONIO' && (s.mediumId === Number(victimId) || (victimMedium?.espiritus || []).some(e => e.id === s.id))
  );
  const isSameLocation = exorcistMedium && victimMedium && (exorcistMedium.ubicacionId === victimMedium.ubicacionId);

  const handleExorcism = async () => {
    if (!exorcistId || !victimId) {
      return showAlert('Selecciona un Medium Exorcista y un Medium Poseído.', 'SELECCIÓN INCOMPLETA', 'warning');
    }
    if (exorcistId === victimId) {
      return showAlert('El exorcista y la víctima no pueden ser la misma persona.', 'CONFLICTO DE OBJETIVOS', 'warning');
    }

    if (exorcistAngels.length === 0) {
      setBattleLog([
        `⚠️ RITUAL IMPOSIBLE: El Médium ${exorcistMedium?.nombre} no tiene Ángeles celestiales enlazados.`,
        `💡 Sugerencia: Ve al Compendio de Espíritus y conecta un Ángel a ${exorcistMedium?.nombre} antes de exorcizar.`
      ]);
      return showAlert(`El exorcista ${exorcistMedium?.nombre} no posee ningún Ángel enlazado para atacar.`, 'SIN ÁNGELES DISPONIBLES', 'warning');
    }

    if (!isSameLocation) {
      setBattleLog([
        `⚠️ RITUAL IMPOSIBLE: Ambos Médiums están en ubicaciones diferentes.`,
        `📍 Exorcista (${exorcistMedium?.nombre}) está en Ubicación #${exorcistMedium?.ubicacionId}.`,
        `📍 Víctima (${victimMedium?.nombre}) está en Ubicación #${victimMedium?.ubicacionId}.`,
        `💡 Sugerencia: Mueve a ambos Médiums a la misma ubicación desde el Mapa.`
      ]);
      return showAlert(`El Exorcista y la Víctima deben encontrarse en la misma ubicación para realizar el ritual.`, 'UBICACIÓN DIFERENTE', 'warning');
    }

    if (victimDemons.length === 0) {
      setBattleLog([
        `ℹ️ RITUAL SIN INVASORES: El Médium ${victimMedium?.nombre} no tiene demonios o entidades malignas enlazadas.`,
        `✨ La víctima se encuentra completamente limpia de toda posesión.`
      ]);
      return showAlert(`El médium ${victimMedium?.nombre} no posee demonios que exorcizar.`, 'MÉDIUM LIMPIO', 'info');
    }

    setLoading(true);
    setBattleLog([
      `⚡ INICIANDO RITUAL DE EXORCISMO SANTO...`,
      `✝️ Exorcista: ${exorcistMedium?.nombre} (Ángeles: ${exorcistAngels.map(a => a.nombre).join(', ')})`,
      `🎯 Víctima: ${victimMedium?.nombre} (Demonios a purificar: ${victimDemons.map(d => d.nombre).join(', ')})`,
      `🔥 Canatando plegarias celestiales y desplegando combate espiritual...`
    ]);

    // Guardar estado previo de los espíritus
    const snapshotAngels = exorcistAngels.map(a => ({ id: a.id, nombre: a.nombre, conn: a.nivelDeConexion ?? 0 }));
    const snapshotDemons = victimDemons.map(d => ({ id: d.id, nombre: d.nombre, conn: d.nivelDeConexion ?? 0 }));

    try {
      await mediumService.exorcizar(exorcistId, victimId);

      // Re-obtener la lista fresca de espíritus para comparar resultados de combate
      let freshSpirits = [];
      try {
        freshSpirits = await spiritService.getEspiritus();
      } catch (e) {
        freshSpirits = spirits;
      }

      setTimeout(() => {
        const battleDetails = [];
        let outcomeType = 'info';

        // Evaluar resultado en Demonios
        snapshotDemons.forEach(oldD => {
          const freshD = freshSpirits.find(s => s.id === oldD.id);
          if (!freshD || freshD.mediumId !== victimMedium.id) {
            battleDetails.push(`✨ ¡DEMONIO PURIFICADO! "${oldD.nombre}" perdió toda su conexión y fue expulsado de ${victimMedium.nombre}.`);
            outcomeType = 'success';
          } else if ((freshD.nivelDeConexion ?? 0) < oldD.conn) {
            const damage = oldD.conn - freshD.nivelDeConexion;
            battleDetails.push(`🔥 ¡ATAQUE CELESTIAL EXITOSO! El demonio "${oldD.nombre}" sufrió -${damage}% de daño (Conexión: ${oldD.conn}% ➔ ${freshD.nivelDeConexion}%).`);
            if (outcomeType !== 'success') outcomeType = 'success';
          }
        });

        // Evaluar resultado en Ángeles (si fueron repelidos)
        snapshotAngels.forEach(oldA => {
          const freshA = freshSpirits.find(s => s.id === oldA.id);
          if (freshA && (freshA.nivelDeConexion ?? 0) < oldA.conn) {
            const pen = oldA.conn - freshA.nivelDeConexion;
            battleDetails.push(`🛡️ ¡DEFENSA DEMONÍACA REPELIÓ EL ATAQUE! El Ángel "${oldA.nombre}" fue penalizado por fallo (-${pen}% Conexión: ${oldA.conn}% ➔ ${freshA.nivelDeConexion}%).`);
            if (outcomeType === 'info') outcomeType = 'warning';
          }
        });

        if (battleDetails.length === 0) {
          battleDetails.push(`⚡ RITUAL COMPLETO: Las fuerzas celestiales colisionaron contra el escudo demoníaco sin alterar conexiones.`);
        }

        setBattleLog(prev => [
          ...prev,
          `💥 RESULTADOS DEL COMBATE ESPIRITUAL:`,
          ...battleDetails,
          `🛡️ Ritual concluido.`
        ]);

        const summaryText = battleDetails.join(' | ');
        showAlert(summaryText, outcomeType === 'success' ? '¡EXORCISMO EXITOSO!' : 'RITUAL FINALIZADO', outcomeType);

        if (onRefresh) onRefresh();
        setLoading(false);
      }, 700);

    } catch (err) {
      const errorMsg = formatErrorMessage(err);
      setBattleLog(prev => [
        ...prev,
        `❌ NO SE PUDO EXORCIZAR:`,
        `⛔ ${errorMsg}`
      ]);
      showAlert(`Fallo en el ritual: ${errorMsg}`, 'ERROR EN EXORCISMO', 'error');
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
              {mediums.map(m => {
                const numAngels = spirits.filter(s => (s.tipo === 'ANGEL' || s.tipo === 'ANGELICAL') && (s.mediumId === m.id || (m.espiritus || []).some(e => e.id === s.id))).length;
                return (
                  <option key={m.id} value={m.id}>
                    {m.nombre} (👼 {numAngels} Ángeles)
                  </option>
                );
              })}
            </select>
            {exorcistMedium && (
              <div style={{ fontSize: '0.75rem', marginTop: '6px', color: exorcistAngels.length > 0 ? 'var(--primary-green)' : 'var(--amber-gold)' }}>
                {exorcistAngels.length > 0 ? (
                  <span>✅ Ángeles listos: {exorcistAngels.map(a => a.nombre).join(', ')}</span>
                ) : (
                  <span>⚠️ Atención: Este Médium NO tiene Ángeles para atacar.</span>
                )}
              </div>
            )}
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
              {mediums.filter(m => m.id !== Number(exorcistId)).map(m => {
                const numDemons = spirits.filter(s => s.tipo === 'DEMONIO' && (s.mediumId === m.id || (m.espiritus || []).some(e => e.id === s.id))).length;
                return (
                  <option key={m.id} value={m.id}>
                    {m.nombre} (😈 {numDemons} Demonios)
                  </option>
                );
              })}
            </select>
            {victimMedium && (
              <div style={{ fontSize: '0.75rem', marginTop: '6px', color: 'var(--text-muted)' }}>
                <span>😈 Demonios enlazados: {victimDemons.length > 0 ? victimDemons.map(d => d.nombre).join(', ') : 'Ninguno (Limpio)'}</span>
              </div>
            )}
          </div>

          {exorcistMedium && victimMedium && !isSameLocation && (
            <div style={{ background: 'rgba(255, 170, 0, 0.1)', border: '1px solid var(--amber-gold)', padding: '10px', borderRadius: '6px', fontSize: '0.8rem', color: 'var(--amber-gold)', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <AlertTriangle size={18} />
              <div>
                Ambos médiums están en ubicaciones distintas. Muévelos a la misma zona en el Mapa para poder exorcizar.
              </div>
            </div>
          )}

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
                color: log.includes('FALLO') || log.includes('IMPOSIBLE') || log.includes('⛔')
                  ? 'var(--demonic-red)' 
                  : log.includes('ÉXITO') || log.includes('COMPLETADO') 
                  ? 'var(--primary-green)' 
                  : log.includes('⚠️') || log.includes('💡')
                  ? 'var(--amber-gold)'
                  : 'var(--text-main)'
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

