import apiFetch from './apiFetch';

const MENSAJERIA_URL = import.meta.env.VITE_MENSAJERIA_API_URL || 'http://localhost:8090';

export const spiritBoxService = {
  getComunicacionActiva: async (mediumId, ubicacionId) => {
    try {
      return await apiFetch(`${MENSAJERIA_URL}/comunicacion-activa?mediumId=${mediumId}&ubicacionId=${ubicacionId}`, {}, "Error al verificar comunicación activa");
    } catch (e) {
      if (e.status === 404) return null;
      console.warn("Servicio de mensajería no disponible o error 404:", e.message);
      return null;
    }
  },

  getHistorialChat: async (mediumId) => {
    try {
      return await apiFetch(`${MENSAJERIA_URL}/historial/${mediumId}`, {}, "Error al consultar historial de chat en MongoDB");
    } catch (e) {
      console.warn("Error leyendo historial de MongoDB:", e.message);
      return [];
    }
  },

  agregarPlantilla: async (texto) => {
    return apiFetch(`${MENSAJERIA_URL}/plantillas`, {
      method: 'POST',
      body: JSON.stringify({ texto })
    }, "Error al guardar la plantilla de respuesta");
  }
};
