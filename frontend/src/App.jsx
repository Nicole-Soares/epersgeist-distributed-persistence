import React, { useState, useEffect } from 'react';
import { locationService } from './services/locationService';
import { mediumService } from './services/mediumService';
import { spiritService } from './services/spiritService';
import { formatErrorMessage } from './services/errorHandler';

import { Header } from './components/Header';
import { MapPage } from './pages/MapPage';
import { InvestigatorsPage } from './pages/InvestigatorsPage';
import { SpiritBoxPage } from './pages/SpiritBoxPage';
import { ExorcismPage } from './pages/ExorcismPage';
import { RulesPage } from './pages/RulesPage';
import { CreateEntityModal } from './components/CreateEntityModal';

import { RefreshCw, Radio } from 'lucide-react';

export function App() {
  const [locations, setLocations] = useState([]);
  const [mediums, setMediums] = useState([]);
  const [spirits, setSpirits] = useState([]);
  const [activeTab, setActiveTab] = useState('map');
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState(null);

  useEffect(() => {
    fetchInitialData();
  }, []);

  const sanitizeLocations = (rawLocations) => {
    if (!rawLocations || !Array.isArray(rawLocations)) return [];
    const uniquePool = [
      'Tanglewood Street House',
      'Graveyard of Souls',
      'Asylum Sanctuary',
      'Catedral Divina',
      'Capilla Sagrada',
      'Abadía de San Gabriel',
      'Monasterio de Monte Carmelo',
      'Basílica de Nuestra Señora',
      'Santuario del Alba',
      'Templo de la Redención',
      'Convento Santa Catalina',
      'Ermita de la Esperanza',
      'Catedral del Espíritu Santo',
      'Oratorio de San Miguel',
      'Santuario del Valle'
    ];
    const used = new Set();
    return rawLocations.map((loc) => {
      let clean = (loc.nombre || 'Zona').replace(/\s*#\d+.*$/, '').trim();
      if (used.has(clean)) {
        const unused = uniquePool.find(n => !used.has(n));
        if (unused) {
          clean = unused;
        } else {
          const Suffixes = ['Norte', 'Sur', 'Este', 'Oeste', 'Central', 'Vieja', 'Nueva'];
          const suf = Suffixes.find(s => !used.has(`${clean} ${s}`)) || `Sector ${used.size}`;
          clean = `${clean} ${suf}`;
        }
      }
      used.add(clean);
      return { ...loc, nombre: clean };
    });
  };

  const sanitizeMediums = (rawMediums) => {
    if (!rawMediums || !Array.isArray(rawMediums)) return [];
    const uniquePool = [
      'John Constantine',
      'Lorraine Warren',
      'Padre Gabriel',
      'Hermana Lucía',
      'Obispo Tomás',
      'Monje Ezequiel',
      'Sacerdotisa Helena',
      'Fray Sebastián',
      'Investigador Edward',
      'Medium Clara'
    ];
    const used = new Set();
    return rawMediums.map((m) => {
      let clean = (m.nombre || 'Medium').replace(/\s*#\d+.*$/, '').trim();
      if (used.has(clean)) {
        const unused = uniquePool.find(n => !used.has(n));
        if (unused) {
          clean = unused;
        } else {
          clean = `${clean} (Auxiliar)`;
        }
      }
      used.add(clean);
      return { ...m, nombre: clean };
    });
  };

  const fetchInitialData = async () => {
    setLoading(true);
    setErrorMsg(null);
    try {
      const [locRes, medRes, spiRes] = await Promise.all([
        locationService.getUbicaciones().catch(() => []),
        mediumService.getMediums().catch(() => []),
        spiritService.getEspiritus().catch(() => [])
      ]);

      const cleanLocs = sanitizeLocations(locRes || []);
      const cleanMeds = sanitizeMediums(medRes || []);

      setLocations(cleanLocs);
      setMediums(cleanMeds);
      setSpirits(spiRes || []);

      if (cleanLocs.length > 0 && !selectedLocation) {
        setSelectedLocation(cleanLocs[0]);
      }
    } catch (err) {
      console.error("Error cargando datos iniciales:", err);
      setErrorMsg(formatErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container" style={{ maxWidth: '1440px', margin: '0 auto', padding: '24px' }}>
      
      {/* HEADER CONTROLS & TABS */}
      <Header
        onRefresh={fetchInitialData}
        onOpenCreateModal={() => setShowCreateModal(true)}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />

      {/* ERROR / BACKEND CONNECTION ALERT */}
      {errorMsg && (
        <div className="hud-panel" style={{ padding: '16px 20px', marginBottom: '24px', borderColor: 'var(--demonic-red)', background: 'rgba(255, 42, 95, 0.1)' }}>
          <div style={{ color: 'var(--demonic-red)', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Radio size={20} /> Error de Conexión con el Monolito Backend (:8080)
          </div>
          <p style={{ fontSize: '0.9rem', marginTop: '6px', color: 'var(--text-muted)' }}>{errorMsg}</p>
          <button className="btn-hud btn-hud-danger" onClick={fetchInitialData} style={{ marginTop: '12px', fontSize: '0.8rem' }}>
            <RefreshCw size={14} /> Reintentar Conexión
          </button>
        </div>
      )}

      {/* LOADING INDICATOR */}
      {loading && (
        <div style={{ textCenter: 'center', padding: '40px 0', color: 'var(--primary-green)', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '10px' }}>
          <RefreshCw size={24} className="glow-text-green" style={{ animation: 'spin 1s linear infinite' }} />
          <span className="font-orbitron" style={{ letterSpacing: '1px' }}>Sintonizando datos psíquicos del servidor...</span>
        </div>
      )}

      {/* PAGE ROUTER CONTENT */}
      {!loading && (
        <main>
          {activeTab === 'map' && (
            <MapPage
              locations={locations}
              mediums={mediums}
              onRefresh={fetchInitialData}
              selectedLocation={selectedLocation}
              setSelectedLocation={setSelectedLocation}
            />
          )}

          {activeTab === 'investigation' && (
            <InvestigatorsPage
              mediums={mediums}
              spirits={spirits}
              locations={locations}
              onRefresh={fetchInitialData}
            />
          )}

          {activeTab === 'ghostbox' && (
            <SpiritBoxPage
              mediums={mediums}
              locations={locations}
              spirits={spirits}
              onRefresh={fetchInitialData}
            />
          )}

          {activeTab === 'exorcism' && (
            <ExorcismPage
              mediums={mediums}
              spirits={spirits}
              onRefresh={fetchInitialData}
            />
          )}

          {activeTab === 'rules' && (
            <RulesPage />
          )}
        </main>
      )}

      {/* MODAL DE CREACIÓN */}
      {showCreateModal && (
        <CreateEntityModal
          onClose={() => setShowCreateModal(false)}
          locations={locations}
          onRefresh={fetchInitialData}
        />
      )}

    </div>
  );
}
