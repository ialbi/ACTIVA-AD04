# ACTIVA — Gestión de actividades y reservas

Proyecto de la asignatura **Acceso a Datos (DAM)**, correspondiente a la tarea evaluativa AD04 — Mapeo objeto-relacional.

Aplicación Java de consola que utiliza Hibernate ORM y MySQL para gestionar monitores, actividades, clientes y reservas.

## Tecnologías utilizadas

- Java 21
- Apache Maven
- Hibernate ORM 7.4.12.Final
- MySQL 8.0
- MySQL Connector/J
- Anotaciones JPA
- HQL para las consultas

## Funcionalidades

La aplicación dispone de un menú con ocho opciones:

1. Crear monitor.
2. Crear actividad asociada a un monitor.
3. Buscar actividades disponibles por localidad, fecha y plazas.
4. Crear cliente y evitar correos duplicados.
5. Crear reserva y descontar plazas.
6. Cancelar reserva y recuperar plazas.
7. Listar todas las actividades.
8. Salir.

Las operaciones de reserva y cancelación se realizan mediante transacciones de Hibernate, de manera que los cambios relacionados se confirman o deshacen conjuntamente.

## Estructura del proyecto

```text
src/main/
├── java/eus/birt/dam/ad04/
│   ├── config/       # Configuración de Hibernate
│   ├── domain/       # Entidades JPA
│   ├── service/      # Operaciones y lógica de negocio
│   └── main/         # Menú de consola
└── resources/
    └── hibernate.cfg.xml
```

## Preparación de la base de datos

Es necesario tener instalado MySQL y preparar previamente la base de datos mediante el archivo `dbactiva.sql` proporcionado para la tarea.

Para importar el archivo sobre una base de datos vacía:

```bash
sudo mysql < dbactiva.sql
```

La base de datos contiene las tablas `monitor`, `actividad`, `cliente` y `reserva`.

Hibernate utiliza la configuración `hibernate.hbm2ddl.auto=validate`, por lo que comprueba el esquema existente sin crear ni modificar las tablas.

## Configuración de MySQL

La aplicación utiliza un usuario MySQL llamado `activa`, con permisos `SELECT`, `INSERT`, `UPDATE` y `DELETE` sobre `dbactiva`.

Ejemplo de creación del usuario y asignación de permisos, desde una sesión administrativa de MySQL:

```sql
CREATE USER 'activa'@'localhost'
IDENTIFIED BY 'CONTRASEÑA_ELEGIDA';

GRANT SELECT, INSERT, UPDATE, DELETE
ON dbactiva.*
TO 'activa'@'localhost';
```

La contraseña real no se almacena en el código fuente. La aplicación la obtiene de la variable de entorno `ACTIVA_DB_PASSWORD`.

## Compilación y ejecución

Desde la raíz del proyecto:

```bash
mvn clean compile
```

Copiar las dependencias necesarias:

```bash
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
```

Dar permiso de ejecución al lanzador:

```bash
chmod +x activa.sh
```

Ejecutar:

```bash
./activa.sh
```

El script solicita la contraseña de MySQL si no está definida en el entorno y ejecuta la aplicación con Java.

También es posible ejecutarla directamente:

```bash
java -cp "target/classes:target/dependency/*" eus.birt.dam.ad04.main.Main
```

En ese caso, la variable `ACTIVA_DB_PASSWORD` debe estar configurada previamente.

## Comprobaciones realizadas

Se han probado las principales funcionalidades de la aplicación y se han contrastado los resultados con consultas directas a MySQL.

Entre las comprobaciones realizadas están la creación de entidades, la búsqueda mediante HQL parametrizado, la gestión de reservas y cancelaciones, el rechazo de plazas insuficientes, los correos duplicados y la prevención de dobles cancelaciones.

## Consideraciones

El proyecto está desarrollado como una aplicación de consola monousuario para fines educativos.

No incluye contraseñas reales ni las dependencias descargadas por Maven en el repositorio.
