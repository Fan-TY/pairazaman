
import { useEffect, useState } from 'react'
import { listarReclamos, cambiarEstadoReclamo } from '../api/reclamos'

export default function ReclamoList() {
  const [reclamos, setReclamos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  async function cargarReclamos() {
    try {
      setCargando(true)
      setError('')
      const datos = await listarReclamos()
      setReclamos(Array.isArray(datos) ? datos : [])
    } catch (e) {
      setError(e.message)
    } finally {
      setCargando(false)
    }
  }

  async function actualizarEstado(id, estado) {
    try {
      await cambiarEstadoReclamo(id, estado)
      await cargarReclamos()
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    cargarReclamos()
  }, [])

  if (cargando) return <p>Cargando reclamos...</p>

  return (
    <section className="panel">
      <div className="panel-titulo">
        <h2>Gestión de reclamos</h2>
        <button onClick={cargarReclamos}>Actualizar</button>
      </div>

      {error && <p role="alert">{error}</p>}

      {reclamos.length === 0 ? (
        <p>No hay reclamos registrados.</p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Descripción</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              {reclamos.map((reclamo) => (
                <tr key={reclamo.id}>
                  <td>{reclamo.id}</td>
                  <td>{reclamo.descripcion}</td>
                  <td>{reclamo.fechaRegistro || '-'}</td>
                  <td>{reclamo.estado}</td>
                  <td>
                    <select
                      value={reclamo.estado || 'PENDIENTE'}
                      onChange={(e) =>
                        actualizarEstado(reclamo.id, e.target.value)
                      }
                    >
                      <option value="PENDIENTE">Pendiente</option>
                      <option value="EN_PROCESO">En proceso</option>
                      <option value="RESUELTO">Resuelto</option>
                      <option value="CERRADO">Cerrado</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}