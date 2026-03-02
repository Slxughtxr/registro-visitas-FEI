package mx.uv.fei.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import mx.uv.fei.domain.entities.Visitor;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.domain.services.visitors.VisitorSearchService;
import mx.uv.fei.presentation.utils.WindowManager;

import java.io.IOException;

public class SearchVisitorController {

    @FXML
    private TextField searchTextField;

    private final VisitorSearchService searchService = new VisitorSearchService();

    @FXML
    public void onSearchClick() {
        String identifier = searchTextField.getText().trim();

        if (identifier.isEmpty()) {
            showWarningMessage("Por favor, ingrese un identificador para buscar.");
            return;
        }

        try {
            Visitor foundVisitor = searchService.findVisitorById(identifier);

            if (foundVisitor != null) {
                navigateToVisitRegistration(foundVisitor, identifier);
            } else {
                promptForNewRegistration(identifier);
            }

        } catch (ServiceException e) {
            showErrorMessage("Error en la búsqueda", e.getMessage());
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

    private void promptForNewRegistration(String identifier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Visitante No Encontrado");
        alert.setHeaderText("El identificador " + identifier + " no está registrado.");
        alert.setContentText("¿Desea registrar a este nuevo visitante para darle acceso?");

        ButtonType buttonYes = new ButtonType("Sí");
        ButtonType buttonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                navigateToVisitRegistration(null, identifier);
            }
        });
    }

    private void navigateToVisitRegistration(Visitor visitor, String searchedId) {
        try {
            VisitRegistrationController controller = WindowManager.changeSceneAndGetController(
                "VisitRegistration.fxml", "Registro de Visita - FEI"
            );
            
            controller.setupScreen(visitor, searchedId);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Error", "No se pudo cargar la pantalla de registro.");
        }
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
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