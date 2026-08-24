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
  },

  seedExorcismoPoderoso: async () => {
    // 1. Obtener ubicaciones existentes para no repetir nombres
    let existingLocs = [];
    try {
      existingLocs = await locationService.getUbicaciones();
    } catch (e) {}
    const existingNames = new Set(existingLocs.map(l => l.nombre));

    // Pools de nombres temáticos únicos
    const sanctuaryNamesPool = [
      'Capilla San Ignacio',
      'Catedral de San Patricio',
      'Abadía de San Gabriel',
      'Monasterio de Monte Carmelo',
      'Basílica de Nuestra Señora',
      'Santuario del Alba Celestial',
      'Templo de la Redención',
      'Convento Santa Catalina',
      'Ermita de la Esperanza',
      'Catedral del Espíritu Santo',
      'Oratorio de San Miguel',
      'Santuario de las Alturas',
      'Monasterio del Valle Sagrado',
      'Parroquia de la Resurrección'
    ];

    const exorcistNames = ['Padre Gabriel', 'Hermana Lucía', 'Obispo Tomás', 'Monje Ezequiel', 'Sacerdotisa Helena', 'Fray Sebastián'];
    const victimNames   = ['María la Poseída', 'Víctima del Abismo', 'Alma en Pena', 'El Condenado', 'Huésped Oscuro', 'Cuerpo sin Voluntad'];
    const angelNames    = ['Arcángel Miguel', 'Serafín Celestial', 'Querubín de Luz', 'Arcángel Rafael', 'Ángel Guardián Divino', 'Trono de Gloria'];
    const demonNames    = ['Azazel el Débil', 'Sombra Menor', 'Espectro Cobarde', 'Demonio Dormido', 'Larva Infernal', 'Eco del Inframundo'];

    const pickUnused = (pool, fallbackPrefix) => {
      const unused = pool.filter(n => !existingNames.has(n));
      if (unused.length > 0) {
        return unused[Math.floor(Math.random() * unused.length)];
      }
      return `${fallbackPrefix} ${existingLocs.length + 1}`;
    };

    const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
    const chosenLocName = pickUnused(sanctuaryNamesPool, 'Santuario Sagrado Sector');

    // 1. Crear Ubicación Santuario de Alta Energía
    const loc = await locationService.crearUbicacion({
      nombre: chosenLocName,
      tipo: 'SANTUARIO',
      energia: 100,
      vertices: triangleVertices(-34.7200 + (Math.random() - 0.5) * 0.005, -58.2900 + (Math.random() - 0.5) * 0.005)
    });

    // 2. Crear Médiums en la misma ubicación
    const exorcista = await mediumService.crearMedium({
      nombre: pick(exorcistNames),
      manaMax: 200,
      mana: 200,
      ubicacionId: loc.id
    });

    const victima = await mediumService.crearMedium({
      nombre: pick(victimNames),
      manaMax: 100,
      mana: 100,
      ubicacionId: loc.id
    });

    // 3. Crear Ángel Poderoso y Demonio Débil
    const angel = await spiritService.crearEspiritu({
      nombre: pick(angelNames),
      tipo: 'ANGELICAL',
      hostilidad: 5.0,
      ubicacionId: loc.id
    });

    const demonio = await spiritService.crearEspiritu({
      nombre: pick(demonNames),
      tipo: 'DEMONIO',
      hostilidad: 15.0,
      ubicacionId: loc.id
    });

    // 4. Conectar el Ángel al Exorcista y el Demonio a la Víctima
    if (angel?.id && exorcista?.id) {
      await spiritService.conectarEspiritu(angel.id, exorcista.id);
      try {
        await spiritService.actualizarEspiritu(angel.id, { nombre: angel.nombre, nivelDeConexion: 95 });
      } catch (e) {}
    }
    if (demonio?.id && victima?.id) {
      await spiritService.conectarEspiritu(demonio.id, victima.id);
    }

    return { exorcista, victima, angel, demonio };
  }
};
