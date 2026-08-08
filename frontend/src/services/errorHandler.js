export function formatErrorMessage(error) {
  if (error && error.status === 401) {
    return 'Tu sesión ha expirado o no tienes permisos (401).';
  }
  if (error && error.status === 404) {
    return 'Recurso no encontrado (404).';
  }
  if (error && error.message) {
    return error.message;
  }
  return 'Ocurrió un error inesperado al procesar la operación.';
}
