import { useEffect, useState } from 'react'
import { obtenerHistorial } from '../api/clientes.js'

const TABS = [
  { key: 'contratos', label: 'Contratos' },
  { key: 'pagos', label: 'Pagos' },
  { key: 'reclamos', label: 'Reclamos' },
  { key: 'instalaciones', label: 'Instalaciones' },
]

function Contratos({ datos }) {
  if (datos.length === 0) return <p className="vacio">Sin contratos registrados.</p>
  return (
    <table>
      <thead>
        <tr><th>Código</th><th>Fecha inicio</th><th>Estado</th></tr>
      </thead>
      <tbody>
        {datos.map((item) => (
          <tr key={item.id}>
            <td>{item.codigo}</td>
            <td>{item.fechaInicio || '—'}</td>
            <td>{item.estado || '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function Pagos({ datos }) {
  if (datos.length === 0) return <p className="vacio">Sin pagos registrados.</p>
  return (
    <table>
      <thead>
        <tr><th>Monto</th><th>Fecha pago</th><th>Estado</th></tr>
      </thead>
      <tbody>
        {datos.map((item) => (
          <tr key={item.id}>
            <td>{item.monto != null ? `$ ${Number(item.monto).toFixed(2)}` : '—'}</td>
            <td>{item.fechaPago || '—'}</td>
            <td>{item.estado || '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function Reclamos({ datos }) {
  if (datos.length === 0) return <p className="vacio">Sin reclamos registrados.</p>
  return (
    <table>
      <thead>
        <tr><th>Descripción</th><th>Fecha registro</th><th>Estado</th></tr>
      </thead>
      <tbody>
        {datos.map((item) => (
          <tr key={item.id}>
            <td>{item.descripcion || '—'}</td>
            <td>{item.fechaRegistro ? item.fechaRegistro.replace('T', ' ').slice(0, 16) : '—'}</td>
            <td>{item.estado || '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function Instalaciones({ datos }) {
  if (datos.length === 0) return <p className="vacio">Sin instalaciones registradas.</p>
  return (
    <table>
      <thead>
        <tr><th>Fecha instalación</th><th>Dirección</th><th>Estado</th></tr>
      </thead>
      <tbody>
        {datos.map((item) => (
          <tr key={item.id}>
            <td>{item.fechaInstalacion || '—'}</td>
            <td>{item.direccion || '—'}</td>
            <td>{item.estado || '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

const VISTAS = {
  contratos: Contratos,
  pagos: Pagos,
  reclamos: Reclamos,
  instalaciones: Instalaciones,
}

export default function ClienteFicha({ clienteId, onVolver }) {
  const [historial, setHistorial] = useState(null)
  const [tab, setTab] = useState('contratos')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let activo = true

    async function cargar() {
      setCargando(true)
      setError('')
      try {
        const datos = await obtenerHistorial(clienteId)
        if (activo) setHistorial(datos)
      } catch (e) {
        if (activo) setError(e.message)
      } finally {
        if (activo) setCargando(false)
      }
    }

    cargar()
    return () => {
      activo = false
    }
  }, [clienteId])

  const Vista = VISTAS[tab]

  return (
    <section className="panel">
      <div className="panel-titulo">
        <div>
          <h2>Ficha del cliente</h2>
          {historial && (
            <p>
              {historial.nombresRazonSocial} · DNI/RUC {historial.dniRuc}
            </p>
          )}
        </div>
        <button type="button" className="secundario" onClick={onVolver}>
          Volver
        </button>
      </div>

      {cargando && <p className="vacio">Cargando historial…</p>}
      {error && <div className="alerta error">{error}</div>}

      {!cargando && historial && (
        <>
          <div className="tabs" role="tablist">
            {TABS.map((item) => (
              <button
                key={item.key}
                type="button"
                role="tab"
                aria-selected={tab === item.key}
                className={tab === item.key ? 'tab activo' : 'tab'}
                onClick={() => setTab(item.key)}
              >
                {item.label}
                <span className="contador">{historial[item.key]?.length ?? 0}</span>
              </button>
            ))}
          </div>

          <div className="tabla-contenedor">
            <Vista datos={historial[tab] ?? []} />
          </div>
        </>
      )}
    </section>
  )
}
