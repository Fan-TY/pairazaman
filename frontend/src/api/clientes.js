const BASE = '/api/clientes'

async function solicitar(url, opciones = {}) {
  const respuesta = await fetch(url, {
    ...opciones,
    headers: {
      'Content-Type': 'application/json',
      ...(opciones.headers || {}),
    },
  })

  if (respuesta.status === 204) {
    return null
  }

  const texto = await respuesta.text()
  let datos = null
  try {
    datos = texto ? JSON.parse(texto) : null
  } catch {
    datos = null
  }

  if (!respuesta.ok) {
    const error = new Error(datos?.mensaje || `Error ${respuesta.status}`)
    error.status = respuesta.status
    error.campos = datos?.campos
    throw error
  }

  return datos
}

export function listarClientes({ nombre, estado } = {}) {
  const params = new URLSearchParams()
  if (nombre) params.set('nombre', nombre)
  if (estado) params.set('estado', estado)
  const query = params.toString()
  return solicitar(`${BASE}${query ? `?${query}` : ''}`)
}

export function obtenerCliente(id) {
  return solicitar(`${BASE}/${id}`)
}

export function crearCliente(cliente) {
  return solicitar(BASE, {
    method: 'POST',
    body: JSON.stringify(cliente),
  })
}

export function actualizarCliente(id, cliente) {
  return solicitar(`${BASE}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(cliente),
  })
}

export function cambiarEstadoCliente(id, estado) {
  return solicitar(`${BASE}/${id}/estado`, {
    method: 'PATCH',
    body: JSON.stringify({ estado }),
  })
}

export function obtenerHistorial(id) {
  return solicitar(`${BASE}/${id}/historial`)
}
