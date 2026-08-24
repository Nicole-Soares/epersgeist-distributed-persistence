import React from 'react';
import { Ghost, ShieldAlert, Radio, Cpu, PlusCircle } from 'lucide-react';
import { SeedDataButton } from './SeedDataButton';

export function Header({ onRefresh, onOpenCreateModal, activeTab, setActiveTab }) {
  return (
    <header className="hud-panel" style={{ padding: '16px 24px', marginBottom: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        
        {/* Title Brand */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{
            background: 'rgba(0, 255, 157, 0.15)',
            border: '1px solid var(--primary-green)',
            padding: '10px',
            borderRadius: '12px',
            display: 'flex'
          }}>
            <Ghost size={28} color="var(--primary-green)" className="glow-text-green" />
          </div>
          <div>
            <h1 className="font-orbitron glow-text-green" style={{ fontSize: '1.5rem', letterSpacing: '2px', color: 'var(--primary-green)' }}>
              EPERSGEIST HQ
            </h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', letterSpacing: '1px' }}>
              PHASMOPHOBIA GHOST HUNTING & PARANORMAL DISPATCH
            </p>
          </div>
        </div>

      </div>

      {/* Navigation Tabs */}
      <nav className="nav-tabs-container" style={{ display: 'flex', flexWrap: 'wrap', gap: '10px', marginTop: '20px', borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '16px' }}>
        {[
          { id: 'map', label: '🗺️ Mapa Táctico & Zonas' },
          { id: 'investigation', label: '👥 Investigadores & Espíritus' },
          { id: 'ghostbox', label: '📻 Spirit Box & Chat' },
          { id: 'exorcism', label: '⚔️ Cámara de Exorcismos' },
          { id: 'rules', label: '📖 Reglas & Manual' }
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`btn-hud nav-tab-btn ${activeTab === tab.id ? '' : 'btn-hud-blue'}`}
            style={{
              opacity: activeTab === tab.id ? 1 : 0.7,
              background: activeTab === tab.id ? 'var(--primary-green)' : 'transparent',
              color: activeTab === tab.id ? '#000' : 'var(--ice-blue)',
              borderColor: activeTab === tab.id ? 'var(--primary-green)' : 'rgba(0,229,255,0.3)'
            }}
          >
            {tab.label}
          </button>
        ))}
      </nav>
    </header>
  );
}
