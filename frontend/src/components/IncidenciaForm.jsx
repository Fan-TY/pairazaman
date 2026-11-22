
import { useState } from 'react'
import { crearIncidencia } from '../api/incidencias'

export default function IncidenciaForm({ onGuardado, onCancelar }) {
  const [formulario, setFormulario] = useState({
    titulo: '',
    descripcion: '',
    fecha: new Date().toISOString().slice(0, 10),
    hora: '',
    tipo: 'OTRO',
    prioridad: 'MEDIA',
    ubicacion: '',
    camaraId: ''
  })

  const [error, setError] = useState('')
  const [guardando, setGuardando] = useState(false)

  function cambiarCampo(e) {
    const { name, value } = e.target
    setFormulario((anterior) => ({
      ...anterior,
      [name]: value
    }))
  }

  async function manejarEnvio(e) {
    e.preventDefault()
    setError('')

    try {
      setGuardando(true)

      const datos = {
        ...formulario,
        camaraId: formulario.camaraId
          ? Number(formulario.camaraId)
          : null
      }

      await crearIncidencia(datos)

      setFormulario({
        titulo: '',
        descripcion: '',
        fecha: new Date().toISOString().slice(0, 10),
        hora: '',
        tipo: 'OTRO',
        prioridad: 'MEDIA',
        ubicacion: '',
        camaraId: ''
      })

      if (onGuardado) onGuardado()
    } catch (e) {
      setError(e.message)
    } finally {
      setGuardando(false)
    }
  }

  return (
    <section className="panel">
      <h2>Registrar incidencia</h2>

      <form className="formulario" onSubmit={manejarEnvio}>
        <label>Título</label>
        <input
          name="titulo"
          value={formulario.titulo}
          onChange={cambiarCampo}
          maxLength="150"
          required
        />

        <label>Descripción</label>
        <textarea
          name="descripcion"
          value={formulario.descripcion}
          onChange={cambiarCampo}
          rows="4"
        />

        <label>Fecha</label>
        <input
          type="date"
          name="fecha"
          value={formulario.fecha}
          onChange={cambiarCampo}
          required
        />

        <label>Hora</label>
        <input
          type="time"
          name="hora"
          value={formulario.hora}
          onChange={cambiarCampo}
        />

        <label>Tipo de incidencia</label>
        <select
          name="tipo"
          value={formulario.tipo}
          onChange={cambiarCampo}
        >
          <option value="OTRO">Otro</option>
          <option value="TECNICA">Técnica</option>
          <option value="SEGURIDAD">Seguridad</option>
          <option value="MANTENIMIENTO">Mantenimiento</option>
        </select>

        <label>Prioridad</label>
        <select
          name="prioridad"
          value={formulario.prioridad}
          onChange={cambiarCampo}
        >
          <option value="BAJA">Baja</option>
          <option value="MEDIA">Media</option>
          <option value="ALTA">Alta</option>
          <option value="CRITICA">Crítica</option>
        </select>

        <label>Ubicación</label>
        <input
          name="ubicacion"
          value={formulario.ubicacion}
          onChange={cambiarCampo}
          maxLength="200"
          required
        />

        <label>ID de cámara (opcional)</label>
        <input
          type="number"
          name="camaraId"
          min="1"
          value={formulario.camaraId}
          onChange={cambiarCampo}
        />

        {error && <p role="alert">{error}</p>}

        <button type="submit" disabled={guardando}>
          {guardando ? 'Guardando...' : 'Registrar incidencia'}
        </button>

        {onCancelar && (
          <button type="button" onClick={onCancelar}>
            Cancelar
          </button>
        )}
      </form>
    </section>
  )
}