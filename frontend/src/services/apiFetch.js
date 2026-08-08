const apiFetch = async (url, options = {}, defaultMessage = "Error en la petición al servidor") => {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  const fetchOptions = {
    ...options,
    headers,
  };

  let response;
  try {
    response = await fetch(url, fetchOptions);
  } catch (err) {
    console.error("Error de Red / Servidor no disponible:", err);
    throw new Error("No se pudo conectar con el servidor. Verifica que los servicios backend estén en marcha.");
  }

  if (response.status === 401) {
    console.error("Error 401: No autorizado o sesión expirada.");
    window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    const error = new Error("Tu sesión expiró o no tienes autorización.");
    error.status = 401;
    throw error;
  }

  if (response.status === 404) {
    console.error("Error 404: Recurso no encontrado.");
    const error = new Error("El recurso solicitado no fue encontrado (404).");
    error.status = 404;
    throw error;
  }

  if (!response.ok) {
    let errorData = {};
    try {
      errorData = await response.json();
    } catch (e) {
      errorData.message = response.statusText || defaultMessage;
    }
    const error = new Error(errorData.message || errorData.error || defaultMessage);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return {};
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return await response.json();
  }

  return await response.text();
};

export default apiFetch;
