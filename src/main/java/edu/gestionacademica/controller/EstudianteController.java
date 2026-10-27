package edu.gestionacademica.controller;

import edu.gestionacademica.model.Estudiante;
import edu.gestionacademica.repository.EstudianteRepository;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

public class EstudianteController {

    @FXML
    private TextField txtNombres;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtCorreo;

    @FXML
    private ComboBox<String> cmbCarrera;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnConsultar;

    @FXML
    private TableView<Estudiante> tablaEstudiantes;

    @FXML
    private TableColumn<Estudiante, Long> colId;

    @FXML
    private TableColumn<Estudiante, String> colNombres;

    @FXML
    private TableColumn<Estudiante, String> colApellidos;

    @FXML
    private TableColumn<Estudiante, String> colCorreo;

    @FXML
    private TableColumn<Estudiante, String> colCarrera;

    @FXML
    private Label lblMensaje;

    private final EstudianteRepository repositorio = new EstudianteRepository();

    @FXML
    private void initialize() {
        cmbCarrera.setItems(FXCollections.observableArrayList(
                "Ingeniería de Sistemas de Información",
                "Ingeniería Civil",
                "Arquitectura",
                "Ingeniería Industrial"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera"));
        lblMensaje.setText("");
    }

    @FXML
    private void onGuardar() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String carrera = cmbCarrera.getValue() == null ? "" : cmbCarrera.getValue().trim();

        List<String> camposFaltantes = new ArrayList<>();
        if (nombres.isEmpty()) {
            camposFaltantes.add("nombres");
        }
        if (apellidos.isEmpty()) {
            camposFaltantes.add("apellidos");
        }
        if (correo.isEmpty()) {
            camposFaltantes.add("correo");
        }
        if (carrera.isEmpty()) {
            camposFaltantes.add("carrera");
        }
        if (!camposFaltantes.isEmpty()) {
            mostrarMensaje("Falta completar: " + String.join(", ", camposFaltantes) + ".", true);
            return;
        }
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            mostrarMensaje("El correo no tiene un formato válido.", true);
            return;
        }

        Estudiante estudiante = new Estudiante(nombres, apellidos, correo, carrera);
        // Task mantiene la operación de base de datos fuera del hilo de JavaFX.
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() {
                repositorio.guardar(estudiante);
                return null;
            }
        };

        btnGuardar.setDisable(true);
        btnConsultar.setDisable(true);
        tarea.setOnSucceeded(evento -> {
            habilitarBotones();
            mostrarMensaje("Estudiante guardado correctamente.", false);
            limpiarFormulario();
            onConsultar();
        });
        tarea.setOnFailed(evento -> {
            habilitarBotones();
            Throwable error = tarea.getException();
            if (error != null) {
                error.printStackTrace();
            }
            if (esCorreoDuplicado(error)) {
                mostrarMensaje("Ya existe un estudiante con ese correo.", true);
            } else {
                mostrarMensaje("No fue posible guardar el estudiante. Verifique la conexión o los datos.", true);
            }
        });
        ejecutarEnSegundoPlano(tarea);
    }

    @FXML
    private void onConsultar() {
        // La consulta también usa Task para no bloquear la interfaz mientras responde la base de datos.
        Task<List<Estudiante>> tarea = new Task<>() {
            @Override
            protected List<Estudiante> call() {
                return repositorio.listar();
            }
        };

        btnGuardar.setDisable(true);
        btnConsultar.setDisable(true);
        tarea.setOnSucceeded(evento -> {
            List<Estudiante> estudiantes = tarea.getValue();
            tablaEstudiantes.setItems(FXCollections.observableArrayList(estudiantes));
            habilitarBotones();
            mostrarMensaje(estudiantes.size() + " estudiante(s) encontrado(s).", false);
        });
        tarea.setOnFailed(evento -> {
            habilitarBotones();
            Throwable error = tarea.getException();
            if (error != null) {
                error.printStackTrace();
            }
            mostrarMensaje("No fue posible consultar los estudiantes. Verifique la conexión o la consulta.", true);
        });
        ejecutarEnSegundoPlano(tarea);
    }

    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtCorreo.clear();
        cmbCarrera.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError
                ? "-fx-text-fill: #b00020;"
                : "-fx-text-fill: #1b5e20;");
    }

    private boolean esCorreoDuplicado(Throwable error) {
        Throwable actual = error;
        while (actual != null) {
            if (actual instanceof ConstraintViolationException) {
                return true;
            }
            actual = actual.getCause();
        }
        return false;
    }

    private void ejecutarEnSegundoPlano(Task<?> tarea) {
        Thread hilo = new Thread(tarea, "gestion-academica-tarea");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void habilitarBotones() {
        btnGuardar.setDisable(false);
        btnConsultar.setDisable(false);
    }
}
