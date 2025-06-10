# Proyecto Docker Compose

Este proyecto contiene una configuración de `docker-compose` para levantar un entorno con PostgreSQL, un backend y PgAdmin.

## Prerrequisitos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

- **Docker**: [Descargar e instalar Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Verificar instalación](https://docs.docker.com/compose/install/)

Para verificar que Docker y Docker Compose están instalados, ejecuta en la terminal:

```sh
docker --version
```

```sh
docker-compose --version
```

Si no están instalados, sigue las instrucciones en los enlaces proporcionados.

## Instalación y configuración

### 1. Clonar el repositorio

```sh
git clone git@github.com:JorgeParra10/nequi.git
cd nequi
```

### 2. Configurar variables de entorno

Debes crear los archivos `.env_db` y `.env_back` en la raíz del proyecto. Asegúrate de configurar correctamente las variables necesarias para la base de datos y el backend.

Ejemplo de `.env_db`:

```env
POSTGRES_USER=usuario
POSTGRES_PASSWORD=contraseña
POSTGRES_DB=nombre_de_la_base
```

Ejemplo de `.env_back`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://db_service:5432/nombre_de_la_base
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=contraseña
SPRING_JPA_HIBERNATE_DDL_AUTO=update
springdoc.api-docs.enabled=true
```

### 3. Construir y levantar los servicios

Ejecuta el siguiente comando para construir y levantar los contenedores:

```sh
docker-compose up --build -d
```

### 4. Verificar que los contenedores están corriendo

```sh
docker ps
```

Deberías ver los servicios `db_service`, `backend_service` y `pgadmin_service` ejecutándose.

## Acceso a los servicios

- **Base de datos (PostgreSQL):** Disponible en `localhost:6000`.
- **Backend:** Disponible en `http://localhost:8080`.
- **Docs:** Disponible en `http://localhost:8080/docs/swagger-ui/swagger-ui/index.html`.
- **PgAdmin:** Accesible en `http://localhost:5050`.
  - Usuario: `admin@example.com`
  - Contraseña: `admin`

## Comandos útiles

### Detener los contenedores

```sh
docker-compose down
```

### Ver logs de un servicio específico

```sh
docker logs -f backend_service
```

### Reiniciar un servicio

```sh
docker-compose restart backend
```

### Eliminar volúmenes y contenedores

```sh
docker-compose down -v
```

## Fin