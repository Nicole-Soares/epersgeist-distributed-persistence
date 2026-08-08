import React, { useState } from 'react';
import { BookOpen, MapPin, Ghost, User, ArrowRight, Zap, Shield, Heart, Sword, Navigation, AlertTriangle, Compass, CheckCircle2 } from 'lucide-react';

const SECTION_STYLE = {
  marginBottom: '24px',
};

const RULE_CARD = {
  background: 'rgba(255,255,255,0.03)',
  border: '1px solid rgba(255,255,255,0.08)',
  borderRadius: '10px',
  padding: '16px 20px',
  marginBottom: '12px',
};

const TAG = ({ children, color }) => (
  <span style={{
    display: 'inline-block',
    background: `${color}22`,
    border: `1px solid ${color}`,
    color: color,
    borderRadius: '4px',
    padding: '2px 8px',
    fontSize: '0.72rem',
    fontWeight: 'bold',
    marginRight: '6px',
    marginBottom: '4px',
  }}>
    {children}
  </span>
);

export function RulesPage() {
  const [activeSection, setActiveSection] = useState('network');

  const sections = [
    { id: 'network', label: 'Mapa de Recorrido (Subte)', icon: Navigation },
    { id: 'locations', label: 'Ubicaciones', icon: MapPin },
    { id: 'spirits', label: 'Espíritus', icon: Ghost },
    { id: 'mediums', label: 'Mediums', icon: User },
    { id: 'mechanics', label: 'Mecánicas', icon: Zap },
    { id: 'howto', label: 'Cómo Jugar', icon: Shield },
    { id: 'lore', label: 'Lore', icon: BookOpen },
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '240px 1fr', gap: '24px', alignItems: 'start' }}>

      {/* SIDEBAR NAV */}
      <div className="hud-panel" style={{ padding: '16px', position: 'sticky', top: '16px' }}>
        <h3 className="font-orbitron" style={{ color: 'var(--primary-green)', fontSize: '0.85rem', marginBottom: '14px', letterSpacing: '1px' }}>
          📖 MANUAL Y REGLAS
        </h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          {sections.map(s => {
            const Icon = s.icon;
            const active = activeSection === s.id;
            return (
              <button
                key={s.id}
                onClick={() => setActiveSection(s.id)}
                style={{
                  display: 'flex', alignItems: 'center', gap: '8px',
                  background: active ? 'rgba(0,255,128,0.12)' : 'transparent',
                  border: active ? '1px solid var(--primary-green)' : '1px solid transparent',
                  color: active ? 'var(--primary-green)' : 'var(--text-muted)',
                  padding: '10px 12px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '0.85rem',
                  textAlign: 'left',
                  width: '100%',
                  transition: 'all 0.2s',
                  fontWeight: active ? 'bold' : 'normal'
                }}
              >
                <Icon size={16} /> {s.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* CONTENT */}
      <div>

        {/* RECORRIDO DE UBICACIONES / MAPA DE SUBTE PSIQUICO */}
        {activeSection === 'network' && (
          <div>
            <h2 className="font-orbitron glow-text-green" style={{ color: 'var(--primary-green)', fontSize: '1.3rem', marginBottom: '8px' }}>
              🚇 RED DE RECORRIDO Y CONEXIONES PSIÓNICAS
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '20px' }}>
              Imaginá las ubicaciones como un <strong>mapa de líneas de subte</strong>. Un médium sólo puede viajar entre estaciones (ubicaciones) que están conectadas directamente por una línea de conexión.
            </p>

            {/* DIAGRAMA ESTILO SUBTE */}
            <div className="hud-panel" style={{ padding: '24px', marginBottom: '24px', background: 'linear-gradient(135deg, #070b12 0%, #0d1524 100%)', borderColor: 'var(--ice-blue)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid rgba(0,229,255,0.2)', paddingBottom: '10px' }}>
                <span className="font-orbitron" style={{ color: 'var(--ice-blue)', fontSize: '0.9rem', letterSpacing: '1px' }}>
                  🗺️ ESQUEMA DE LÍNEAS DE TRÁNSITO PARANORMAL
                </span>
                <span style={{ fontSize: '0.75rem', background: 'rgba(0,229,255,0.1)', color: 'var(--ice-blue)', border: '1px solid var(--ice-blue)', padding: '2px 8px', borderRadius: '4px' }}>
                  RED CONECTADA DIRECTA
                </span>
              </div>

              {/* VISUAL METRO LINE */}
              <div style={{ position: 'relative', padding: '20px 10px', display: 'flex', flexDirection: 'column', gap: '30px' }}>
                
                {/* LINEA ASTRALIAS */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }}>
                  <span className="font-orbitron" style={{ background: '#00ff9d', color: '#000', padding: '4px 10px', borderRadius: '4px', fontWeight: 'bold', fontSize: '0.8rem' }}>
                    LÍNEA A (SANTUARIOS)
                  </span>

                  <div style={{ display: 'flex', alignItems: 'center', flexGrow: 1, gap: '0px' }}>
                    
                    {/* Estacion 1 */}
                    <div style={{ textAlign: 'center', minWidth: '130px' }}>
                      <div style={{ width: '20px', height: '20px', borderRadius: '50%', background: 'var(--primary-green)', border: '4px solid #000', margin: '0 auto 6px', boxShadow: '0 0 10px var(--primary-green)' }} />
                      <div style={{ fontSize: '0.82rem', fontWeight: 'bold', color: '#fff' }}>Tanglewood</div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--primary-green)' }}>✨ Santuario</div>
                    </div>

                    {/* Tramo 1 */}
                    <div style={{ flexGrow: 1, height: '6px', background: 'linear-gradient(90deg, #00ff9d, #ff2a5f)', position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <span style={{ fontSize: '0.7rem', background: '#070b12', color: 'var(--amber-gold)', border: '1px solid var(--amber-gold)', padding: '2px 6px', borderRadius: '10px', fontWeight: 'bold' }}>
                        -10 Maná
                      </span>
                    </div>

                    {/* Estacion 2 */}
                    <div style={{ textAlign: 'center', minWidth: '140px' }}>
                      <div style={{ width: '20px', height: '20px', borderRadius: '50%', background: 'var(--demonic-red)', border: '4px solid #000', margin: '0 auto 6px', boxShadow: '0 0 10px var(--demonic-red)' }} />
                      <div style={{ fontSize: '0.82rem', fontWeight: 'bold', color: '#fff' }}>Graveyard Souls</div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--demonic-red)' }}>💀 Cementerio</div>
                    </div>

                    {/* Tramo 2 */}
                    <div style={{ flexGrow: 1, height: '6px', background: 'linear-gradient(90deg, #ff2a5f, #00ff9d)', position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <span style={{ fontSize: '0.7rem', background: '#070b12', color: 'var(--amber-gold)', border: '1px solid var(--amber-gold)', padding: '2px 6px', borderRadius: '10px', fontWeight: 'bold' }}>
                        -15 Maná
                      </span>
                    </div>

                    {/* Estacion 3 */}
                    <div style={{ textAlign: 'center', minWidth: '130px' }}>
                      <div style={{ width: '20px', height: '20px', borderRadius: '50%', background: 'var(--primary-green)', border: '4px solid #000', margin: '0 auto 6px', boxShadow: '0 0 10px var(--primary-green)' }} />
                      <div style={{ fontSize: '0.82rem', fontWeight: 'bold', color: '#fff' }}>Asylum</div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--primary-green)' }}>✨ Santuario</div>
                    </div>

                  </div>
                </div>

              </div>

              {/* EXPLICACION CLARA */}
              <div style={{ marginTop: '20px', background: 'rgba(0, 229, 255, 0.05)', border: '1px solid rgba(0, 229, 255, 0.2)', borderRadius: '8px', padding: '16px', fontSize: '0.88rem', color: 'var(--text-main)', lineHeight: '1.6' }}>
                <h4 style={{ color: 'var(--ice-blue)', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Compass size={16} /> ¿Cómo funciona la movilidad?
                </h4>
                <ul style={{ paddingLeft: '20px', display: 'grid', gap: '6px', color: 'var(--text-muted)' }}>
                  <li><strong>Viaje directo:</strong> Solo podés mover un médium entre ubicaciones adyacentes (que tienen un tramo de conexión directo).</li>
                  <li><strong>Costo de Maná:</strong> Cada tramo consume maná del médium. Si el médium no tiene maná suficiente o se queda en 0, es <strong>eliminado</strong> del mundo terrenal.</li>
                  <li><strong>Viaje en Convoy:</strong> Al mover al médium, <strong>TODOS sus espíritus conectados se desplazan junto a él automáticamente</strong> a la nueva ubicación.</li>
                </ul>
              </div>
            </div>

            {/* CONSECUENCIAS DEL MOVIMIENTO DE ESPIRITUS */}
            <div className="hud-panel" style={{ padding: '20px' }}>
              <h3 style={{ color: 'var(--amber-gold)', fontSize: '1rem', marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Ghost size={18} /> Efecto del Viaje sobre los Espíritus Acompañantes
              </h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                
                <div style={{ background: 'rgba(255,50,80,0.08)', border: '1px solid var(--demonic-red)', borderRadius: '8px', padding: '14px' }}>
                  <h4 style={{ color: 'var(--demonic-red)', marginBottom: '8px' }}>💀 Si el destino es un SANTUARIO:</h4>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
                    Los <strong>Demonios</strong> que viajan con el médium sufren rechazo sagrado y pierden <strong>10 puntos de conexión</strong>.
                    <br />
                    <em style={{ color: '#fff', display: 'block', marginTop: '6px' }}>⚠ Si la conexión de un demonio cae a 0, se desvincula automáticamente y queda libre en el Santuario.</em>
                  </p>
                </div>

                <div style={{ background: 'rgba(0,255,128,0.08)', border: '1px solid var(--primary-green)', borderRadius: '8px', padding: '14px' }}>
                  <h4 style={{ color: 'var(--primary-green)', marginBottom: '8px' }}>✨ Si el destino es un CEMENTERIO:</h4>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: '1.5' }}>
                    Los <strong>Ángeles</strong> que viajan con el médium sufren la marea profana y pierden <strong>5 puntos de conexión</strong>.
                    <br />
                    <em style={{ color: '#fff', display: 'block', marginTop: '6px' }}>⚠ Si la conexión de un ángel cae a 0, se desvincula automáticamente y queda libre en el Cementerio.</em>
                  </p>
                </div>

              </div>
            </div>

          </div>
        )}

        {/* LOCATIONS */}
        {activeSection === 'locations' && (
          <div>
            <h2 className="font-orbitron" style={{ color: 'var(--ice-blue)', fontSize: '1.3rem', marginBottom: '20px' }}>
              🗺️ UBICACIONES: SANTUARIOS Y CEMENTERIOS
            </h2>

            <div style={SECTION_STYLE}>
              <div className="hud-panel" style={{ padding: '20px', background: 'linear-gradient(145deg, #0a120a 0%, #0a0d14 100%)', borderColor: 'var(--primary-green)' }}>
                <h3 style={{ color: 'var(--primary-green)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  ✨ SANTUARIOS
                </h3>
                <div style={{ display: 'grid', gap: '8px', fontSize: '0.9rem' }}>
                  <div style={RULE_CARD}><TAG color="var(--primary-green)">INVOCACIÓN</TAG> Solo se pueden invocar espíritus <strong>ANGELICALES</strong></div>
                  <div style={RULE_CARD}><TAG color="var(--primary-green)">DESCANSO</TAG> El medium recupera <strong>150% de la energía</strong> de la zona en maná (hasta su manaMax)</div>
                  <div style={RULE_CARD}><TAG color="var(--primary-green)">ÁNGELES</TAG> Ganan nivel de conexión equivalente a la energía del santuario (hasta el máximo de 100%)</div>
                  <div style={RULE_CARD}><TAG color="var(--demonic-red)">DEMONIOS</TAG> <strong>No pueden recuperarse</strong> en santuarios</div>
                  <div style={{ background: 'rgba(0,255,128,0.05)', border: '1px solid rgba(0,255,128,0.2)', borderRadius: '8px', padding: '12px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    <strong>Ejemplo del Enunciado:</strong> Lorraine tiene 10 de maná y descansa en un santuario de energía 100 (recupera 150% = +150 maná). 
                    <br />• <em>Cálculo teórico:</em> 10 + 150 = 160 de maná.
                    <br />• <em>En el código:</em> El maná resultante siempre se acota al <strong>manaMax</strong> del médium.
                  </div>
                </div>
              </div>
            </div>

            <div style={SECTION_STYLE}>
              <div className="hud-panel" style={{ padding: '20px', background: 'linear-gradient(145deg, #120a0a 0%, #0d0d14 100%)', borderColor: 'var(--demonic-red)' }}>
                <h3 style={{ color: 'var(--demonic-red)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  💀 CEMENTERIOS
                </h3>
                <div style={{ display: 'grid', gap: '8px', fontSize: '0.9rem' }}>
                  <div style={RULE_CARD}><TAG color="var(--demonic-red)">INVOCACIÓN</TAG> Solo se pueden invocar espíritus <strong>DEMONÍACOS</strong></div>
                  <div style={RULE_CARD}><TAG color="var(--demonic-red)">DESCANSO</TAG> El medium recupera <strong>50% de la energía</strong> de la zona en maná (hasta su manaMax)</div>
                  <div style={RULE_CARD}><TAG color="var(--demonic-red)">DEMONIOS</TAG> Ganan nivel de conexión equivalente a la energía del cementerio (hasta el máximo de 100%)</div>
                  <div style={RULE_CARD}><TAG color="var(--primary-green)">ÁNGELES</TAG> <strong>No pueden recuperarse</strong> en cementerios</div>
                  <div style={{ background: 'rgba(255,50,80,0.05)', border: '1px solid rgba(255,50,80,0.2)', borderRadius: '8px', padding: '12px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    <strong>Ejemplo del Enunciado:</strong> John tiene 10 de maná y descansa en un cementerio de energía 100 (recupera 50% = +50 maná). Termina con 60 de maná.
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* SPIRITS */}
        {activeSection === 'spirits' && (
          <div>
            <h2 className="font-orbitron" style={{ color: 'var(--primary-green)', fontSize: '1.3rem', marginBottom: '20px' }}>
              👻 ESPÍRITUS: ÁNGELES Y DEMONIOS
            </h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '20px' }}>
              <div className="hud-panel" style={{ padding: '20px', borderColor: 'var(--primary-green)', background: 'linear-gradient(145deg, #0a120a, #0a0d14)' }}>
                <h3 style={{ color: 'var(--primary-green)', marginBottom: '12px' }}>✨ ESPÍRITU ANGELICAL</h3>
                <div style={{ fontSize: '0.88rem', color: 'var(--text-muted)', display: 'grid', gap: '8px' }}>
                  <div>🏠 Habita y se invoca en <strong style={{ color: 'var(--primary-green)' }}>Santuarios</strong></div>
                  <div>⬆ Gana conexión al descansar con el médium en santuarios</div>
                  <div>⬇ Pierde <strong>5 puntos</strong> de conexión al viajar a un cementerio</div>
                  <div>⚔ Participa en rituales de exorcismo atacando demonios hostiles</div>
                  <div>⚠ Si su conexión llega a 0, se desvincula del médium y queda libre</div>
                </div>
              </div>
              <div className="hud-panel" style={{ padding: '20px', borderColor: 'var(--demonic-red)', background: 'linear-gradient(145deg, #120a0a, #0d0d14)' }}>
                <h3 style={{ color: 'var(--demonic-red)', marginBottom: '12px' }}>💀 ESPÍRITU DEMONÍACO</h3>
                <div style={{ fontSize: '0.88rem', color: 'var(--text-muted)', display: 'grid', gap: '8px' }}>
                  <div>🏚 Habita y se invoca en <strong style={{ color: 'var(--demonic-red)' }}>Cementerios</strong></div>
                  <div>⬆ Gana conexión al descansar con el médium en cementerios</div>
                  <div>⬇ Pierde <strong>10 puntos</strong> de conexión al viajar a un santuario</div>
                  <div>👑 Puede dominar a otros espíritus libres para formar jerarquías</div>
                  <div>⚠ Si su conexión llega a 0, se desvincula del médium y queda libre</div>
                </div>
              </div>
            </div>
            <div className="hud-panel" style={{ padding: '16px' }}>
              <h4 style={{ color: 'var(--amber-gold)', marginBottom: '8px' }}>📊 LÍMITES Y ATRIBUTOS DE LOS ESPÍRITUS</h4>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                <div><Zap size={12} style={{ display: 'inline' }} /> <strong>Hostilidad:</strong> Agresividad (rango 0 a 100)</div>
                <div><Heart size={12} style={{ display: 'inline' }} /> <strong>Nivel de Conexión:</strong> Vínculo con el médium (<strong>máximo 100%</strong>)</div>
              </div>
            </div>
          </div>
        )}

        {/* MEDIUMS */}
        {activeSection === 'mediums' && (
          <div>
            <h2 className="font-orbitron" style={{ color: 'var(--ice-blue)', fontSize: '1.3rem', marginBottom: '20px' }}>
              🧑‍🔬 MEDIUMS / INVESTIGADORES
            </h2>
            <div style={{ display: 'grid', gap: '12px' }}>
              {[
                { icon: '💧', title: 'Maná (Energía Espiritual)', desc: 'Recurso principal del médium. Se consume al invocar espíritus (10 maná) y al viajar entre ubicaciones. Está limitado por el manaMax.' },
                { icon: '🚶', title: 'Desplazamiento en Convoy', desc: 'Cuando un médium viaja a una ubicación adyacente, todos los espíritus conectados viajan automáticamente junto a él.' },
                { icon: '📢', title: 'Invocar Espíritu (Traer a la zona)', desc: 'Gasta 10 de maná para trasladar un espíritu libre (hasta 50km) a la ubicación actual del médium. ¡ATENCIÓN! El espíritu invocado sigue estando LIBRE hasta que te conectes a él.' },
                { icon: '🔗', title: 'Conectar Espíritu (Vincularse / Hacerse con él)', desc: 'El ritual para adueñarse de un espíritu libre presente en la misma ubicación. El espíritu DEJA de ser libre, se vincula al cuerpo del médium y gana conexión (+20% del maná actual del médium).' },
                { icon: '✝', title: 'Exorcismo', desc: 'Un médium con ángeles puede purificar a un médium poseído en la misma ubicación para librarlo de demonios.' },
                { icon: '🛌', title: 'Descanso', desc: 'Recupera maná según el tipo de ubicación (150% en Santuarios, 50% en Cementerios).' },
              ].map((item, i) => (
                <div key={i} className="hud-panel" style={{ padding: '16px', display: 'flex', gap: '14px', alignItems: 'flex-start' }}>
                  <span style={{ fontSize: '1.5rem' }}>{item.icon}</span>
                  <div>
                    <strong style={{ color: '#fff', display: 'block', marginBottom: '4px' }}>{item.title}</strong>
                    <span style={{ fontSize: '0.87rem', color: 'var(--text-muted)' }}>{item.desc}</span>
                  </div>
                </div>
              ))}

            </div>
          </div>
        )}

        {/* MECHANICS */}
        {activeSection === 'mechanics' && (
          <div>
            <h2 className="font-orbitron" style={{ color: 'var(--amber-gold)', fontSize: '1.3rem', marginBottom: '20px' }}>
              ⚙️ MECÁNICAS DE JUEGO
            </h2>
            <div style={{ display: 'grid', gap: '16px' }}>
              <div className="hud-panel" style={{ padding: '20px' }}>
                <h3 style={{ color: 'var(--ice-blue)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <ArrowRight size={16} /> Movimiento y Desvinculación
                </h3>
                <div style={{ display: 'grid', gap: '8px', fontSize: '0.88rem', color: 'var(--text-muted)' }}>
                  <div style={RULE_CARD}>Un médium viaja de estación a estación. Todos los espíritus vinculados viajan en convoy con él.</div>
                  <div style={RULE_CARD}>Al <strong>llegar a un Santuario</strong>: los demonios pierden <strong>10 de conexión</strong>.</div>
                  <div style={RULE_CARD}>Al <strong>llegar a un Cementerio</strong>: los ángeles pierden <strong>5 de conexión</strong>.</div>
                  <div style={RULE_CARD}>Si un espíritu llega a 0 de conexión, se <strong>desvincula automáticamente</strong> y queda libre en la ubicación destino.</div>
                </div>
              </div>
              <div className="hud-panel" style={{ padding: '20px' }}>
                <h3 style={{ color: 'var(--amber-gold)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Zap size={16} /> Energía y Descanso
                </h3>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', fontSize: '0.88rem' }}>
                  <div style={{ ...RULE_CARD, borderColor: 'rgba(0,255,128,0.3)' }}>
                    <strong style={{ color: 'var(--primary-green)' }}>Santuario</strong><br/>
                    Médium recupera <strong>150% × energía</strong> en maná<br/>
                    Ángeles ganan <strong>energía</strong> en conexión
                  </div>
                  <div style={{ ...RULE_CARD, borderColor: 'rgba(255,50,80,0.3)' }}>
                    <strong style={{ color: 'var(--demonic-red)' }}>Cementerio</strong><br/>
                    Médium recupera <strong>50% × energía</strong> en maná<br/>
                    Demonios ganan <strong>energía</strong> en conexión
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* HOW TO PLAY */}
        {activeSection === 'howto' && (
          <div>
            <h2 className="font-orbitron glow-text-green" style={{ color: 'var(--primary-green)', fontSize: '1.3rem', marginBottom: '20px' }}>
              🎮 CÓMO JUGAR — GUÍA PASO A PASO
            </h2>
            <div style={{ display: 'grid', gap: '14px' }}>
              {[
                {
                  step: '1', title: 'Explorar el Mapa de Zonas', color: 'var(--primary-green)',
                  items: [
                    'Ingresá a "Mapa Táctico & Zonas".',
                    'Revisá la red de ubicaciones (Santuarios en verde ✨, Cementerios en rojo 💀).',
                    'Cada ubicación indica sus coordenadas, temperatura y flujo de energía.'
                  ]
                },
                {
                  step: '2', title: 'Seleccionar y Mover un Médium', color: 'var(--ice-blue)',
                  items: [
                    'Seleccioná un investigador de la lista desplegable superior.',
                    'Hacé click en "→ Mover Medium Aquí" en una ubicación a la cual se pueda llegar directamente desde su ubicación actual.',
                    '¡IMPORTANTE! Al mover al médium, todos sus espíritus conectados viajan automáticamente junto a él a la nueva ubicación.',
                    'Tené en cuenta que los demonios pierden 10 de conexión en Santuarios y los ángeles pierden 5 en Cementerios.'
                  ]
                },
                {
                  step: '3', title: 'Invocar vs. Conectar un Espíritu', color: 'var(--amber-gold)',
                  items: [
                    'Ingresá a "Investigadores & Espíritus".',
                    '1º INVOCAR: Trae un espíritu libre (hasta 50km) a la ubicación actual del médium. Consume 10 de maná. El espíritu sigue estando LIBRE.',
                    '2º CONECTAR: Seleccioná el médium y hacé click en "Conectar" sobre el espíritu libre de su ubicación. ¡Ahí recién el espíritu deja de ser libre y se vincula a tu médium!'
                  ]
                },

                {
                  step: '4', title: 'Comunicarte por Spirit Box', color: 'var(--primary-green)',
                  items: [
                    'Ingresá a "Spirit Box & Chat".',
                    'Seleccioná un médium con enlace psíquico activo.',
                    'Transmití mensajes y enviá conjeturas de identificación para resolver casos paranormales.'
                  ]
                },
                {
                  step: '5', title: 'Realizar Exorcismos', color: 'var(--demonic-red)',
                  items: [
                    'Ingresá a "Cámara de Exorcismos".',
                    'Elegí el médium exorcista (que debe tener ángeles) y el médium poseído a purificar.',
                    'Ambos médiums deben estar presentes en la misma ubicación.'
                  ]
                },
              ].map(section => (
                <div key={section.step} className="hud-panel" style={{ padding: '16px 20px', borderLeft: `3px solid ${section.color}` }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '10px' }}>
                    <span style={{ background: section.color, color: '#000', borderRadius: '50%', width: '26px', height: '26px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: '0.85rem', flexShrink: 0 }}>
                      {section.step}
                    </span>
                    <strong style={{ color: '#fff' }}>{section.title}</strong>
                  </div>
                  <ul style={{ paddingLeft: '20px', color: 'var(--text-muted)', fontSize: '0.87rem', display: 'grid', gap: '6px', lineHeight: '1.5' }}>
                    {section.items.map((item, i) => <li key={i}>{item}</li>)}
                  </ul>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* LORE */}
        {activeSection === 'lore' && (
          <div>
            <h2 className="font-orbitron glow-text-green" style={{ color: 'var(--primary-green)', fontSize: '1.3rem', marginBottom: '20px' }}>
              🌌 EL LORE — EPERSGEIST
            </h2>
            <div className="hud-panel" style={{ padding: '24px', lineHeight: '1.8', color: 'var(--text-muted)' }}>
              <p style={{ marginBottom: '16px' }}>
                Luego de experimentos de contacto espiritual, un <strong style={{ color: '#fff' }}>desequilibrio</strong> fue desatado tanto en el plano espiritual como en el mundo terrenal. La actividad paranormal comenzó a manifestarse en todo el mundo siguiendo un mismo patrón.
              </p>
              <div style={{ borderLeft: '3px solid var(--demonic-red)', paddingLeft: '16px', marginBottom: '16px' }}>
                <p><strong style={{ color: 'var(--demonic-red)' }}>Cementerios</strong> — Lugares donde los demonios habitan y se vuelven más fuertes. Los demonios están atados a estos lugares y solo pueden salir si un médium los conecta.</p>
              </div>
              <div style={{ borderLeft: '3px solid var(--primary-green)', paddingLeft: '16px', marginBottom: '16px' }}>
                <p><strong style={{ color: 'var(--primary-green)' }}>Santuarios</strong> — Hogar de los ángeles donde recuperan sus energías. Los ángeles no pueden llegar a los cementerios sin un médium que los conecte al mundo terrenal.</p>
              </div>
              <p style={{ fontStyle: 'italic', color: 'var(--ice-blue)' }}>
                "Los demonios abandonaban los cementerios en grandes cantidades, junto a médiums que los conectaban a su cuerpo..."
              </p>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
