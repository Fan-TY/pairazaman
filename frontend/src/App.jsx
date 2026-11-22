
import { useState } from 'react'

import ClienteList from './components/ClienteList.jsx'
import ClienteForm from './components/ClienteForm.jsx'
import ClienteFicha from './components/ClienteFicha.jsx'
import { obtenerCliente } from './api/clientes.js'

import ReclamoList from './components/ReclamoList.jsx'
import ReclamoForm from './components/ReclamoForm.jsx'
import IncidenciaList from './components/IncidenciaList.jsx'
import IncidenciaForm from './components/IncidenciaForm.jsx'

export default function App() {
  const [vista, setVista] = useState('lista')
  const [clienteEdicion, setClienteEdicion] = useState(null)
  const [clienteFichaId, setClienteFichaId] = useState(null)
  const [refresco, setRefresco] = useState(0)

  const abrirNuevo = () => {
    setClienteEdicion(null)
    setVista('formulario')
  }

  const abrirEdicion = async (id) => {
    try {
      const cliente = await obtenerCliente(id)
      setClienteEdicion(cliente)
      setVista('formulario')
    } catch (error) {
      alert(error.message)
    }
  }

  const alGuardar = () => {
    setVista('lista')
    setRefresco((valor) => valor + 1)
  }

  const abrirFicha = (id) => {
    setClienteFichaId(id)
    setVista('ficha')
  }

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>Pairazaman</h1>
          <p>Gestión de Clientes, Reclamos e Incidencias</p>
        </div>

        <nav>
          <button
            className={vista === 'lista' || vista === 'formulario' || vista === 'ficha' ? 'activo' : ''}
            onClick={() => setVista('lista')}
          >
            Clientes
          </button>

          <button onClick={abrirNuevo}>
            + Nuevo cliente
          </button>

          <button
            className={vista === 'reclamos' || vista === 'nuevoReclamo' ? 'activo' : ''}
            onClick={() => setVista('reclamos')}
          >
            Reclamos
          </button>

          <button
            className={vista === 'incidencias' || vista === 'nuevaIncidencia' ? 'activo' : ''}
            onClick={() => setVista('incidencias')}
          >
            Incidencias
          </button>
        </nav>
      </header>

      <main className="contenido">

        {vista === 'lista' && (
          <ClienteList
            key={refresco}
            onNuevo={abrirNuevo}
            onEditar={abrirEdicion}
            onFicha={abrirFicha}
          />
        )}

        {vista === 'formulario' && (
          <ClienteForm
            cliente={clienteEdicion}
            onSaved={alGuardar}
            onCancel={() => setVista('lista')}
          />
        )}

        {vista === 'ficha' && (
          <ClienteFicha
            clienteId={clienteFichaId}
            onVolver={() => setVista('lista')}
          />
        )}

        {vista === 'reclamos' && (
          <>
            <button onClick={() => setVista('nuevoReclamo')}>
              + Registrar reclamo
            </button>
            <ReclamoList key={refresco} />
          </>
        )}

        {vista === 'nuevoReclamo' && (
          <ReclamoForm
            onGuardado={() => {
              setRefresco((valor) => valor + 1)
              setVista('reclamos')
            }}
            onCancelar={() => setVista('reclamos')}
          />
        )}

        {vista === 'incidencias' && (
          <>
            <button onClick={() => setVista('nuevaIncidencia')}>
              + Registrar incidencia
            </button>
            <IncidenciaList key={refresco} />
          </>
        )}

        {vista === 'nuevaIncidencia' && (
          <IncidenciaForm
            onGuardado={() => {
              setRefresco((valor) => valor + 1)
              setVista('incidencias')
            }}
            onCancelar={() => setVista('incidencias')}
          />
        )}

      </main>
    </div>
  )
}
