import React, { useState, useEffect } from 'react';
import { locationService } from '../services/locationService';
import { mediumService } from '../services/mediumService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from '../components/ModalProvider';
import { MapPin, Thermometer, Zap, Compass, ArrowRight, ShieldAlert, Route, RefreshCw } from 'lucide-react';

export function MapPage({ locations, mediums, onRefresh, selectedLocation, setSelectedLocation }) {
  const { showAlert } = useModal();
  const [selectedMediumId, setSelectedMediumId] = useState('');
  const [targetLocId, setTargetLocId] = useState('');
  const [pathResult, setPathResult] = useState(null);
  const [pathType, setPathType] = useState('');
  const [overloadedLocs, setOverloadedLocs] = useState([]);
  const [loadingAction, setLoadingAction] = useState(false);
  const [locationDetails, setLocationDetails] = useState({});

  useEffect(() => {
    if (locations.length > 0) {
      fetchLocationDetails();
    }
  }, [locations]);

  const fetchLocationDetails = async () => {
    const details = {};
    for (const loc of locations) {
      try {
        const espiritus = await locationService.getEspiritusEnUbicacion(loc.id);
        const freeMediums = await locationService.getMediumsSinEspiritusEnUbicacion(loc.id);
        details[loc.id] = { espiritus: espiritus || [], freeMediums: freeMediums || [] };
      } catch (e) {
        console.warn(`Error obteniendo detalles de zona ${loc.id}`, e);
      }
    }
    setLocationDetails(details);
  };

  const handleMoveMedium = async (loc) => {
    if (!selectedMediumId) return showAlert('Selecciona un Medium de la lista desplegable.', 'SELECCIÓN REQUERIDA', 'warning');

    // Verificar si el médium ya se encuentra en la ubicación elegida
    const med = mediums.find(m => m.id === Number(selectedMediumId));
    const currentLocId = med?.ubicacionId || med?.ubicacion?.id;
    if (med && currentLocId === loc.id) {
      return showAlert(`El médium ${med.nombre} ya se encuentra en la ubicación "${loc.nombre}".`, 'UBICACIÓN ACTUAL', 'warning');
    }

    setLoadingAction(true);
    try {
      // Los vértices en el JSON tienen los campos swapeados vs lo que espera el backend
      // El JSON devuelve: { longitud: ~-34, latitud: ~-58 } (están invertidos en la DB)
      // El endpoint mover(latitud, longitud) espera: latitud ~-34, longitud ~-58
      // Por eso usamos v.longitud como latitud y v.latitud como longitud
      const verts = loc.vertices ?? [];
      // v.latitud es la latitud real (~-34) y v.longitud es la longitud real (~-58)
      let lat = -34.71;
      let lng = -58.28;
      if (verts.length > 0) {
        const avg1 = verts.reduce((a, v) => a + (v.latitud ?? 0), 0) / verts.length;
        const avg2 = verts.reduce((a, v) => a + (v.longitud ?? 0), 0) / verts.length;
        // Identificar cuál promedio es latitud (~-34) y cuál es longitud (~-58)
        if (Math.abs(avg1) < Math.abs(avg2)) {
          lat = avg1;
          lng = avg2;
        } else {
          lat = avg2;
          lng = avg1;
        }
      }
      await mediumService.moverMedium(selectedMediumId, lat, lng);
      showAlert(`Medium movido exitosamente a ${loc.nombre}`, 'DESPLAZAMIENTO EXITOSO', 'success');
      onRefresh();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'RESTRICCIÓN DE MOVIMIENTO', 'error');
    } finally {
      setLoadingAction(false);
    }
  };

  const calculateShortestPath = async () => {
    if (!selectedLocation || !targetLocId) return showAlert('Selecciona una Ubicación Origen y Destino.', 'SELECCIÓN REQUERIDA', 'warning');
    setLoadingAction(true);
    try {
      const res = await locationService.getCaminoMasCorto(selectedLocation.id, targetLocId);
      setPathResult(res);
      setPathType('Camino Más Corto (Menor número de saltos)');
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR EN RUTA', 'error');
    } finally {
      setLoadingAction(false);
    }
  };

  const calculateProfitablePath = async () => {
    if (!selectedLocation || !targetLocId) return showAlert('Selecciona una Ubicación Origen y Destino.', 'SELECCIÓN REQUERIDA', 'warning');
    setLoadingAction(true);
    try {
      const res = await locationService.getCaminoMasRentable(selectedLocation.id, targetLocId);
      setPathResult(res);
      setPathType('Camino Más Rentable (Maximización de Energía / Menor costo)');
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR EN RUTA', 'error');
    } finally {
      setLoadingAction(false);
    }
  };

  const fetchOverloaded = async () => {
    setLoadingAction(true);
    try {
      const res = await locationService.getUbicacionesSobrecargadas(2);
      setOverloadedLocs(res);
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE TELEMETRÍA', 'error');
    } finally {
      setLoadingAction(false);
    }
  };

  return (
    <div className="responsive-split-grid">

      {/* MAP GRID */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 className="font-orbitron glow-text-green" style={{ fontSize: '1.2rem', color: 'var(--primary-green)' }}>
            🗺️ MAPA PARANORMAL DE ZONAS (& Ubicaciones)
          </h2>
          <button className="btn-hud btn-hud-blue" onClick={() => { onRefresh(); fetchLocationDetails(); }}>
            <RefreshCw size={14} /> Actualizar Sensores
          </button>
        </div>

        {/* Medium Selection Bar */}
        <div className="hud-panel" style={{ padding: '12px 16px', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Compass size={18} color="var(--ice-blue)" />
          <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Investigador Activo para Mover:</span>
          <select
            value={selectedMediumId}
            onChange={(e) => setSelectedMediumId(e.target.value)}
            style={{
              background: '#0a0e17',
              border: '1px solid var(--bg-card-border)',
              color: 'var(--text-main)',
              padding: '6px 12px',
              borderRadius: '6px',
              flexGrow: 1
            }}
          >
            <option value="">-- Seleccionar Medium --</option>
            {mediums.map(m => (
              <option key={m.id} value={m.id}>
                {m.nombre} (Maná: {m.mana}/{m.manaMax} | {m.ubicacion?.nombre || locations.find(l => String(l.id) === String(m.ubicacionId))?.nombre || 'En mapa'})
              </option>
            ))}
          </select>
        </div>

        {/* Location Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
          {locations.map(loc => {
            const isSelected = selectedLocation?.id === loc.id;
            const details = locationDetails[loc.id] || { espiritus: [], freeMediums: [] };
            const temp = loc.temperatura ?? 22;
            const isFreezing = temp <= 10;
            const isCementerio = loc.tipo === 'CEMENTERIO';
            // Color theme por tipo
            const typeColor = isCementerio ? 'var(--demonic-red)' : 'var(--primary-green)';
            const typeGlow = isCementerio ? '0 0 16px rgba(255,50,80,0.3)' : '0 0 16px rgba(0,255,128,0.2)';
            const typeIcon = isCementerio ? '💀' : '✨';
            const typeRule = isCementerio
              ? 'Solo DEMONIOS · Demonios ↑ conexión · Ángeles no recuperan'
              : 'Solo ÁNGELES · Ángeles ↑ conexión · Demonios no recuperan';
            // Centroide del polígono (promedio de vértices)
            const verts = loc.vertices ?? [];
            // En la DB los campos están swapeados: v.longitud ≈ -34 (lat real), v.latitud ≈ -58 (lon real)
            const centLat = verts.length ? (verts.reduce((a, v) => a + v.longitud, 0) / verts.length).toFixed(3) : '—';
            const centLon = verts.length ? (verts.reduce((a, v) => a + v.latitud, 0) / verts.length).toFixed(3) : '—';

            return (
              <div
                key={loc.id}
                className="hud-panel"
                onClick={() => setSelectedLocation(loc)}
                style={{
                  padding: '16px',
                  cursor: 'pointer',
                  borderWidth: isSelected ? '2px' : '1px',
                  borderColor: isSelected ? typeColor : 'var(--bg-card-border)',
                  boxShadow: isSelected ? typeGlow : 'none',
                  background: isCementerio
                    ? 'linear-gradient(145deg, #120a0a 0%, #0d0d14 100%)'
                    : 'linear-gradient(145deg, #0a120a 0%, #0a0d14 100%)'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px' }}>
                  <div>
                    <h3 className="font-orbitron" style={{ fontSize: '1.05rem', color: '#fff' }}>{loc.nombre}</h3>
                    <span
                      className="badge-hud"
                      style={{
                        marginTop: '4px',
                        background: isCementerio ? 'rgba(255,50,80,0.15)' : 'rgba(0,255,128,0.12)',
                        border: `1px solid ${typeColor}`,
                        color: typeColor
                      }}
                    >
                      {typeIcon} {loc.tipo || 'SANTUARIO'}
                    </span>
                  </div>
                  <div className={`temp-gauge ${isFreezing ? 'temp-freezing' : 'temp-warm'}`}>
                    <Thermometer size={16} />
                    {temp}°C
                  </div>
                </div>

                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'grid', gap: '6px', margin: '12px 0' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Zap size={14} color="var(--amber-gold)" />
                    <span>Energía: <strong style={{ color: 'var(--amber-gold)' }}>{loc.energia ?? 'N/A'}</strong></span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <MapPin size={14} color="var(--ice-blue)" />
                    <span>Coords: [{centLat}, {centLon}]</span>
                  </div>
                  {/* Regla del tipo de ubicación */}
                  <div style={{
                    fontSize: '0.72rem',
                    color: typeColor,
                    background: isCementerio ? 'rgba(255,50,80,0.07)' : 'rgba(0,255,128,0.06)',
                    border: `1px solid ${typeColor}30`,
                    borderRadius: '4px',
                    padding: '4px 6px',
                    marginTop: '4px'
                  }}>
                    ⚠ {typeRule}
                  </div>
                </div>

                <div style={{ display: 'flex', gap: '8px', fontSize: '0.75rem', marginTop: '10px' }}>
                  <span className="badge-hud badge-green">
                    👻 Espíritus: {details.espiritus.length}
                  </span>
                  <span className="badge-hud badge-blue">
                    🧑‍🚀 Mediums Libres: {details.freeMediums.length}
                  </span>
                </div>

                <button
                  className="btn-hud btn-hud-blue"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleMoveMedium(loc);
                  }}
                  disabled={loadingAction || !selectedMediumId}
                  style={{ width: '100%', marginTop: '14px', justifyContent: 'center' }}
                >
                  <ArrowRight size={14} /> Mover Medium Aquí
                </button>
              </div>
            );
          })}
        </div>
      </div>

      {/* SIDEBAR */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {/* PARTICIÓN DE ZONAS SOBRECARGADAS */}

        <div className="hud-panel" style={{ padding: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
            <h3 className="font-orbitron" style={{ color: 'var(--demonic-red)', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <ShieldAlert size={18} /> Zonas Sobrecargadas
            </h3>
            <button className="btn-hud btn-hud-danger" onClick={fetchOverloaded} style={{ padding: '4px 8px', fontSize: '0.75rem' }}>
              Detectar
            </button>
          </div>

          <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '12px', lineHeight: '1.4' }}>
            Son aquellas ubicaciones cuya energía mística supera el umbral crítico configurado (ej: mayor a 20 de energía).
          </p>

          {overloadedLocs.length === 0 ? (
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Haz clic en Detectar para buscar zonas sobrecargadas.</p>
          ) : (
            <div style={{ display: 'grid', gap: '8px' }}>
              {overloadedLocs.map(loc => (
                <div key={loc.id} style={{ background: 'rgba(255, 42, 95, 0.1)', border: '1px solid var(--demonic-red)', padding: '8px 12px', borderRadius: '6px', fontSize: '0.85rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong>{loc.nombre}</strong>
                  <span style={{ color: 'var(--amber-gold)', fontWeight: 'bold', fontSize: '0.8rem' }}>⚡ {loc.energia ?? loc.flujoEnergia ?? 'Sobrecargada'}</span>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>

    </div>
  );
}
