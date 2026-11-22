
const BASE = '/api/reclamos'

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

export function listarReclamos() {
  return solicitar(BASE)
}

export function crearReclamo(reclamo) {
  return solicitar(BASE, {
    method: 'POST',
    body: JSON.stringify(reclamo),
  })
}

export function cambiarEstadoReclamo(id, estado) {
  return solicitar(`${BASE}/${id}/estado`, {
    method: 'PATCH',
    body: JSON.stringify({ estado }),
  })
}
