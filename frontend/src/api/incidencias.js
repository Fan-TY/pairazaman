
const BASE = '/api/incidencias'

async function solicitar(url, opciones = {}) {
  const respuesta = await fetch(url, {
    ...opciones,
    headers: {
      'Content-Type': 'application/json',
      ...(opciones.headers || {}),
    },
  })

  if (respuesta.status === 204) return null

  const texto = await respuesta.text()
  let datos = null

  try {
    datos = texto ? JSON.parse(texto) : null
  } catch {
    datos = null
  }

  if (!respuesta.ok) {
    throw new Error(datos?.mensaje || `Error ${respuesta.status}`)
  }

  return datos
}

export function listarIncidencias() {
  return solicitar(BASE)
}

export function obtenerIncidencia(id) {
  return solicitar(`${BASE}/${id}`)
}

export function crearIncidencia(incidencia) {
  return solicitar(BASE, {
    method: 'POST',
    body: JSON.stringify(incidencia),
  })
}

export function actualizarIncidencia(id, incidencia) {
  return solicitar(`${BASE}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(incidencia),
  })
}

export function cambiarEstadoIncidencia(id, estado) {
  return solicitar(`${BASE}/${id}/estado`, {
    method: 'PATCH',
    body: JSON.stringify({ estado }),
  })
}

export function eliminarIncidencia(id) {
  return solicitar(`${BASE}/${id}`, {
    method: 'DELETE',
  })
}
