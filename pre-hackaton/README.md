# CRUD de Personas (JavaFX + MySQL)

Aplicación de escritorio para registrar, listar, buscar, editar y eliminar personas.
La interfaz está hecha con JavaFX y los datos se guardan en un contenedor MySQL.

## 1. Crear / iniciar el contenedor MySQL

```bash
docker run -d --name tarea-poo -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=db_tarea -p 3308:3306 mysql:latest
```

Si el contenedor ya existe y está detenido:

```bash
docker start tarea-poo
```

## 2. Conexión en MySQL Workbench

| Campo    | Valor       |
|----------|-------------|
| Hostname | `127.0.0.1` |
| Port     | `3308`      |
| Username | `root`      |
| Password | `123`       |
| Database | `db_tarea`  |

La base `db_tarea` la crea el contenedor automáticamente.
La tabla `personas` la crea la aplicación al iniciar (también está en `schema.sql`
por si prefieres crearla manualmente desde Workbench).

## 3. Ejecutar la aplicación

Desde la carpeta del proyecto:

```bash
./mvnw clean javafx:run
```

En Windows (PowerShell o CMD):

```bash
mvnw.cmd clean javafx:run
```

## 4. Funcionalidades

- **Guardar**: registra una persona nueva. Valida DNI (8 dígitos), nombres y apellidos obligatorios, correo y teléfono.
- **Actualizar**: selecciona una fila de la tabla, modifica los campos y pulsa Actualizar.
- **Eliminar**: selecciona una fila y confirma la eliminación.
- **Limpiar**: vacía el formulario.
- **Buscar**: filtra en tiempo real por DNI, nombres, apellidos, teléfono, correo o dirección.

## 5. Estructura del código

```
src/main/java/vallegrande/edu/pe/prehackaton/
├── model/
│   ├── Persona.java       # Entidad
│   ├── Conexion.java      # Conexión JDBC y creación de la tabla
│   └── PersonaDAO.java    # Operaciones CRUD (INSERT, SELECT, UPDATE, DELETE)
├── controller/
│   └── MainController.java # Lógica de la interfaz y validaciones
└── view/
    └── MainView.java       # Punto de entrada JavaFX

src/main/resources/vallegrande/edu/pe/prehackaton/view/
├── main-view.fxml           # Interfaz
└── styles.css               # Estilos
```

## 6. Cambiar credenciales sin recompilar

La clase `Conexion` acepta estas variables de entorno:

| Variable      | Valor por defecto |
|---------------|-------------------|
| `DB_HOST`     | `localhost`       |
| `DB_PORT`     | `3308`            |
| `DB_NAME`     | `db_tarea`        |
| `DB_USER`     | `root`            |
| `DB_PASSWORD` | `123`             |

## 7. Solución de problemas

- Si aparece un error de conexión, verifica que Docker esté abierto y que el
  contenedor esté corriendo con `docker ps`.
- Comprueba la conexión con:
  `docker exec -it tarea-poo mysql -uroot -p123 -e "SHOW DATABASES;"`
- El puerto debe ser `3308` (no `3306`), porque el contenedor se publica en ese puerto.
