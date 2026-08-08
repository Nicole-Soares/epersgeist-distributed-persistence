import apiFetch from './apiFetch';

const EPERSGEIST_URL = import.meta.env.VITE_EPERSGEIST_API_URL || 'http://localhost:8080';
const BASE_URL = `${EPERSGEIST_URL}/medium`;

export const mediumService = {
  getMediums: async () => {
    return apiFetch(`${BASE_URL}`, {}, "Error al recuperar los mediums");
  },

  getMedium: async (id) => {
    return apiFetch(`${BASE_URL}/${id}`, {}, "Error al consultar el medium");
  },

  crearMedium: async (data) => {
    return apiFetch(`${BASE_URL}`, {
      method: 'POST',
      body: JSON.stringify(data)
    }, "Error al crear el medium");
  },

  actualizarMedium: async (id, data) => {
    return apiFetch(`${BASE_URL}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }, "Error al actualizar el medium");
  },

  moverMedium: async (mediumId, latitud, longitud) => {
    return apiFetch(`${BASE_URL}/${mediumId}/mover/${latitud}/${longitud}`, {
      method: 'PATCH'
    }, "Error al mover el medium");
  },

  descansarMedium: async (mediumId) => {
    return apiFetch(`${BASE_URL}/descansar/${mediumId}`, {
      method: 'PATCH'
    }, "Error al hacer descansar al medium");
  },

  invocarEspiritu: async (mediumId, espirituId) => {
    return apiFetch(`${BASE_URL}/${mediumId}/invocar/${espirituId}`, {
      method: 'PATCH'
    }, "Error al invocar el espíritu");
  },

  exorcizar: async (mediumExorcistaId, mediumPoseidoId) => {
    return apiFetch(`${BASE_URL}/${mediumExorcistaId}/exorcizar/${mediumPoseidoId}`, {
      method: 'PATCH'
    }, "Error durante el ritual de exorcismo");
  },

  // Kafka message producer call
  enviarMensajeMedium: async (mediumId, mensaje) => {
    return apiFetch(`${BASE_URL}/${mediumId}/mensaje`, {
      method: 'POST',
      body: JSON.stringify({ mensaje })
    }, "Error al transmitir el mensaje al Spirit Box");
  },

  // Kafka conjecture producer call
  identificarEspiritu: async (mediumId, conjeturaNombre) => {
    return apiFetch(`${BASE_URL}/${mediumId}/identificar`, {
      method: 'POST',
      body: JSON.stringify({ nombre: conjeturaNombre })
    }, "Error al enviar la conjetura de identificación");
  }
};
