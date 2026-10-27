# GestionAcademicaORM

Aplicación de escritorio didáctica para registrar y consultar estudiantes con Java 21, JavaFX, Hibernate ORM y PostgreSQL.

## Requisitos

- JDK 21.
- Maven 3.9 o superior, o el Maven Wrapper incluido.
- PostgreSQL local y pgAdmin opcional.

Las versiones configuradas son JavaFX 21.0.6, Hibernate ORM 6.6.11.Final y pgJDBC 42.7.4. Estas versiones están publicadas en Maven Central.

## Base de datos y configuración

Crear la base de datos conectado a PostgreSQL como usuario administrador:

```sql
CREATE DATABASE gestionacademica;
```

No crear la tabla manualmente. Hibernate la genera o actualiza mediante `hibernate.hbm2ddl.auto=update`.

Copiar `src/main/resources/hibernate.properties.example` como `src/main/resources/hibernate.properties` y completar el usuario y la clave local de PostgreSQL. El archivo real está ignorado por Git para evitar publicar credenciales.

Para una conexión remota se recomienda añadir `sslmode=verify-full` a la URL y configurar los certificados correspondientes. El ejemplo está preparado para laboratorio local.

## Ejecución

```bash
mvn clean package
mvn javafx:run
```

También se puede usar `mvnw.cmd` en Windows.

Después de registrar estudiantes, la tabla se puede verificar con:

```sql
SELECT * FROM estudiantes;
```

## Estructura

```text
src/main/java/edu/gestionacademica/
  MainApp.java
  model/Estudiante.java
  repository/EstudianteRepository.java
  controller/EstudianteController.java
  config/HibernateUtil.java
src/main/resources/
  hibernate.properties.example
  edu/gestionacademica/estudiante-view.fxml
```

La vista llama al controlador, el controlador delega en `EstudianteRepository` y el repositorio administra sesiones, transacciones y consultas Hibernate. Las operaciones de base de datos se ejecutan con `Task` para mantener libre el hilo de JavaFX.

## Reflexión

Una ventaja del ORM es que permite trabajar con objetos Java y dejar que Hibernate gestione el mapeo hacia tablas y restricciones. Una limitación es que hay que conocer el ciclo de vida de las sesiones y vigilar el SQL generado. Una mejora posible sería añadir paginación, validaciones más completas y pruebas automatizadas con una base de datos de prueba.

Frente a JDBC, Hibernate reduce el código repetitivo de conexiones, mapeo y transacciones, mientras JDBC ofrece un control más directo del SQL y puede ser preferible para consultas muy específicas o de alto rendimiento.
