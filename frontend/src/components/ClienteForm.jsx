import { useEffect, useState } from 'react'
import { actualizarCliente, crearCliente } from '../api/clientes.js'

const FORM_VACIO = {
  dniRuc: '',
  nombresRazonSocial: '',
  direccion: '',
  telefono: '',
  correo: '',
  tipo: 'HOGAR',
  estado: 'ACTIVO',
}

const TIPOS = [
  { value: 'HOGAR', label: 'Hogar' },
  { value: 'COMERCIO', label: 'Comercio' },
  { value: 'INSTITUCION', label: 'Institución' },
]

const ESTADOS = [
  { value: 'ACTIVO', label: 'Activo' },
  { value: 'INACTIVO', label: 'Inactivo' },
]

export default function ClienteForm({ cliente, onSaved, onCancel }) {
  const [form, setForm] = useState(FORM_VACIO)
  const [errores, setErrores] = useState({})
  const [errorGeneral, setErrorGeneral] = useState('')
  const [enviando, setEnviando] = useState(false)

  const esEdicion = Boolean(cliente?.id)

  useEffect(() => {
    if (cliente) {
      setForm({
        dniRuc: cliente.dniRuc ?? '',
        nombresRazonSocial: cliente.nombresRazonSocial ?? '',
        direccion: cliente.direccion ?? '',
        telefono: cliente.telefono ?? '',
        correo: cliente.correo ?? '',
        tipo: cliente.tipo ?? 'HOGAR',
        estado: cliente.estado ?? 'ACTIVO',
      })
    } else {
      setForm(FORM_VACIO)
    }
    setErrores({})
    setErrorGeneral('')
  }, [cliente])

  const cambiar = (evento) => {
    const { name, value } = evento.target
    setForm((actual) => ({ ...actual, [name]: value }))
  }

  const enviar = async (evento) => {
    evento.preventDefault()
    setEnviando(true)
    setErrores({})
    setErrorGeneral('')

    const payload = {
      ...form,
      dniRuc: form.dniRuc.trim(),
      nombresRazonSocial: form.nombresRazonSocial.trim(),
      direccion: form.direccion.trim() || null,
      telefono: form.telefono.trim() || null,
      correo: form.correo.trim() || null,
    }

    try {
      if (esEdicion) {
        await actualizarCliente(cliente.id, payload)
      } else {
        await crearCliente(payload)
      }
      onSaved()
    } catch (error) {
      if (error.campos) {
        setErrores(error.campos)
      }
      setErrorGeneral(error.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="panel">
      <div className="panel-titulo">
        <div>
          <h2>{esEdicion ? 'Editar cliente' : 'Registrar cliente'}</h2>
          <p>Los campos marcados con * son obligatorios.</p>
        </div>
        <button type="button" className="secundario" onClick={onCancel}>
          Volver
        </button>
      </div>

      {errorGeneral && <div className="alerta error">{errorGeneral}</div>}

      <form className="formulario" onSubmit={enviar} noValidate>
        <label>
          <span>DNI / RUC *</span>
          <input
            name="dniRuc"
            value={form.dniRuc}
            onChange={cambiar}
            maxLength={13}
            placeholder="10 dígitos (cédula) o 13 (RUC)"
            required
          />
          {errores.dniRuc && <small className="campo-error">{errores.dniRuc}</small>}
        </label>

        <label>
          <span>Nombres o razón social *</span>
          <input
            name="nombresRazonSocial"
            value={form.nombresRazonSocial}
            onChange={cambiar}
            maxLength={150}
            required
          />
          {errores.nombresRazonSocial && (
            <small className="campo-error">{errores.nombresRazonSocial}</small>
          )}
        </label>

        <label>
          <span>Tipo de cliente *</span>
          <select name="tipo" value={form.tipo} onChange={cambiar} required>
            {TIPOS.map((tipo) => (
              <option key={tipo.value} value={tipo.value}>
                {tipo.label}
              </option>
            ))}
          </select>
          {errores.tipo && <small className="campo-error">{errores.tipo}</small>}
        </label>

        <label>
          <span>Estado</span>
          <select name="estado" value={form.estado} onChange={cambiar}>
            {ESTADOS.map((estado) => (
              <option key={estado.value} value={estado.value}>
                {estado.label}
              </option>
            ))}
          </select>
        </label>

        <label className="ancho-completo">
          <span>Dirección</span>
          <input
            name="direccion"
            value={form.direccion}
            onChange={cambiar}
            maxLength={200}
          />
          {errores.direccion && <small className="campo-error">{errores.direccion}</small>}
        </label>

        <label>
          <span>Teléfono</span>
          <input
            name="telefono"
            value={form.telefono}
            onChange={cambiar}
            maxLength={20}
          />
          {errores.telefono && <small className="campo-error">{errores.telefono}</small>}
        </label>

        <label>
          <span>Correo</span>
          <input
            name="correo"
            type="email"
            value={form.correo}
            onChange={cambiar}
            maxLength={100}
          />
          {errores.correo && <small className="campo-error">{errores.correo}</small>}
        </label>

        <div className="acciones ancho-completo">
          <button type="submit" disabled={enviando}>
            {enviando ? 'Guardando…' : esEdicion ? 'Guardar cambios' : 'Registrar cliente'}
          </button>
          <button type="button" className="secundario" onClick={onCancel}>
            Cancelar
          </button>
        </div>
      </form>
    </section>
  )
}
