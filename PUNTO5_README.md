# Punto 5 — Programación de instalaciones y personal técnico (RF-03, ECU-03)

Uso: Secretaria (también Gerente). Rutas nuevas:

| Ruta | Qué hace |
|---|---|
| `/instalaciones` | Listado de órdenes con filtros por estado y por técnico; cambio rápido de estado |
| `/instalaciones/nueva` y `/instalaciones/editar/{id}` | Formulario: contrato, tipo, fecha y hora, dirección, observaciones, técnico |
| `/instalaciones/agenda` | Agenda semanal (lunes a domingo), con filtro por técnico |
| `/tecnicos` | Registro y edición del personal técnico: datos, zona y disponibilidad |

Al asignar o cambiar el técnico (o reprogramar fecha/hora) se crea un aviso (`Notificacion`)
que el técnico ve en su panel de inicio (`/dashboard`) hasta marcarlo como leído.

Reglas: no se programa en fecha pasada, un técnico no puede tener dos órdenes a la misma hora,
y no se asigna a técnicos inactivos o "No disponibles".

## Datos de prueba (PostgreSQL)

Aún no hay pantalla de contratos, así que para probar crea un cliente y un contrato:

```sql
INSERT INTO clientes (dni_ruc, nombres_razon_social, direccion, telefono, correo, tipo, estado, fecha_registro)
VALUES ('0102030405', 'María Pairazaman', 'Av. Principal 123', '999999999', 'maria@example.com', 'HOGAR', 'ACTIVO', now());

INSERT INTO contratos (codigo, fecha_inicio, estado, cliente_id)
VALUES ('CT-001', current_date, 'VIGENTE', (SELECT id FROM clientes WHERE dni_ruc = '0102030405'));
```

Usuarios de prueba (DataInitializer): `secretaria@pairazaman.com / secretaria123` y `tecnico@pairazaman.com / tecnico123`.

## Archivos
Nuevos: `model/{TipoOrden,EstadoOrden,DisponibilidadTecnico,Notificacion}`, `historial/{ContratoRepository,InstalacionRepository}`,
`repository/{TecnicoRepository,NotificacionRepository}`, `dto/{OrdenInstalacionDto,TecnicoFormDto,DiaAgenda}`,
`service/{InstalacionService,TecnicoService}` + `impl/`, `controller/{InstalacionController,TecnicoController}`,
plantillas `instalaciones/*` y `tecnicos/*`.

Modificados: `historial/Instalacion` (contrato, tipo, hora, observaciones, técnico), `model/Tecnico` (zona, disponibilidad),
`config/SecurityConfig`, `controller/DashboardController`, `templates/dashboard/tecnico.html`, `templates/layout/base.html`.
