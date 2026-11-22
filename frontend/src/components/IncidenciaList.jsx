
import { useEffect, useState } from 'react'
import {
  listarIncidencias,
  eliminarIncidencia
} from '../api/incidencias'

export default function IncidenciaList() {
  const [incidencias, setIncidencias] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  async function cargarIncidencias() {
    try {
      setCargando(true)
      setError('')
      const datos = await listarIncidencias()
      setIncidencias(Array.isArray(datos) ? datos : [])
    } catch (e) {
      setError(e.message)
    } finally {
      setCargando(false)
    }
  }

  async function eliminar(id) {
    if (!window.confirm('¿Deseas eliminar esta incidencia?')) return

    try {
      await eliminarIncidencia(id)
      await cargarIncidencias()
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    cargarIncidencias()
  }, [])

  if (cargando) return <p>Cargando incidencias...</p>

  return (
    <section className="panel">
      <div className="panel-titulo">
        <h2>Gestión de incidencias</h2>
        <button onClick={cargarIncidencias}>Actualizar</button>
      </div>

      {error && <p role="alert">{error}</p>}

      {incidencias.length === 0 ? (
        <p>No hay incidencias registradas.</p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Tipo</th>
                <th>Prioridad</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Ubicación</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {incidencias.map((incidencia) => (
                <tr key={incidencia.id}>
                  <td>{incidencia.id}</td>
                  <td>{incidencia.titulo}</td>
                  <td>{incidencia.tipo}</td>
                  <td>{incidencia.prioridad}</td>
                  <td>{incidencia.fecha}</td>
                  <td>{incidencia.estado}</td>
                  <td>{incidencia.ubicacion}</td>
                  <td>
                    <button
                      type="button"
                      onClick={() => eliminar(incidencia.id)}
                    >
                      Eliminar
                    </button>
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