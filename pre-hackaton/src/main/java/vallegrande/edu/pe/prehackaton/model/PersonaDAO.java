package vallegrande.edu.pe.prehackaton.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD sobre la tabla personas.
 */
public class PersonaDAO {

    /**
     * Devuelve todas las personas ordenadas por apellidos y nombres.
     */
    public List<Persona> listar() throws SQLException {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT id, dni, nombres, apellidos, telefono, correo, direccion, fecha_nacimiento "
                + "FROM personas ORDER BY apellidos, nombres";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Inserta una persona y asigna el id generado al objeto.
     */
    public void insertar(Persona persona) throws SQLException {
        String sql = "INSERT INTO personas (dni, nombres, apellidos, telefono, correo, direccion, fecha_nacimiento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, persona);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    persona.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza los datos de una persona existente.
     */
    public void actualizar(Persona persona) throws SQLException {
        String sql = "UPDATE personas SET dni = ?, nombres = ?, apellidos = ?, telefono = ?, "
                + "correo = ?, direccion = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            asignarParametros(ps, persona);
            ps.setInt(8, persona.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una persona por su id.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM personas WHERE id = ?";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Verifica si un DNI ya está registrado. Se puede excluir un id
     * para permitir que una persona conserve su propio DNI al editarse.
     */
    public boolean existeDni(String dni, Integer idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM personas WHERE dni = ?"
                + (idExcluir == null ? "" : " AND id <> ?");

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void asignarParametros(PreparedStatement ps, Persona persona) throws SQLException {
        ps.setString(1, persona.getDni());
        ps.setString(2, persona.getNombres());
        ps.setString(3, persona.getApellidos());
        ps.setString(4, vacioANull(persona.getTelefono()));
        ps.setString(5, vacioANull(persona.getCorreo()));
        ps.setString(6, vacioANull(persona.getDireccion()));

        if (persona.getFechaNacimiento() == null) {
            ps.setNull(7, java.sql.Types.DATE);
        } else {
            ps.setDate(7, Date.valueOf(persona.getFechaNacimiento()));
        }
    }

    private Persona mapear(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha_nacimiento");
        return new Persona(
                rs.getInt("id"),
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("direccion"),
                fecha == null ? null : fecha.toLocalDate()
        );
    }

    private String vacioANull(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }
}
