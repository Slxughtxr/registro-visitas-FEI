package mx.uv.fei.presentation.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowManager {
    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void changeScene(String fxmlName, String title) throws IOException {
        FXMLLoader loader = getLoader(fxmlName);
        Parent root = loader.load();
        
        showScene(root, title);
    }

    public static <T> T changeSceneAndGetController(String fxmlName, String title) throws IOException {
        FXMLLoader loader = getLoader(fxmlName);
        Parent root = loader.load();
        
        showScene(root, title);
    
        return loader.getController(); 
    }

    private static FXMLLoader getLoader(String fxmlName) {
        String path = "/mx/uv/fei/presentation/views/" + fxmlName;
        java.net.URL fxmlUrl = WindowManager.class.getResource(path);
    
        if (fxmlUrl == null) {
            System.err.println("ERROR CRÍTICO: Java no encuentra el archivo FXML.");
            System.err.println("Ruta buscada: " + path);
            System.err.println("Revisa si el archivo está dentro de target/classes" + path);
        }
        
        return new FXMLLoader(fxmlUrl);
    }

    private static void showScene(Parent root, String title) {
        Scene scene = new Scene(root);
        mainStage.setTitle(title);
        mainStage.setScene(scene);
        mainStage.show();
    }
}