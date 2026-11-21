import { useState } from 'react'
import ClienteList from './components/ClienteList.jsx'
import ClienteForm from './components/ClienteForm.jsx'
import ClienteFicha from './components/ClienteFicha.jsx'
import { obtenerCliente } from './api/clientes.js'

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
          <p>Gestión de Clientes · RF-01 / ECU-01</p>
        </div>
        <nav>
          <button
            type="button"
            className={vista === 'lista' ? 'activo' : ''}
            onClick={() => setVista('lista')}
          >
            Clientes
          </button>
          <button type="button" onClick={abrirNuevo}>
            + Nuevo cliente
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
      </main>
    </div>
  )
}
