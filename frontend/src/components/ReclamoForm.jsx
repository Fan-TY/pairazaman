
import { useEffect, useState } from 'react'
import { crearReclamo } from '../api/reclamos'
import { listarClientes } from '../api/clientes'

export default function ReclamoForm({ onGuardado, onCancelar }) {
  const [clienteId, setClienteId] = useState('')
  const [clientes, setClientes] = useState([])
  const [descripcion, setDescripcion] = useState('')
  const [error, setError] = useState('')
  const [cargandoClientes, setCargandoClientes] = useState(true)
  const [guardando, setGuardando] = useState(false)

  useEffect(() => {
    async function cargarClientes() {
      try {
        setCargandoClientes(true)
        const respuesta = await listarClientes()

        const lista = Array.isArray(respuesta)
          ? respuesta
          : respuesta?.content || []

        setClientes(lista)
      } catch (e) {
        setError('No se pudieron cargar los clientes: ' + e.message)
      } finally {
        setCargandoClientes(false)
      }
    }

    cargarClientes()
  }, [])

  function obtenerNombre(cliente) {
    const nombreCompleto = [
      cliente.nombres,
      cliente.apellidos
    ].filter(Boolean).join(' ')

    return (
      cliente.nombreCompleto ||
      cliente.nombre ||
      nombreCompleto ||
      cliente.razonSocial ||
      `Cliente #${cliente.id}`
    )
  }

  async function manejarEnvio(e) {
    e.preventDefault()
    setError('')

    if (!clienteId || !descripcion.trim()) {
      setError('Selecciona un cliente y escribe la descripción.')
      return
    }

    try {
      setGuardando(true)

      await crearReclamo({
        clienteId: Number(clienteId),
        descripcion: descripcion.trim()
      })

      setClienteId('')
      setDescripcion('')

      if (onGuardado) onGuardado()
    } catch (e) {
      setError(e.message)
    } finally {
      setGuardando(false)
    }
  }

  return (
    <section className="panel">
      <h2>Registrar reclamo</h2>

      <form className="formulario" onSubmit={manejarEnvio}>
        <label htmlFor="clienteId">Seleccionar cliente</label>

        <select
          id="clienteId"
          value={clienteId}
          onChange={(e) => setClienteId(e.target.value)}
          required
          disabled={cargandoClientes || clientes.length === 0}
        >
          <option value="">
            {cargandoClientes
              ? 'Cargando clientes...'
              : clientes.length === 0
                ? 'No hay clientes registrados'
                : 'Selecciona un cliente'}
          </option>

          {clientes.map((cliente) => (
            <option key={cliente.id} value={cliente.id}>
              {obtenerNombre(cliente)} (ID: {cliente.id})
            </option>
          ))}
        </select>

        <label htmlFor="descripcion">Descripción del reclamo</label>

        <textarea
          id="descripcion"
          rows="4"
          maxLength="500"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
          placeholder="Describe el reclamo del cliente"
          required
        />

        {error && <p role="alert">{error}</p>}

        <button
          type="submit"
          disabled={guardando || cargandoClientes || clientes.length === 0}
        >
          {guardando ? 'Guardando...' : 'Registrar reclamo'}
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