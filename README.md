# Tienda de Libros — Fullstack

Tienda de libros digitales. Los usuarios exploran el catálogo, compran libros con MercadoPago, los guardan en su biblioteca y dejan reseñas. Los administradores gestionan el catálogo, los autores y los usuarios.

Trabajo Práctico Final de **Programación IV** (UTN). El backend es la API desarrollada en Programación III. El frontend en Angular se construyó en esta materia y consume esa API.

## Integrantes

| Integrante | GitHub |
|---|---|
| Franco Leonel Gaitán | [@franleo10](https://github.com/franleo10) |
| Gian Franco Chiesa | [@Gian-c](https://github.com/Gian-c) |

## Tecnologías

**Frontend**
- Angular 20, con componentes standalone y signals
- TypeScript 5.9 y RxJS 7.8
- Formularios reactivos con validaciones propias
- Jasmine y Karma para las pruebas unitarias

**Backend**
- Java 21 y Spring Boot 3.4
- Spring Security con JWT, con roles y permisos
- Spring Data JPA con MySQL 8
- SDK de MercadoPago para los pagos
- Open Library para vincular cada libro con su versión digital
- JUnit 5 y Mockito para las pruebas

## Funcionalidades

**Visitante (sin sesión)**
- Registro e inicio de sesión
- Catálogo de libros:
  - búsqueda por título o autor;
  - filtros por género y rango de precio;
  - ordenamiento y paginación.
- Detalle de libro con sinopsis, portada, reseñas y puntuación promedio
- Listado y detalle de autores

**Usuario**
- Carrito de compras y pago con MercadoPago
- Historial de compras con el detalle de cada factura
- Mi biblioteca, con los libros comprados
- Reseñas con puntuación de 1 a 5 estrellas
- Perfil: ver, editar y cambiar contraseña

**Administrador**
- CRUD de libros: alta, edición con portada por URL y baja lógica
- CRUD de autores: alta, edición y baja
- Gestión de usuarios:
  - listado con búsqueda y filtros;
  - cambio de rol;
  - baja y reactivación.

## Roles y seguridad

Hay dos roles, **USUARIO** y **ADMINISTRADOR**. Cada rol tiene un conjunto de **permisos** que asigna el backend, por ejemplo `AGREGAR_LIBRO` o `ELIMINAR_USUARIOS`.

- **Backend:** emite un **JWT** al iniciar sesión y valida cada endpoint con `@PreAuthorize`.
- **Interceptor HTTP:** agrega el token a cada petición que hace el frontend.
- **Guard `permisoGuard`:** protege las rutas de administración.
  - Si no hay sesión, redirige al login.
  - Si hay sesión, pide al backend los permisos del usuario. Deja pasar solo si el usuario tiene el permiso que la ruta declara en `data.permiso`.
- **Interfaz:** muestra u oculta botones y menús según los permisos del usuario.

## Estructura del repositorio

```
tienda-libros-fullstack/
├── backend/            API REST en Spring Boot
│   └── src/main/java/org/utn/tpfinalprogramacion3/
│       ├── controller/     endpoints REST
│       ├── services/       lógica de negocio
│       ├── repository/     acceso a datos (JPA)
│       ├── entities/       entidades JPA
│       ├── dtos/ mapper/   objetos de transferencia y conversión
│       ├── security/       JWT, roles y permisos
│       └── openLibrary/    integración con Open Library
├── frontend/           aplicación Angular
│   └── src/app/
│       ├── core/           guards, interceptor y servicios de sesión y permisos
│       ├── features/       pantallas por funcionalidad (catálogo, carrito, perfil, admin…)
│       ├── services/       servicios HTTP contra la API
│       ├── models/         interfaces de datos
│       ├── shared/         componentes reutilizables (portada, estrellas, confirmación…)
│       └── layout/         navbar y footer
└── postman/            colección para probar la API
```

## Cómo ejecutarlo

### Requisitos

- Java 21
- MySQL 8
- Node.js 20 o superior y npm
- Angular CLI 20: `npm install -g @angular/cli@20`

### 1. Base de datos

Crear una base vacía. Las tablas se generan solas al iniciar el backend (`ddl-auto=update`).

```sql
CREATE DATABASE tienda_libros;
```

### 2. Backend

Crear el archivo `backend/.env`. No se sube al repositorio.

```env
DB_URL=jdbc:mysql://localhost:3306/tienda_libros
DB_USERNAME=root
DB_PASSWORD=tu_contraseña
JWT_SECRET=una_clave_secreta_larga_en_base64
JWT_EXPIRATION=86400000
```

Levantar la API desde la carpeta `backend`:

```bash
cd backend
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`. En el primer arranque se crean los roles USUARIO y ADMINISTRADOR con sus permisos.

### 3. Frontend

```bash
cd frontend
npm install
ng serve
```

La aplicación queda en `http://localhost:4200`.

### 4. Primer administrador

Todo usuario que se registra desde la aplicación queda con rol USUARIO. Para tener un administrador:

1. Registrar el usuario desde la aplicación.
2. Cambiarle el rol en la base de datos:

```sql
UPDATE credentials_roles cr
JOIN credencial_entity c ON c.id = cr.credential_id
SET cr.role_id = (SELECT id FROM roles WHERE role_name = 'ADMINISTRADOR')
WHERE c.email = 'admin@ejemplo.com';
```

Desde ahí, ese administrador puede cambiar el rol de otros usuarios en **Admin → Usuarios**.

## Pruebas

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
ng test
```

## Metodología

El trabajo se organizó con Scrum en Jira (proyecto `SCRUM`):

- cada tarea tiene una rama propia con la clave del ticket, por ejemplo `franco/SCRUM-24-filtros-orden-libros`;
- cada rama se integra a `master` con un pull request.

La planificación, las estimaciones, los riesgos y el seguimiento del proyecto están en el dossier de la materia Gestión de Desarrollo de Software.
