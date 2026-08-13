import React, { useState } from 'react';
import { locationService } from '../services/locationService';
import { mediumService } from '../services/mediumService';
import { spiritService } from '../services/spiritService';
import { formatErrorMessage } from '../services/errorHandler';
import { useModal } from './ModalProvider';
import { X, MapPin, User, Ghost, Link2 } from 'lucide-react';

export function CreateEntityModal({ onClose, locations, onRefresh }) {
  const { showAlert } = useModal();
  const [activeTab, setActiveTab] = useState('location');
  const [loading, setLoading] = useState(false);

  const [locForm, setLocForm] = useState({ nombre: '', tipo: 'SANTUARIO', energia: 50, latitud: -34.7, longitud: -58.2 });
  const [medForm, setMedForm] = useState({ nombre: '', manaMax: 100, mana: 100, ubicacionId: '' });
  const [spiForm, setSpiForm] = useState({ nombre: '', tipo: 'DEMONIO', hostilidad: 70, ubicacionId: '' });
  const [connForm, setConnForm] = useState({ origenId: '', destinoId: '', costo: 10 });

  const handleCreateLocation = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const lat = Number(locForm.latitud);
      const lon = Number(locForm.longitud);
      const d = 0.001;
      const newLoc = await locationService.crearUbicacion({
        nombre: locForm.nombre,
        tipo: locForm.tipo,
        energia: Number(locForm.energia),
        vertices: [
          { latitud: lat + d, longitud: lon },
          { latitud: lat - d, longitud: lon + d },
          { latitud: lat - d, longitud: lon - d }
        ]
      });

      // Auto-conectar bidireccionalmente con todas las ubicaciones existentes
      if (newLoc?.id && locations && locations.length > 0) {
        for (const loc of locations) {
          try {
            await locationService.conectarUbicaciones(newLoc.id, loc.id, 10);
            await locationService.conectarUbicaciones(loc.id, newLoc.id, 10);
          } catch (e) {
            // Ignorar silenciando advertencias secundarias
          }
        }
      }

      showAlert('¡Ubicación creada con éxito!', 'CREACIÓN EXITOSA', 'success');
      onRefresh();
      onClose();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE CREACIÓN', 'error');

    } finally {
      setLoading(false);
    }
  };

  const handleCreateMedium = async (e) => {
    e.preventDefault();
    if (!medForm.ubicacionId) return showAlert('Selecciona una ubicación inicial', 'CAMPO REQUERIDO', 'warning');
    setLoading(true);
    try {
      await mediumService.crearMedium({
        nombre: medForm.nombre,
        manaMax: Number(medForm.manaMax),
        mana: Number(medForm.mana),
        ubicacionId: Number(medForm.ubicacionId)
      });
      showAlert('¡Medium creado con éxito!', 'CREACIÓN EXITOSA', 'success');
      onRefresh();
      onClose();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE CREACIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSpirit = async (e) => {
    e.preventDefault();
    if (!spiForm.ubicacionId) return showAlert('Selecciona una ubicación inicial', 'CAMPO REQUERIDO', 'warning');
    setLoading(true);
    try {
      await spiritService.crearEspiritu({
        nombre: spiForm.nombre,
        tipo: spiForm.tipo,
        hostilidad: Number(spiForm.hostilidad),
        ubicacionId: Number(spiForm.ubicacionId)
      });
      showAlert('¡Espíritu creado con éxito!', 'CREACIÓN EXITOSA', 'success');
      onRefresh();
      onClose();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE CREACIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleConnectLocations = async (e) => {
    e.preventDefault();
    if (!connForm.origenId || !connForm.destinoId) return showAlert('Selecciona Origen y Destino', 'CAMPO REQUERIDO', 'warning');
    setLoading(true);
    try {
      await locationService.conectarUbicaciones(connForm.origenId, connForm.destinoId, connForm.costo);
      showAlert('¡Conexión establecida con éxito!', 'CONEXIÓN CREADA', 'success');
      onRefresh();
      onClose();
    } catch (err) {
      showAlert(formatErrorMessage(err), 'ERROR DE CONEXIÓN', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="hud-panel" style={{ width: '520px', padding: '24px', position: 'relative' }}>
        
        <button 
          onClick={onClose}
          style={{ position: 'absolute', top: '16px', right: '16px', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
        >
          <X size={20} />
        </button>

        <h2 className="font-orbitron glow-text-green" style={{ color: 'var(--primary-green)', marginBottom: '16px', fontSize: '1.2rem' }}>
          ➕ CENTRO DE CREACIÓN DE ENTIDADES
        </h2>

        <div style={{ display: 'flex', gap: '8px', marginBottom: '20px', borderBottom: '1px solid rgba(255,255,255,0.1)', pb: '10px' }}>
          {[
            { id: 'location', label: 'Ubicación', icon: MapPin },
            { id: 'medium', label: 'Medium', icon: User },
            { id: 'spirit', label: 'Espíritu', icon: Ghost },
            { id: 'connect', label: 'Conexión Grafo', icon: Link }
          ].map(t => {
            const IconComponent = t.icon;
            return (
              <button
                key={t.id}
                onClick={() => setActiveTab(t.id)}
                className={`btn-hud ${activeTab === t.id ? '' : 'btn-hud-blue'}`}
                style={{ fontSize: '0.75rem', padding: '6px 10px' }}
              >
                <IconComponent size={14} /> {t.label}
              </button>
            );
          })}
        </div>

        {activeTab === 'location' && (
          <form onSubmit={handleCreateLocation} style={{ display: 'grid', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Nombre de la Zona:</label>
              <input type="text" required value={locForm.nombre} onChange={e => setLocForm({...locForm, nombre: e.target.value})} style={inputStyle} placeholder="ej: Sunny Meadows Hospital" />
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Tipo de Ubicación:</label>
              <select value={locForm.tipo} onChange={e => setLocForm({...locForm, tipo: e.target.value})} style={inputStyle}>
                <option value="SANTUARIO">SANTUARIO</option>
                <option value="CEMENTERIO">CEMENTERIO</option>
              </select>
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Energía (1-100):</label>
              <input type="number" required value={locForm.energia} onChange={e => setLocForm({...locForm, energia: e.target.value})} style={inputStyle} />
            </div>
            <div style={{ display: 'flex', gap: '10px' }}>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Latitud:</label>
                <input type="number" step="0.0001" required value={locForm.latitud} onChange={e => setLocForm({...locForm, latitud: e.target.value})} style={inputStyle} />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Longitud:</label>
                <input type="number" step="0.0001" required value={locForm.longitud} onChange={e => setLocForm({...locForm, longitud: e.target.value})} style={inputStyle} />
              </div>
            </div>
            <button type="submit" className="btn-hud" disabled={loading} style={{ marginTop: '10px', justifyContent: 'center' }}>Crear Ubicación</button>
          </form>
        )}

        {activeTab === 'medium' && (
          <form onSubmit={handleCreateMedium} style={{ display: 'grid', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Nombre del Investigador / Medium:</label>
              <input type="text" required value={medForm.nombre} onChange={e => setMedForm({...medForm, nombre: e.target.value})} style={inputStyle} placeholder="ej: Lorraine Warren" />
            </div>
            <div style={{ display: 'flex', gap: '10px' }}>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Maná Actual:</label>
                <input type="number" required value={medForm.mana} onChange={e => setMedForm({...medForm, mana: e.target.value})} style={inputStyle} />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Maná Máximo:</label>
                <input type="number" required value={medForm.manaMax} onChange={e => setMedForm({...medForm, manaMax: e.target.value})} style={inputStyle} />
              </div>
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Ubicación Inicial:</label>
              <select required value={medForm.ubicacionId} onChange={e => setMedForm({...medForm, ubicacionId: e.target.value})} style={inputStyle}>
                <option value="">-- Seleccionar Ubicación --</option>
                {locations.map(l => <option key={l.id} value={l.id}>{l.nombre}</option>)}
              </select>
            </div>
            <button type="submit" className="btn-hud" disabled={loading} style={{ marginTop: '10px', justifyContent: 'center' }}>Crear Medium</button>
          </form>
        )}

        {activeTab === 'spirit' && (
          <form onSubmit={handleCreateSpirit} style={{ display: 'grid', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Nombre del Espíritu:</label>
              <input type="text" required value={spiForm.nombre} onChange={e => setSpiForm({...spiForm, nombre: e.target.value})} style={inputStyle} placeholder="ej: Banshee Nocturna" />
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Tipo de Espíritu:</label>
              <select value={spiForm.tipo} onChange={e => setSpiForm({...spiForm, tipo: e.target.value})} style={inputStyle}>
                <option value="DEMONIO">DEMONIO</option>
                <option value="ANGELICAL">ÁNGEL</option>
              </select>
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Hostilidad (0-100):</label>
              <input type="number" required min="0" max="100" value={spiForm.hostilidad} onChange={e => setSpiForm({...spiForm, hostilidad: e.target.value})} style={inputStyle} />
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Ubicación Inicial:</label>
              <select required value={spiForm.ubicacionId} onChange={e => setSpiForm({...spiForm, ubicacionId: e.target.value})} style={inputStyle}>
                <option value="">-- Seleccionar Ubicación --</option>
                {locations.map(l => <option key={l.id} value={l.id}>{l.nombre}</option>)}
              </select>
            </div>
            <button type="submit" className="btn-hud btn-hud-purple" disabled={loading} style={{ marginTop: '10px', justifyContent: 'center' }}>Crear Espíritu</button>
          </form>
        )}

        {activeTab === 'connect' && (
          <form onSubmit={handleConnectLocations} style={{ display: 'grid', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Ubicación Origen:</label>
              <select required value={connForm.origenId} onChange={e => setConnForm({...connForm, origenId: e.target.value})} style={inputStyle}>
                <option value="">-- Seleccionar Origen --</option>
                {locations.map(l => <option key={l.id} value={l.id}>{l.nombre}</option>)}
              </select>
            </div>
            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Ubicación Destino:</label>
              <select required value={connForm.destinoId} onChange={e => setConnForm({...connForm, destinoId: e.target.value})} style={inputStyle}>
                <option value="">-- Seleccionar Destino --</option>
                {locations.filter(l => l.id !== Number(connForm.origenId)).map(l => <option key={l.id} value={l.id}>{l.nombre}</option>)}
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Costo de Conexión Energética:</label>
              <input type="number" required value={connForm.costo} onChange={e => setConnForm({...connForm, costo: e.target.value})} style={inputStyle} />
            </div>
            <button type="submit" className="btn-hud btn-hud-blue" disabled={loading} style={{ marginTop: '10px', justifyContent: 'center' }}>Conectar Ubicaciones</button>
          </form>
        )}

      </div>
    </div>
  );
}

const inputStyle = {
  width: '100%',
  background: '#0a0e17',
  border: '1px solid var(--bg-card-border)',
  color: '#fff',
  padding: '8px 12px',
  borderRadius: '6px',
  fontSize: '0.85rem'
};
