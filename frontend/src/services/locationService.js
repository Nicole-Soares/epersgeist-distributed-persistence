import apiFetch from './apiFetch';

const EPERSGEIST_URL = import.meta.env.VITE_EPERSGEIST_API_URL || 'http://localhost:8080';
const BASE_URL = `${EPERSGEIST_URL}/ubicacion`;

export const locationService = {
  getUbicaciones: async () => {
    return apiFetch(`${BASE_URL}`, {}, "Error al obtener las ubicaciones");
  },

  getUbicacion: async (id) => {
    return apiFetch(`${BASE_URL}/${id}`, {}, "Error al obtener el detalle de la ubicación");
  },

  crearUbicacion: async (data) => {
    return apiFetch(`${BASE_URL}`, {
      method: 'POST',
      body: JSON.stringify(data)
    }, "Error al crear la ubicación");
  },

  getEspiritusEnUbicacion: async (id) => {
    return apiFetch(`${BASE_URL}/${id}/espiritus`, {}, "Error al obtener espíritus en la ubicación");
  },

  getMediumsSinEspiritusEnUbicacion: async (id) => {
    return apiFetch(`${BASE_URL}/${id}/mediumsSinEspiritus`, {}, "Error al obtener mediums en la ubicación");
  },

  conectarUbicaciones: async (origenId, destinoId, costo) => {
    return apiFetch(`${BASE_URL}/${origenId}/conectarA/${destinoId}?costo_conexion=${costo}`, {
      method: 'PATCH'
    }, "Error al conectar las ubicaciones");
  },

  getCaminoMasCorto: async (origenId, destinoId) => {
    return apiFetch(`${BASE_URL}/${origenId}/caminoMasCorto/${destinoId}`, {}, "Error al calcular el camino más corto");
  },

  getCaminoMasRentable: async (origenId, destinoId) => {
    return apiFetch(`${BASE_URL}/${origenId}/caminoMasRentableA/${destinoId}`, {}, "Error al calcular el camino más rentable");
  },

  getUbicacionesSobrecargadas: async (umbral = 2) => {
    return apiFetch(`${BASE_URL}/sobrecargadas?umbral=${umbral}`, {}, "Error al consultar ubicaciones sobrecargadas");
  }
};
