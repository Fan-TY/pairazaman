import { useCallback, useEffect, useState } from 'react'
import { cambiarEstadoCliente, listarClientes } from '../api/clientes.js'

const FILTROS_VACIOS = { nombre: '', estado: '' }

export default function ClienteList({ onNuevo, onEditar, onFicha }) {
  const [filtros, setFiltros] = useState(FILTROS_VACIOS)
  const [clientes, setClientes] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  const cargar = useCallback(async (valores) => {
    setCargando(true)
    setError('')
    try {
      const datos = await listarClientes({
        nombre: valores.nombre.trim() || undefined,
        estado: valores.estado || undefined,
      })
      setClientes(datos)
    } catch (e) {
      setError(e.message)
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    const temporizador = setTimeout(() => cargar(filtros), 300)
    return () => clearTimeout(temporizador)
  }, [filtros, cargar])

  const cambiar = (evento) => {
    const { name, value } = evento.target
    setFiltros((actual) => ({ ...actual, [name]: value }))
  }

  const alternarEstado = async (cliente) => {
    const nuevoEstado = cliente.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'
    try {
      await cambiarEstadoCliente(cliente.id, nuevoEstado)
      await cargar(filtros)
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <section className="panel">
      <div className="panel-titulo">
        <div>
          <h2>Clientes registrados</h2>
          <p>{clientes.length} resultado(s)</p>
        </div>
        <button type="button" onClick={onNuevo}>
          + Nuevo cliente
        </button>
      </div>

      <div className="barra-busqueda">
        <input
          name="nombre"
          value={filtros.nombre}
          onChange={cambiar}
          placeholder="Buscar por nombres o razón social…"
        />
        <select name="estado" value={filtros.estado} onChange={cambiar}>
          <option value="">Todos los estados</option>
          <option value="ACTIVO">Activos</option>
          <option value="INACTIVO">Inactivos</option>
        </select>
      </div>

      {error && <div className="alerta error">{error}</div>}

      <div className="tabla-contenedor">
        <table>
          <thead>
            <tr>
              <th>DNI / RUC</th>
              <th>Nombres / Razón social</th>
              <th>Tipo</th>
              <th>Teléfono</th>
              <th>Correo</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {cargando && (
              <tr>
                <td colSpan={7} className="vacio">Cargando…</td>
              </tr>
            )}

            {!cargando && clientes.length === 0 && (
              <tr>
                <td colSpan={7} className="vacio">No hay clientes que coincidan con la búsqueda.</td>
              </tr>
            )}

            {!cargando && clientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.dniRuc}</td>
                <td>{cliente.nombresRazonSocial}</td>
                <td>{cliente.tipo}</td>
                <td>{cliente.telefono || '—'}</td>
                <td>{cliente.correo || '—'}</td>
                <td>
                  <span className={`etiqueta ${cliente.estado === 'ACTIVO' ? 'activa' : 'inactiva'}`}>
                    {cliente.estado}
                  </span>
                </td>
                <td>
                  <div className="acciones-tabla">
                    <button type="button" className="secundario" onClick={() => onEditar(cliente.id)}>
                      Editar
                    </button>
                    <button
                      type="button"
                      className="secundario"
                      onClick={() => alternarEstado(cliente)}
                    >
                      {cliente.estado === 'ACTIVO' ? 'Desactivar' : 'Activar'}
                    </button>
                    <button type="button" className="secundario" onClick={() => onFicha(cliente.id)}>
                      Ficha
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
