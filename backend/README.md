# 🚀 FastBank – REST API (Backend)

[![Java 21](https://img.shields.io/badge/Java-21-007396?logo=java)](https://adoptium.net/) [![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5.x-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot) [![Maven 3.9](https://img.shields.io/badge/Maven-3.9.11-C71A36?logo=apache-maven)](https://maven.apache.org/) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791?logo=postgresql)](https://www.postgresql.org/)

FastBank es una API REST de banca simplificada que gestiona usuarios, cuentas y transacciones (depósitos, retiros y transferencias), con soporte de concurrencia segura mediante bloqueo pesimista. Incluye endpoints de prueba para cargas concurrentes y documentación OpenAPI/Swagger.

## ✨ Características

- Gestión de usuarios y cuentas bancarias
- Transacciones: depósito, retiro y transferencia entre cuentas
- Consistencia bajo concurrencia con JPA y bloqueo pesimista
- Paginación básica de usuarios y consultas por cuenta
- Observabilidad con Spring Boot Actuator
- Documentación automática con springdoc-openapi

## 🧱 Arquitectura (módulo backend)

```
com.josemiguelhyb.fastbank
├─ controller/   # Endpoints REST (Users, Accounts, Transactions)
├─ service/      # Reglas de negocio y transacciones @Transactional
├─ repository/   # Repositorios Spring Data JPA
├─ model/        # Entidades JPA
├─ dto/          # Requests/Responses
└─ mapper/       # Conversión entre entidades y DTOs
```

## 🛠️ Stack técnico

- Java 21 (LTS)
- Spring Boot 3.5.6, Spring Web, Spring Data JPA, Validation
- Base de datos: PostgreSQL (local por defecto)
- Build: Maven Wrapper (no necesitas Maven instalado)
- OpenAPI: springdoc-openapi-starter-webmvc-ui
- Actuator para métricas/health

## ✅ Requisitos

- JDK 21 instalado (verifica con `java -version`)
- PostgreSQL 14+ con un esquema accesible
- Puerto 8080 libre (o configura `server.port`)

## ⚙️ Configuración

Valores por defecto en `src/main/resources/application.properties`:

```
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/fastbank
spring.datasource.username=postgres
spring.datasource.password=***

spring.jpa.hibernate.ddl-auto=update
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

Recomendado para desarrollo: sobreescribir credenciales mediante variables de entorno o un `application-local.properties` ignorado por Git.

Ejemplos de variables de entorno (Windows PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/fastbank"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="<tu_password>"
```

> Importante: No publiques contraseñas reales en el repositorio.

## 🧪 Ejecutar en local

1) Arranca PostgreSQL y crea la base de datos si no existe:

```sql
CREATE DATABASE fastbank;
```

2) Desde la carpeta `backend`, levanta la aplicación:

```powershell
cd "c:\Users\miguel\Documents\proyectos_it\45 - FastBankUI\fastbankui\backend"
.\\\mvnw spring-boot:run
```

3) Salud y docs:

- Health: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## 🌐 Endpoints principales

Base URL: `http://localhost:8080`

### Usuarios (`/api/users`)

- GET `/api/users` — Listado
- GET `/api/users?size=10&page=0` — Paginado
- GET `/api/users/{id}` — Detalle
- POST `/api/users` — Crear
- PUT `/api/users/{id}` — Actualizar
- DELETE `/api/users/{id}` — Eliminar
- POST `/api/users/rehash` — Recalcula hash de contraseñas (BCrypt)

Ejemplo de creación:

```json
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "secreta"
}
```

### Cuentas (`/api/accounts`)

- POST `/api/accounts` — Crear cuenta para un usuario
- GET `/api/accounts/{id}` — Detalle de cuenta
- GET `/api/accounts` — Listado de cuentas
- PUT `/api/accounts/{id}` — Actualizar
- DELETE `/api/accounts/{id}` — Eliminar

### Transacciones (`/api/transactions`)

- POST `/api/transactions/deposit` — Depósito en cuenta
- POST `/api/transactions/withdraw` — Retiro de cuenta
- POST `/api/transactions/transfer` — Transferencia entre cuentas
- GET `/api/transactions` — Listado
- GET `/api/transactions/account/{accountId}` — Por cuenta

Endpoints de prueba de concurrencia:

- POST `/api/transactions/test/concurrent-deposits`
- POST `/api/transactions/test/concurrent-withdrawals`
- POST `/api/transactions/test/concurrent-transfers`

## 🔒 Concurrencia y consistencia

- Uso de bloqueo pesimista en operaciones críticas para evitar condiciones de carrera.
- Transacciones con `@Transactional` para asegurar atomicidad.
- Recomendado: aislar operaciones de escritura y revisar tiempos de espera en escenarios de alta contención.

## 📊 Observabilidad

- Actuator expone salud, métricas, info, beans y entorno: `management.endpoints.web.exposure.include=health,info,metrics,beans,env`
- Accesos comunes:
  - `/actuator/health`
  - `/actuator/metrics`

## 🧰 Desarrollo y tests

- Ejecutar tests:

```powershell
cd "c:\Users\miguel\Documents\proyectos_it\45 - FastBankUI\fastbankui\backend"; .\mvnw test
```

- Empaquetar JAR:

```powershell
cd "c:\Users\miguel\Documents\proyectos_it\45 - FastBankUI\fastbankui\backend"; .\mvnw -DskipTests=false clean package
```

- Ejecutar el JAR:

```powershell
java -jar .\target\fastbank-0.0.1-SNAPSHOT.jar
```

## 🐞 Troubleshooting

- Error al arrancar (`Connection refused`): verifica `spring.datasource.*` y que PostgreSQL esté activo.
- Puerto 8080 ocupado: cambia `server.port` o libera el puerto.
- Advertencia Mockito en Java 21 (agente): es un warning; si deseas, podemos añadir configuración para suprimirlo en futuras versiones del JDK.

## 📄 Licencia

Pendiente de definir.

---

¿Quieres que alinee también el README del proyecto raíz y del frontend con este estilo (setup, scripts, proxy, build y despliegue)? Puedo dejarlo todo homogéneo.

