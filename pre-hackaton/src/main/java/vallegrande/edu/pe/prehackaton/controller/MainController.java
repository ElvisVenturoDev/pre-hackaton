package vallegrande.edu.pe.prehackaton.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import vallegrande.edu.pe.prehackaton.model.Conexion;
import vallegrande.edu.pe.prehackaton.model.Persona;
import vallegrande.edu.pe.prehackaton.model.PersonaDAO;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controlador de la vista principal: conecta la interfaz con PersonaDAO.
 */
public class MainController implements Initializable {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<Persona> tblPersonas;
    @FXML
    private TableColumn<Persona, Integer> colId;
    @FXML
    private TableColumn<Persona, String> colDni;
    @FXML
    private TableColumn<Persona, String> colNombres;
    @FXML
    private TableColumn<Persona, String> colApellidos;
    @FXML
    private TableColumn<Persona, String> colTelefono;
    @FXML
    private TableColumn<Persona, String> colCorreo;
    @FXML
    private TableColumn<Persona, String> colDireccion;
    @FXML
    private TableColumn<Persona, LocalDate> colFechaNacimiento;

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtDireccion;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;

    @FXML
    private Label lblEstado;

    private final PersonaDAO dao = new PersonaDAO();
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private FilteredList<Persona> personasFiltradas;
    private Persona seleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarEventos();
        conectarYcargar();
        limpiarFormulario();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));

        colFechaNacimiento.setCellFactory(columna -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setText(vacio || fecha == null ? "" : FECHA.format(fecha));
            }
        });

        personasFiltradas = new FilteredList<>(personas, p -> true);
        tblPersonas.setItems(personasFiltradas);

        tblPersonas.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> {
                    seleccionada = actual;
                    if (actual != null) {
                        cargarFormulario(actual);
                        btnActualizar.setDisable(false);
                        btnEliminar.setDisable(false);
                    } else {
                        btnActualizar.setDisable(true);
                        btnEliminar.setDisable(true);
                    }
                });
    }

    private void configurarEventos() {
        soloNumeros(txtDni, 8);
        soloNumeros(txtTelefono, 9);

        txtBuscar.textProperty().addListener((obs, anterior, texto) -> filtrar(texto));

        btnGuardar.setOnAction(e -> guardar());
        btnActualizar.setOnAction(e -> actualizar());
        btnEliminar.setOnAction(e -> eliminar());
        btnLimpiar.setOnAction(e -> {
            tblPersonas.getSelectionModel().clearSelection();
            limpiarFormulario();
            txtDni.requestFocus();
            setEstado("Formulario limpio. Listo para registrar una nueva persona.");
        });
    }

    private void conectarYcargar() {
        try {
            Conexion.inicializarBaseDatos();
            cargarPersonas();
            setEstado("Conectado a " + Conexion.getUrl());
        } catch (SQLException e) {
            setEstado("No se pudo conectar a MySQL: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se pudo conectar a la base de datos.\n\n"
                            + "Verifica que el contenedor esté corriendo:\n"
                            + "docker start tarea-poo\n\n"
                            + "Detalle: " + e.getMessage());
        }
    }

    private void cargarPersonas() throws SQLException {
        personas.setAll(dao.listar());
        setEstado("Registros cargados: " + personas.size());
    }

    private void guardar() {
        String error = validarFormulario();
        if (error != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", error);
            return;
        }

        try {
            if (dao.existeDni(txtDni.getText().trim(), null)) {
                mostrarAlerta(Alert.AlertType.WARNING, "DNI duplicado",
                        "Ya existe una persona registrada con el DNI " + txtDni.getText().trim() + ".");
                return;
            }

            Persona persona = leerFormulario();
            dao.insertar(persona);
            cargarPersonas();
            limpiarFormulario();
            setEstado("Persona registrada correctamente (ID " + persona.getId() + ").");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo registrar la persona.\n\nDetalle: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona una persona de la tabla para editarla.");
            return;
        }

        String error = validarFormulario();
        if (error != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", error);
            return;
        }

        try {
            if (dao.existeDni(txtDni.getText().trim(), seleccionada.getId())) {
                mostrarAlerta(Alert.AlertType.WARNING, "DNI duplicado",
                        "Otra persona ya tiene el DNI " + txtDni.getText().trim() + ".");
                return;
            }

            Persona persona = leerFormulario();
            persona.setId(seleccionada.getId());
            dao.actualizar(persona);
            cargarPersonas();
            limpiarFormulario();
            setEstado("Persona actualizada correctamente.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar",
                    "No se pudo actualizar la persona.\n\nDetalle: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona una persona de la tabla para eliminarla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar a " + seleccionada.getNombreCompleto());
        confirmacion.setContentText("Esta acción no se puede deshacer. ¿Deseas continuar?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (!respuesta.isPresent() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            dao.eliminar(seleccionada.getId());
            cargarPersonas();
            limpiarFormulario();
            setEstado("Persona eliminada correctamente.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar",
                    "No se pudo eliminar la persona.\n\nDetalle: " + e.getMessage());
        }
    }

    private String validarFormulario() {
        if (txtDni.getText().trim().isEmpty()) {
            return "El DNI es obligatorio.";
        }
        if (!txtDni.getText().trim().matches("\\d{8}")) {
            return "El DNI debe tener exactamente 8 dígitos.";
        }
        if (txtNombres.getText().trim().isEmpty()) {
            return "Los nombres son obligatorios.";
        }
        if (txtApellidos.getText().trim().isEmpty()) {
            return "Los apellidos son obligatorios.";
        }
        if (!txtTelefono.getText().trim().isEmpty()
                && !txtTelefono.getText().trim().matches("\\d{6,9}")) {
            return "El teléfono debe tener entre 6 y 9 dígitos.";
        }
        if (!txtCorreo.getText().trim().isEmpty()
                && !CORREO.matcher(txtCorreo.getText().trim()).matches()) {
            return "El correo electrónico no tiene un formato válido.";
        }
        if (dpFechaNacimiento.getValue() != null
                && dpFechaNacimiento.getValue().isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser futura.";
        }
        return null;
    }

    private Persona leerFormulario() {
        Persona p = new Persona();
        p.setDni(txtDni.getText().trim());
        p.setNombres(txtNombres.getText().trim());
        p.setApellidos(txtApellidos.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setCorreo(txtCorreo.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());
        p.setFechaNacimiento(dpFechaNacimiento.getValue());
        return p;
    }

    private void cargarFormulario(Persona p) {
        txtDni.setText(p.getDni() == null ? "" : p.getDni());
        txtNombres.setText(p.getNombres() == null ? "" : p.getNombres());
        txtApellidos.setText(p.getApellidos() == null ? "" : p.getApellidos());
        txtTelefono.setText(p.getTelefono() == null ? "" : p.getTelefono());
        txtCorreo.setText(p.getCorreo() == null ? "" : p.getCorreo());
        txtDireccion.setText(p.getDireccion() == null ? "" : p.getDireccion());
        dpFechaNacimiento.setValue(p.getFechaNacimiento());
        setEstado("Editando a " + p.getNombreCompleto() + ". Puedes modificar y pulsar Actualizar.");
    }

    private void limpiarFormulario() {
        seleccionada = null;
        txtDni.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        dpFechaNacimiento.setValue(null);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private void filtrar(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        personasFiltradas.setPredicate(p -> {
            if (filtro.isEmpty()) {
                return true;
            }
            return contiene(p.getDni(), filtro)
                    || contiene(p.getNombres(), filtro)
                    || contiene(p.getApellidos(), filtro)
                    || contiene(p.getTelefono(), filtro)
                    || contiene(p.getCorreo(), filtro)
                    || contiene(p.getDireccion(), filtro);
        });
    }

    private boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase().contains(filtro);
    }

    private void soloNumeros(TextField campo, int maxLongitud) {
        campo.setTextFormatter(new javafx.scene.control.TextFormatter<String>(cambio -> {
            String nuevo = cambio.getControlNewText();
            if (nuevo.matches("\\d{0," + maxLongitud + "}")) {
                return cambio;
            }
            return null;
        }));
    }

    private void setEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
