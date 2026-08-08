import apiFetch from './apiFetch';

const EPERSGEIST_URL = import.meta.env.VITE_EPERSGEIST_API_URL || 'http://localhost:8080';
const BASE_URL = `${EPERSGEIST_URL}/espiritu`;

export const spiritService = {
  getEspiritus: async () => {
    return apiFetch(`${BASE_URL}`, {}, "Error al obtener la lista de espíritus");
  },

  getEspiritu: async (id) => {
    return apiFetch(`${BASE_URL}/${id}`, {}, "Error al obtener el espíritu");
  },

  crearEspiritu: async (data) => {
    return apiFetch(`${BASE_URL}`, {
      method: 'POST',
      body: JSON.stringify(data)
    }, "Error al crear el espíritu");
  },

  getDemoniosPaginados: async (direccion = 'DESC', pagina = 0, cantidadPorPagina = 10) => {
    return apiFetch(`${BASE_URL}/demonios?direccion=${direccion}&pagina=${pagina}&cantidadPorPagina=${cantidadPorPagina}`, {}, "Error al obtener ranking de demonios");
  },

  conectarEspiritu: async (espirituId, mediumId) => {
    return apiFetch(`${BASE_URL}/${espirituId}/conectar/${mediumId}`, {
      method: 'PATCH'
    }, "Error al conectar espíritu con el medium");
  },

  dominarEspiritu: async (espirituADominarId, espirituDominanteId) => {
    return apiFetch(`${BASE_URL}/${espirituADominarId}/dominar/${espirituDominanteId}`, {
      method: 'PATCH'
    }, "Error al dominar el espíritu");
  }
};
