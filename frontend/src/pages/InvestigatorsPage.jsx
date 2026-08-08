import React from 'react';
import { MediumCard } from '../components/MediumCard';
import { SpiritRoster } from '../components/SpiritRoster';

export function InvestigatorsPage({ mediums, spirits, locations, onRefresh }) {
  const freeSpirits = spirits.filter(s => !s.mediumId);

  return (
    <div style={{ display: 'grid', gap: '30px' }}>
      <div>
        <h2 className="font-orbitron glow-text-blue" style={{ fontSize: '1.2rem', color: 'var(--ice-blue)', marginBottom: '16px' }}>
          👥 PLANTILLA DE INVESTIGADORES & MEDIUMS ({mediums.length})
        </h2>
        {mediums.length === 0 ? (
          <div className="hud-panel" style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)' }}>
            No hay Mediums registrados. Utiliza el botón "Cargar Datos Semilla" para inicializar cazadores y espíritus.
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '20px' }}>
            {mediums.map(m => (
              <MediumCard
                key={m.id}
                medium={m}
                locations={locations}
                freeSpirits={freeSpirits}
                onRefresh={onRefresh}
              />
            ))}
          </div>
        )}
      </div>

      <SpiritRoster
        spirits={spirits}
        mediums={mediums}
        locations={locations}
        onRefresh={onRefresh}
      />
    </div>
  );
}
