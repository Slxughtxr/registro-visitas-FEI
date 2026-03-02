package mx.uv.fei.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.uv.fei.domain.entities.Visitor;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.domain.services.visitors.VisitorSearchService;
import mx.uv.fei.domain.services.visits.VisitExitService;
import mx.uv.fei.presentation.utils.WindowManager;

import java.io.IOException;

public class RegisterExitController {
    @FXML
    private TextField identifierTextField;

    private final VisitorSearchService searchService = new VisitorSearchService();
    private final VisitExitService exitService = new VisitExitService();

    @FXML
    public void onRegisterExitClick() {
        String identifier = identifierTextField.getText().trim();

        if (identifier.isEmpty()) {
            showWarningMessage("Por favor, ingrese un identificador.");
            return;
        }

        try {
            Visitor visitor = searchService.findVisitorById(identifier);

            if (visitor == null) {
                showWarningMessage("No se encontró ningún visitante registrado con el identificador: " + identifier);
                return;
            }

            exitService.terminateActiveVisit(visitor.getId());

            showSuccessMessage("Salida registrada correctamente para:\n" + visitor.getFirstName() + " " + visitor.getLastName());
            
            identifierTextField.clear();

        } catch (ServiceException e) {
            showErrorMessage("Error al registrar salida", e.getMessage());
        }
    }

    @FXML
    public void onBackClick() {
        try {
            WindowManager.changeScene("MainMenu.fxml", "Menú Principal - Control de Accesos");
        } catch (IOException e) {
            showErrorMessage("Error de navegación", "No se pudo volver al menú principal.");
        }
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salida Registrada");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}