import { locationService } from './locationService';
import { mediumService } from './mediumService';
import { spiritService } from './spiritService';

// Helper para generar 3 vértices de un triángulo pequeño alrededor de un punto central
// El backend requiere exactamente 3 vértices (Size min=3, max=3)
function triangleVertices(lat, lon, delta = 0.001) {
  return [
    { latitud: lat + delta, longitud: lon },
    { latitud: lat - delta, longitud: lon + delta },
    { latitud: lat - delta, longitud: lon - delta }
  ];
}

export const seedService = {
  seedDatabase: async () => {
    // 1. Crear Ubicaciones
    // Backend espera: { nombre, energia, tipo, vertices: [{latitud, longitud}] }
    const loc1 = await locationService.crearUbicacion({
      nombre: 'Tanglewood Street House',
      tipo: 'SANTUARIO',
      energia: 50,
      vertices: triangleVertices(-34.7042, -58.2755)
    });

    const loc2 = await locationService.crearUbicacion({
      nombre: 'Graveyard of Souls',
      tipo: 'CEMENTERIO',
      energia: 80,
      vertices: triangleVertices(-34.7100, -58.2800)
    });

    const loc3 = await locationService.crearUbicacion({
      nombre: 'Asylum Sanctuary',
      tipo: 'SANTUARIO',
      energia: 30,
      vertices: triangleVertices(-34.7150, -58.2850)
    });

    // Conectar Grafo Neo4j
    if (loc1?.id && loc2?.id) await locationService.conectarUbicaciones(loc1.id, loc2.id, 10);
    if (loc2?.id && loc3?.id) await locationService.conectarUbicaciones(loc2.id, loc3.id, 15);
    if (loc1?.id && loc3?.id) await locationService.conectarUbicaciones(loc1.id, loc3.id, 25);

    // 2. Crear Mediums
    // Backend espera: { nombre, manaMax, mana, ubicacionId }
    const m1 = await mediumService.crearMedium({
      nombre: 'John Constantine',
      manaMax: 100,
      mana: 100,
      ubicacionId: loc1.id
    });

    const m2 = await mediumService.crearMedium({
      nombre: 'Lorraine Warren',
      manaMax: 90,
      mana: 80,
      ubicacionId: loc2.id
    });

    // 3. Crear Espíritus
    // Backend espera: { nombre, tipo, ubicacionId, hostilidad }
    const e1 = await spiritService.crearEspiritu({
      nombre: 'Banshee Demoniaca',
      tipo: 'DEMONIO',
      hostilidad: 85.0,
      ubicacionId: loc2.id
    });

    await spiritService.crearEspiritu({
      nombre: 'Ángel Sanador',
      tipo: 'ANGELICAL',
      hostilidad: 10.0,
      ubicacionId: loc1.id
    });

    await spiritService.crearEspiritu({
      nombre: 'Poltergeist Agresivo',
      tipo: 'DEMONIO',
      hostilidad: 95.0,
      ubicacionId: loc1.id
    });

    if (e1?.id && m2?.id) {
      try {
        await spiritService.conectarEspiritu(e1.id, m2.id);
      } catch (e) {
        console.warn('Conexión automática omitida:', e.message);
      }
    }
  }
};
