const fs = require('fs');
// --------------------------------------------------
// Función para levantar nombres de ubicaciones desde
// una aplicación en http://localhost:8080/ubicacion
// --------------------------------------------------
const http = require('http');

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomProximity() {
    const units = ['millas', 'metros', 'kilometros', 'pies'];
    const unit = units[getRandomInt(0, units.length - 1)];
    let value;
    switch (unit) {
        case 'millas': value = Math.random() * 10; break;
        case 'metros': value = Math.random() * 16093; break;
        case 'kilometros': value = Math.random() * 16; break;
        case 'pies': value = Math.random() * 52800; break;
    }
    return { value, unit };
}

function randomPressure() {
    const units = ['hPa', 'atm', 'mmHg'];
    const unit = units[getRandomInt(0, units.length - 1)];
    let value;
    switch (unit) {
        case 'hPa': value = getRandomInt(950, 1050); break;
        case 'atm': value = +(Math.random() * 0.1 + 0.95).toFixed(2); break;
        case 'mmHg': value = getRandomInt(713, 788); break;
    }
    return { value, unit };
}

function randomSound() {
    const units = ['dB', 'sones'];
    const unit = units[getRandomInt(0, units.length - 1)];
    let value;
    switch (unit) {
        case 'dB': value = getRandomInt(30, 120); break;
        case 'sones': value = +(Math.random() * 32).toFixed(2); break;
    }
    return { value, unit };
}

function randomTemperature() {
    const units = ['C', 'F', 'K'];
    const unit = units[getRandomInt(0, units.length - 1)];
    let value;
    switch (unit) {
        case 'C': value = getRandomInt(-20, 50); break;
        case 'F': value = getRandomInt(-4, 122); break;
        case 'K': value = getRandomInt(253, 323); break;
    }
    return { value, unit };
}

function generateSensorInput(sensorType) {
    switch (sensorType) {
        case 'proximidad': return randomProximity();
        case 'presion': return randomPressure();
        case 'sonido': return randomSound();
        case 'temperatura': return randomTemperature();
    }
}

const sensorTypes = ['proximidad', 'presion', 'sonido', 'temperatura'];

/**
 * Genera una lista de objetos de entrada de sensores.
 * Si includeUbicaciones es true, intenta obtener nombres desde /ubicaciones
 * y añadir un campo `ubicacion` con un nombre (si está disponible).
 *
 * @param {number} count
 * @param {boolean} includeUbicaciones
 * @returns {Promise<Array>} lista de documentos generados
 */
async function generateSensorInputs(count = 100, includeUbicaciones = false) {
    const docs = [];
    let ubicaciones = [];

    if (includeUbicaciones) {
        try {
            ubicaciones = await fetchUbicaciones();
            if (!Array.isArray(ubicaciones)) ubicaciones = [];
        } catch (e) {
            // No interrumpimos la generación si falla la obtención de ubicaciones
            console.error('Advertencia: no se pudieron obtener ubicaciones:', e.message);
            ubicaciones = [];
        }
    }

    for (let i = 0; i < count; i++) {
        const sensorType = sensorTypes[getRandomInt(0, sensorTypes.length - 1)];
        const input = generateSensorInput(sensorType);
        const doc = {
            sensor_id: `sensor_${getRandomInt(1, 10)}`,
            tipo: sensorType,
            valor: input.value,
            unidad: input.unit,
            fecha: new Date(Date.now() - getRandomInt(0, 100000000)).toISOString(),
        };

        if (ubicaciones.length > 0) {
            // Asignar aleatoriamente una ubicacion a cada documento
            doc.ubicacion = ubicaciones[getRandomInt(0, ubicaciones.length - 1)];
        }

        docs.push(doc);
    }

    return docs;
}

// Si se requiere, generar el archivo por defecto al cargar el script
// (mantener comportamiento cercano al anterior para compatibilidad)
generateSensorInputs(200, true).then(docs => {
    try {
        fs.writeFileSync('sensor_inputs.json', JSON.stringify(docs, null, 2), 'utf8');
        console.log('Archivo sensor_inputs.json generado correctamente.');
    } catch (e) {
        console.error('Error escribiendo sensor_inputs.json:', e.message);
    }
}).catch(err => {
    console.error('Error generando sensor inputs:', err.message);
});

/**
 * Fetch de ubicaciones desde un endpoint local.
 * Intenta manejar varias formas de respuesta JSON y extraer
 * los nombres de las ubicaciones.
 *
 * @param {string} host
 * @param {number} port
 * @param {string} path
 * @param {number} timeout
 * @returns {Promise<string[]>} lista de nombres
 */
function fetchUbicaciones(host = 'localhost', port = 8080, path = '/ubicacion', timeout = 5000) {
    const options = { hostname: host, port, path, method: 'GET', timeout };
    return new Promise((resolve, reject) => {
        const req = http.request(options, (res) => {
            let data = '';
            res.setEncoding('utf8');
            res.on('data', (chunk) => data += chunk);
            res.on('end', () => {
                try {
                    const parsed = JSON.parse(data);
                    let names = [];

                    function extractFromArray(arr) {
                        return arr.map(item => {
                            if (typeof item === 'string') return item;
                            if (item && typeof item === 'object') {
                                return item.nombre || item.name || item.ubicacion || item.location || null;
                            }
                            return null;
                        }).filter(Boolean);
                    }

                    if (Array.isArray(parsed)) {
                        names = extractFromArray(parsed);
                    } else if (parsed && typeof parsed === 'object') {
                        // casos comunes: { ubicaciones: [...] } o { data: [...] }
                        const arr = parsed.ubicaciones || parsed.locations || parsed.data || parsed.items || null;
                        if (Array.isArray(arr)) {
                            names = extractFromArray(arr);
                        } else {
                            // Si el objeto en sí contiene campos con nombres
                            const maybeName = parsed.nombre || parsed.name || parsed.ubicacion || parsed.location;
                            if (maybeName) names = [maybeName];
                        }
                    }

                    resolve(names);
                } catch (e) {
                    reject(new Error('Error parsing JSON from /ubicaciones: ' + e.message));
                }
            });
        });

        req.on('error', (err) => reject(err));
        req.on('timeout', () => {
            req.destroy(new Error('Request timed out'));
        });
        req.end();
    });
}

// Exportar la función para poder usarla desde otros módulos o tests
module.exports = {
    // mantengo las funciones principales si se quisieran importar
    getRandomInt,
    generateSensorInput,
    fetchUbicaciones
};

// Permitir probar la obtención de ubicaciones desde CLI con --fetch-ubicaciones
if (require.main === module) {
    const args = process.argv.slice(2);
    if (args.includes('--fetch-ubicaciones')) {
        fetchUbicaciones()
            .then(names => console.log('Ubicaciones obtenidas:', names))
            .catch(err => console.error('Error al obtener ubicaciones:', err.message));
    }
}