# Pairazaman — Gestión de Clientes (RF-01 / ECU-01)

Módulo de clientes con backend **Spring Boot + PostgreSQL** y frontend **React + Vite**.

## Requisitos

- Java 17+
- PostgreSQL 14+
- Node.js 18+

## 1. Base de datos

Crea la base de datos (Hibernate crea las tablas con `ddl-auto=update`):

```sql
CREATE DATABASE pairazaman_db;
```

Credenciales por defecto en `src/main/resources/application.properties`:

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/pairazaman_db` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `postgres` |

Puedes sobreescribirlas con variables de entorno.

## 2. Backend

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`.

## 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

La aplicación queda en `http://localhost:5173` y Vite redirige `/api` al backend.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/clientes` | Registra un cliente (`ClienteRegistroDTO` con validaciones) |
| `GET` | `/api/clientes?nombre=&estado=` | Lista con filtro dinámico por nombre y estado (`ACTIVO`/`INACTIVO`) |
| `GET` | `/api/clientes/{id}` | Datos del cliente para edición |
| `PUT` | `/api/clientes/{id}` | Actualiza el cliente (necesario para persistir la edición) |
| `PATCH` | `/api/clientes/{id}/estado` | Cambia únicamente el estado |
| `GET` | `/api/clientes/{id}/historial` | Contratos, pagos, reclamos e instalaciones |

### Ejemplo de registro

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "dniRuc": "0102030405",
    "nombresRazonSocial": "María Pairazaman",
    "direccion": "Av. Principal 123",
    "telefono": "0999999999",
    "correo": "maria@example.com",
    "tipo": "HOGAR"
  }'
```

### Ejemplo de cambio de estado

```bash
curl -X PATCH http://localhost:8080/api/clientes/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "INACTIVO"}'
```

## Estructura del backend

```
src/main/java/com/repo/pairazaman/
├── cliente/
│   ├── Cliente.java               # entidad con relaciones LAZY
│   ├── EstadoCliente.java         # ACTIVO / INACTIVO
│   ├── TipoCliente.java           # HOGAR / COMERCIO / INSTITUCION
│   ├── ClienteRepository.java     # JpaRepository + filtros derivados
│   ├── ClienteService.java        # lógica y transacciones
│   ├── ClienteController.java     # API REST
│   ├── dto/                       # request/response e historial
│   └── exception/                 # errores 400/404/409
├── historial/                     # Contrato, Pago, Reclamo, Instalacion
└── config/WebConfig.java          # CORS para Vite
```

## Notas de diseño

- Las colecciones `@OneToMany` usan `FetchType.LAZY` y `@JsonIgnore`.
- La API nunca serializa entidades: siempre devuelve DTOs, evitando recursión infinita y `LazyInitializationException`.
- El historial se construye dentro de una transacción `readOnly`, inicializando las colecciones antes de mapear a DTOs.
- Se añadió `PUT /api/clientes/{id}` porque el flujo de edición del frontend necesita persistir los cambios; el resto de endpoints respeta la especificación.
