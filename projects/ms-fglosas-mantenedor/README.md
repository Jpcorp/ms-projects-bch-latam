# ms-fglosas-mantenedor

Microservicio encargado de gestionar glosas estáticas del ecosistema BCH.  
Permite crear, actualizar, aprobar y auditar glosas. Es consumido por **bff-mantenedor-glosas**.

---

## 1. Ownership

| Rol              | Responsable                       |
|------------------|-----------------------------------|
| Arquitecto        | José Luis Cardoza Villagra        |
| Jefe de Proyecto  | Leoslay Melendez Romero           |
| Product Owner     | (Indicar nombre)                  |
| Software Factory  | TCS – Juan Pablo Guinart          |

---

## 2. Descripción General

Este microservicio expone endpoints REST para administrar glosas estáticas.  
Incluye servicios de:

- Gestión CRUD de glosas
- Bitácora de operaciones
- Flujo de aprobaciones
- Auditoría y trazabilidad

Base utilizada:
- **Tabla:** `GLOSAS_ESTATICAS`

---

## 3. Arquitectura Técnica

- **Framework:** Spring Boot 3
- **Java:** 21
- **Persistencia:** Spring Data JPA + Hibernate
- **Seguridad:** Vault OS para secretos, manejo de credenciales y cifrado
- **Despliegue:** Kubernetes (OKE)
- **Logs:** lib-ms-log-estandar BCH
- **Config:** ConfigMaps + Secrets

---

## 4. Requisitos Previos

- Maven 3.8+
- Java 21
- Acceso a repositorios BCH
- Variables de entorno configuradas
- Secrets declarados en `secrets.properties`

---

## 5. Endpoints

### Glosas Estáticas

| Verbo | URI                         | Acción    |
|-------|------------------------------|-----------|
| GET   | /glosses/static/{id}         | findById  |
| GET   | /glosses/static/             | findAll   |
| POST  | /glosses/static/{entity}     | create    |
| PUT   | /glosses/static/{id}         | update    |
| DELETE| /glosses/static/{id}         | delete    |
| POST  | /glosses/static/{id}/approve | approve   |

### Bitácora

| Verbo | URI                     | Acción    |
|-------|--------------------------|-----------|
| GET   | /glosses/logs/{id}       | findById  |
| GET   | /glosses/logs/           | findAll   |
| POST  | /glosses/logs/{entity}   | create    |
| PUT   | /glosses/logs/{id}       | update    |
| DELETE| /glosses/logs/{id}       | delete    |

### Aprobaciones

| Verbo | URI                          | Acción    |
|-------|-------------------------------|-----------|
| GET   | /glosses/approvals/{id}       | findById  |
| GET   | /glosses/approvals/           | findAll   |
| POST  | /glosses/approvals/{entity}   | create    |
| PUT   | /glosses/approvals/{id}       | update    |
| DELETE| /glosses/approvals/{id}       | delete    |

---

## 6. Variables de Entorno

Archivo: **properties/bbdd.properties**

- `DS_CONNECTION_USERNAME`
- `DS_CONNECTION_PASSWORD`

---

## 7. Secrets (Vault OS)

Archivo: **src/main/resources/secrets.properties**

```sh
secrets-env-ids=bbdd-ellu,redis
secrets-volumen-ids=wallet-bbdd01,wallet-02