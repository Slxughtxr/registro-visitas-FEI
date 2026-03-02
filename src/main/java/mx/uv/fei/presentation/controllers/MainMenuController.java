package mx.uv.fei.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import mx.uv.fei.presentation.utils.WindowManager;

import java.io.IOException;

public class MainMenuController {

    @FXML
    public void onRegisterEntranceClick(ActionEvent event) {
        try {
            WindowManager.changeScene("SearchVisitor.fxml", "Registrar Entrada - FEI");
        } catch (IOException e) {
            showNavigationError("entrance registration screen", e);
        }
    }

    @FXML
    public void onRegisterExitClick(ActionEvent event) {
        try {
            WindowManager.changeScene("RegisterExit.fxml", "Registrar Salida - FEI");
        } catch (IOException e) {
            showNavigationError("exit registration screen", e);
        }
    }

    @FXML
    public void onViewHistoryClick(ActionEvent event) {
        try {
            WindowManager.changeScene("VisitHistory.fxml", "Historial de Visitas - FEI");
        } catch (IOException e) {
            showNavigationError("history screen", e);
        }
    }

    private void showNavigationError(String destination, IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Navegación");
        alert.setHeaderText("No se pudo cargar la interfaz");
        alert.setContentText("Ocurrió un problema al intentar abrir: " + destination);
        alert.showAndWait();
        e.printStackTrace();
    }
}