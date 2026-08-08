import React, { useState, useEffect } from 'react';
import { telemetryService } from '../services/telemetryService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from '../components/ModalProvider';
import { Activity, ShieldAlert, Cpu, RefreshCw } from 'lucide-react';

export function TelemetryPage() {
  const { showAlert } = useModal();
  const [corruptSanctuary, setCorruptSanctuary] = useState(null);

  const [sensorAverages, setSensorAverages] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchTelemetryData();
  }, []);

  const fetchTelemetryData = async () => {
    setLoading(true);
    try {
      const sanctuary = await telemetryService.getSantuarioCorrupto();
      setCorruptSanctuary(sanctuary);

      const averages = await telemetryService.getPromedioSensores();
      setSensorAverages(averages || []);
    } catch (e) {
      console.warn("Error cargando telemetría:", e);
    } finally {
      setLoading(false);
    }
  };

  const handleNormalizeSensors = async () => {
    setLoading(true);
    try {
      await telemetryService.normalizarDatosSensor();
      showAlert('¡Datos de sensores normalizados correctamente en la base de datos!', 'NORMALIZACIÓN EXITOSA', 'success');
      fetchTelemetryData();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE TELEMETRÍA', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
      
      {/* SANCTUARY CORRUPTION REPORT */}
      <div className="hud-panel" style={{ padding: '24px' }}>
        <h2 className="font-orbitron glow-text-red" style={{ fontSize: '1.2rem', color: 'var(--demonic-red)', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '10px' }}>
          <ShieldAlert size={24} /> ALERTA: SANTUARIO MÁS CORRUPTO
        </h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
          Detección automática de la ubicación de tipo Santuario con mayor concentración de espíritus demoníacos.
        </p>

        {!corruptSanctuary || !corruptSanctuary.nombre ? (
          <p style={{ color: 'var(--text-muted)' }}>No se registraron santuarios corruptos o base de datos vacía.</p>
        ) : (
          <div style={{ background: 'rgba(255, 42, 95, 0.1)', border: '1px solid var(--demonic-red)', padding: '20px', borderRadius: '12px' }}>
            <h3 className="font-orbitron" style={{ fontSize: '1.3rem', color: '#fff', marginBottom: '8px' }}>
              {corruptSanctuary.nombre}
            </h3>
            <div style={{ fontSize: '0.95rem', color: 'var(--demonic-red)', display: 'grid', gap: '6px' }}>
              <div>Demons Registrados: <strong>{corruptSanctuary.cantidadDeDemonios ?? 0}</strong></div>
              <div>Porcentaje de Corrupción: <strong>{corruptSanctuary.porcentajeCorrupcion?.toFixed(1) ?? 100}%</strong></div>
            </div>
          </div>
        )}
      </div>

      {/* SENSOR AVERAGES & DATA NORMALIZER */}
      <div className="hud-panel" style={{ padding: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 className="font-orbitron glow-text-green" style={{ fontSize: '1.2rem', color: 'var(--primary-green)', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Cpu size={24} /> TELEMETRÍA DE SENSORES
          </h2>
          <button className="btn-hud btn-hud-blue" onClick={fetchTelemetryData} disabled={loading}>
            <RefreshCw size={14} /> Actualizar
          </button>
        </div>

        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '16px' }}>
          Métricas promediadas por tipo de sensor y ejecutador de normalización de lecturas crudas.
        </p>

        <div style={{ display: 'grid', gap: '10px', marginBottom: '20px' }}>
          {sensorAverages.length === 0 ? (
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Sin lecturas de sensores procesadas.</p>
          ) : (
            sensorAverages.map((item, idx) => (
              <div key={idx} style={{ background: 'rgba(0, 229, 255, 0.1)', border: '1px solid var(--ice-blue)', padding: '10px 14px', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontWeight: 'bold', color: 'var(--ice-blue)' }}>{item.tipoSensor || item.sensorType}</span>
                <span className="badge-hud badge-green">Promedio: {item.promedio?.toFixed(2) ?? '0.00'}</span>
              </div>
            ))
          )}
        </div>

        <button className="btn-hud" onClick={handleNormalizeSensors} disabled={loading} style={{ width: '100%', justifyContent: 'center', padding: '12px' }}>
          <Activity size={16} /> Normalizar Datos de Sensores
        </button>
      </div>

    </div>
  );
}
