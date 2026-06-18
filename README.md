# Recortador de enlaces (Acortador de URLs)

API REST para acortar URLs, redirigir al destino original y consultar estadísticas de clics, con una interfaz web sencilla y documentación interactiva de la API.

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## Captura

<!-- Sustituye esta línea por tu captura: arrastra la imagen al editar el README en GitHub, o súbela a una carpeta /docs y enlázala aquí -->
![Interfaz del recortador de enlaces](docs/captura.png)

## Características

- Acortar cualquier URL larga en un enlace corto único.
- Redirección automática del enlace corto al destino original.
- Conteo de clics por cada enlace.
- Alias personalizado opcional (elige tu propio código en lugar de uno generado).
- Generación de un código QR del enlace corto, descargable.
- Interfaz web propia para usar el acortador desde el navegador.
- Validación de las URLs introducidas.
- Documentación interactiva de la API con Swagger / OpenAPI.

## Tecnologías

- **Java 17**
- **Spring Boot** (Spring Web, Spring Data JPA)
- **PostgreSQL** como base de datos
- **Maven** para la gestión de dependencias
- **Bean Validation** para validar las entradas
- **springdoc-openapi (Swagger UI)** para la documentación

## Requisitos previos

- JDK 17 o superior
- Maven 3.9 o superior
- PostgreSQL en ejecución

## Cómo ejecutarlo en local

1. Clona el repositorio:

   ```bash
   git clone https://github.com/empty-cyber/Recortador-URL.git
   cd Recortador-URL
   ```

2. Crea una base de datos PostgreSQL para el proyecto:

   ```sql
   CREATE DATABASE recortador;
   ```

3. Configura tus credenciales en `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/recortador
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_CONTRASEÑA
   spring.jpa.hibernate.ddl-auto=update
   ```

4. Arranca la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Abre en el navegador:

   - Interfaz web: <http://localhost:8080>
   - Documentación de la API (Swagger): <http://localhost:8080/swagger-ui/index.html>

## Documentación de la API

Con la aplicación en marcha, todos los endpoints (acortar, redirigir, estadísticas) están documentados y se pueden probar directamente desde Swagger UI:

<http://localhost:8080/swagger-ui/index.html>

A grandes rasgos, el flujo es:

1. Envías una URL larga al endpoint de acortado y recibes un enlace corto.
2. Al visitar ese enlace corto, la aplicación redirige al destino original e incrementa el contador de clics.

## Estructura del proyecto

```
Recortador-URL/
├── src/
│   └── main/
│       ├── java/          # Código fuente (controladores, servicios, entidades)
│       └── resources/
│           ├── static/    # Interfaz web (HTML, CSS, JS)
│           └── application.properties
├── pom.xml
└── README.md
```

## Autora

**Sara Mula**

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo `LICENSE` para más detalles.
