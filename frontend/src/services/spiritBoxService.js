import apiFetch from './apiFetch';

const MENSAJERIA_URL = import.meta.env.VITE_MENSAJERIA_API_URL || 'http://localhost:8090';

export const spiritBoxService = {
  getComunicacionActiva: async (mediumId, ubicacionId) => {
    try {
      const res = await fetch(`${MENSAJERIA_URL}/comunicacion-activa?mediumId=${mediumId}&ubicacionId=${ubicacionId}`);
      if (res.status === 404) return null;
      if (!res.ok) return null;
      return await res.json();
    } catch (e) {
      console.warn("Sin comunicación activa o servicio offline:", e);
      return null;
    }
  },

  getHistorialChat: async (mediumId) => {
    try {
      const res = await fetch(`${MENSAJERIA_URL}/historial/${mediumId}`);
      if (res.status === 404) return [];
      if (!res.ok) return [];
      return await res.json();
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

