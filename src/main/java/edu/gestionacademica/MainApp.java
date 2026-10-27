package edu.gestionacademica;

import edu.gestionacademica.config.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador = new FXMLLoader(MainApp.class.getResource("estudiante-view.fxml"));
        Scene escena = new Scene(cargador.load(), 700, 500);
        escenario.setTitle("Gestión Académica");
        escenario.setScene(escena);
        escenario.show();
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}
