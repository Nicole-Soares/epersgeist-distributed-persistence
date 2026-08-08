import apiFetch from './apiFetch';

const EPERSGEIST_URL = import.meta.env.VITE_EPERSGEIST_API_URL || 'http://localhost:8080';

export const telemetryService = {
  getSantuarioCorrupto: async () => {
    return apiFetch(`${EPERSGEIST_URL}/estadistica/santuarioCorrupto`, {}, "Error al obtener santuario más corrupto");
  },

  getPromedioSensores: async () => {
    return apiFetch(`${EPERSGEIST_URL}/estadistica/promedioSensor`, {}, "Error al obtener promedios de sensores");
  },

  normalizarDatosSensor: async () => {
    return apiFetch(`${EPERSGEIST_URL}/sensor/normalizarDatos`, {
      method: 'POST'
    }, "Error al ejecutar la normalización de datos de sensores");
  }
};
