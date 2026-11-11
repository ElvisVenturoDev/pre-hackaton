package vallegrande.edu.pe.prehackaton.model;

import java.time.LocalDate;

/**
 * Representa a una persona registrada en la base de datos.
 */
public class Persona {

    private Integer id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String direccion;
    private LocalDate fechaNacimiento;

    public Persona() {
    }

    public Persona(Integer id, String dni, String nombres, String apellidos,
                   String telefono, String correo, String direccion,
                   LocalDate fechaNacimiento) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Nombre completo, útil para búsquedas y mensajes.
     */
    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                '}';
    }
}
