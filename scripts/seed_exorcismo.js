// Script para generar un Escenario de Exorcismo Poderoso directamente contra la API backend (http://localhost:8080)
// Ejecutar con: node scripts/seed_exorcismo.js

const API_BASE = 'http://localhost:8080';

async function post(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  return res.json();
}

async function put(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  return res.json();
}

async function patch(path) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' }
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  return res.json();
}

async function main() {
  console.log('⚡ Generando escenario de Exorcismo Poderoso en http://localhost:8080...');

  // Coordenadas aleatorias únicas para evitar colisión de polígonos
  const baseLat = -34.7000 - (Math.random() * 0.05);
  const baseLon = -58.2000 - (Math.random() * 0.05);
  const delta = 0.002;

  // 1. Crear Ubicación Santuario
  const loc = await post('/ubicacion', {
    nombre: `Catedral Divina #${Math.floor(Math.random() * 900 + 100)}`,
    tipo: 'SANTUARIO',
    energia: 100,
    vertices: [
      { latitud: baseLat + delta, longitud: baseLon },
      { latitud: baseLat - delta, longitud: baseLon + delta },
      { latitud: baseLat - delta, longitud: baseLon - delta }
    ]
  });
  console.log(`✅ Santuario creado: "${loc.nombre}" (ID: ${loc.id})`);

  // 2. Crear Médiums
  const exorcista = await post('/medium', {
    nombre: 'Padre Gabriel (Exorcista)',
    manaMax: 200,
    mana: 200,
    ubicacionId: loc.id
  });
  console.log(`✅ Exorcista creado: "${exorcista.nombre}" (ID: ${exorcista.id})`);

  const victima = await post('/medium', {
    nombre: 'Víctima Poseída por Azazel',
    manaMax: 100,
    mana: 100,
    ubicacionId: loc.id
  });
  console.log(`✅ Víctima creada: "${victima.nombre}" (ID: ${victima.id})`);

  // 3. Crear Espíritus
  const angel = await post('/espiritu', {
    nombre: 'Arcángel Miguel (Luz Divina)',
    tipo: 'ANGELICAL',
    hostilidad: 5.0,
    ubicacionId: loc.id
  });
  console.log(`✅ Ángel creado: "${angel.nombre}" (ID: ${angel.id})`);

  const demonio = await post('/espiritu', {
    nombre: 'Azazel el Devorador',
    tipo: 'DEMONIO',
    hostilidad: 90.0,
    ubicacionId: loc.id
  });
  console.log(`✅ Demonio creado: "${demonio.nombre}" (ID: ${demonio.id})`);

  // 4. Conectar Ángel al Exorcista
  await patch(`/espiritu/${angel.id}/conectar/${exorcista.id}`);
  console.log(`🔗 "${angel.nombre}" conectado con el Exorcista "${exorcista.nombre}".`);

  // 5. Conectar Demonio a la Víctima
  await patch(`/espiritu/${demonio.id}/conectar/${victima.id}`);
  console.log(`🔗 "${demonio.nombre}" conectado con la Víctima "${victima.nombre}".`);

  // 6. Elevar la conexión del Ángel a 95%
  await put(`/espiritu/${angel.id}`, { nombre: angel.nombre, nivelDeConexion: 95 });
  console.log(`⚡ Conexión de "${angel.nombre}" elevada a 95% para un ataque devastador.`);

  console.log('\n🎉 Escenario listo. Podés ir a la solapa EXORCISMO en el Frontend para usar al Padre Gabriel.');
}

main().catch(err => {
  console.error('❌ Error al ejecutar el script:', err.message);
  process.exit(1);
});
