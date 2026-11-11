package vallegrande.edu.pe.prehackaton.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Administra la conexión JDBC con el contenedor MySQL.
 *
 * Contenedor esperado:
 * docker run -d --name tarea-poo \
 *   -e MYSQL_ROOT_PASSWORD=123 \
 *   -e MYSQL_DATABASE=db_tarea \
 *   -p 3308:3306 mysql:latest
 *
 * Puedes cambiar estos valores con variables de entorno sin recompilar:
 * DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD.
 */
public final class Conexion {

    private static final String HOST = valor("DB_HOST", "localhost");
    private static final String PORT = valor("DB_PORT", "3308");
    private static final String DATABASE = valor("DB_NAME", "db_tarea");
    private static final String USER = valor("DB_USER", "root");
    private static final String PASSWORD = valor("DB_PASSWORD", "123");

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Lima"
            + "&characterEncoding=UTF-8"
            + "&useUnicode=true"
            + "&connectTimeout=5000"
            + "&socketTimeout=10000";

    private Conexion() {
    }

    private static String valor(String variable, String porDefecto) {
        String valor = System.getenv(variable);
        return (valor == null || valor.trim().isEmpty()) ? porDefecto : valor.trim();
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsuario() {
        return USER;
    }

    /**
     * Crea la tabla personas si todavía no existe. Se ejecuta al iniciar la aplicación,
     * así no hay que crear la tabla manualmente en MySQL Workbench.
     */
    public static void inicializarBaseDatos() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS personas ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "dni VARCHAR(8) NOT NULL UNIQUE,"
                + "nombres VARCHAR(100) NOT NULL,"
                + "apellidos VARCHAR(100) NOT NULL,"
                + "telefono VARCHAR(15) NULL,"
                + "correo VARCHAR(120) NULL,"
                + "direccion VARCHAR(200) NULL,"
                + "fecha_nacimiento DATE NULL,"
                + "creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Connection cn = getConexion();
             Statement st = cn.createStatement()) {
            st.executeUpdate(sql);
        }
    }
}
