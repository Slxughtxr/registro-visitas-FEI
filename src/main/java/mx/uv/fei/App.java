package mx.uv.fei;

import javafx.application.Application;
import javafx.stage.Stage;
import mx.uv.fei.presentation.utils.WindowManager; 

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            WindowManager.setMainStage(primaryStage);
            WindowManager.changeScene("MainMenu.fxml", "Menú Principal - Control de Accesos");
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar la interfaz gráfica: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}