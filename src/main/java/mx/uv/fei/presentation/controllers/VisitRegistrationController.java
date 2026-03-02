package mx.uv.fei.presentation.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.entities.Visitor;
import mx.uv.fei.domain.enums.MemberType;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.domain.services.visitors.VisitorRegistrationService;
import mx.uv.fei.domain.services.visits.VisitEntryService;
import mx.uv.fei.presentation.utils.WindowManager;

import java.io.IOException;

public class VisitRegistrationController {
    @FXML private TextField identifierTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private ComboBox<String> visitorTypeComboBox;

    // --- Campos de la Visita ---
    @FXML private TextField subjectTextField;
    @FXML private VBox externalFieldsContainer;
    @FXML private TextField hostTextField;
    @FXML private TextField evidenceTextField;

    @FXML private Button registerButton;

    private final VisitorRegistrationService visitorService = new VisitorRegistrationService();
    private final VisitEntryService visitService = new VisitEntryService();

    private Visitor currentVisitor;

    @FXML
    public void initialize() {
        visitorTypeComboBox.setItems(FXCollections.observableArrayList("Interno (FEI/UV)", "Externo"));
        
        visitorTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isExternal = "Externo".equals(newVal);
            externalFieldsContainer.setVisible(isExternal);
            externalFieldsContainer.setManaged(isExternal); 
        });
    }

    public void setupScreen(Visitor visitor, String searchedId) {
        this.currentVisitor = visitor;

        if (visitor != null) {
            identifierTextField.setText(searchedId);
            firstNameTextField.setText(visitor.getFirstName());
            lastNameTextField.setText(visitor.getLastName());
            emailTextField.setText(visitor.getEmail());

            identifierTextField.setEditable(false);
            firstNameTextField.setEditable(false);
            lastNameTextField.setEditable(false);
            emailTextField.setEditable(false);
            visitorTypeComboBox.setDisable(true);

            if (visitor instanceof ExternalVisitor) {
                visitorTypeComboBox.setValue("Externo");
            } else if (visitor instanceof InstitutionalMember) {
                visitorTypeComboBox.setValue("Interno (FEI/UV)");
            }

        } else {
            identifierTextField.setText(searchedId);
            visitorTypeComboBox.setValue("Externo");
        }
    }

    public void onCancelClick() {
        try {
            WindowManager.changeScene("SearchVisitor.fxml", "Registrar Entrada - FEI");
        } catch (IOException e) {
            showErrorMessage("Error de Navegación", "No se pudo regresar a la pantalla de búsqueda.");
        }
    }

    @FXML
    public void onRegisterClick() {
        try {
            if (currentVisitor == null) {
                registerNewVisitor();
            }

            registerVisit();

            showSuccessMessage("¡Visita registrada exitosamente!");
            WindowManager.changeScene("MainMenu.fxml", "Menú Principal - Control de Accesos");

        } catch (ServiceException e) {
            showErrorMessage("Error de Registro", e.getMessage());
        } catch (IOException e) {
            showErrorMessage("Error de Navegación", "No se pudo volver al menú principal.");
        } catch (NumberFormatException e) {
            showErrorMessage("Error de Formato", "Los campos de Anfitrión y Evidencia deben ser números válidos (IDs).");
        }
    }

    private void registerNewVisitor() throws ServiceException {
        boolean isExternal = "Externo".equals(visitorTypeComboBox.getValue());

        if (isExternal) {
            ExternalVisitor newExternal = new ExternalVisitor();
            newExternal.setDocumentFolio(identifierTextField.getText());
            newExternal.setFirstName(firstNameTextField.getText());
            newExternal.setLastName(lastNameTextField.getText());
            newExternal.setEmail(emailTextField.getText());
            
            visitorService.registerExternalVisitor(newExternal);
            this.currentVisitor = newExternal; 
        } else {
            InstitutionalMember newInternal = new InstitutionalMember();
            newInternal.setInstitutionalId(identifierTextField.getText());
            newInternal.setFirstName(firstNameTextField.getText());
            newInternal.setLastName(lastNameTextField.getText());
            newInternal.setEmail(emailTextField.getText());
            newInternal.setMemberType(MemberType.STUDENT); 
            
            visitorService.registerInstitutionalMember(newInternal);
            this.currentVisitor = newInternal;
        }
    }

    private void registerVisit() throws ServiceException {
        Visit newVisit = new Visit();

        newVisit.setVisitorId(currentVisitor.getId()); 
        newVisit.setSubject(subjectTextField.getText());

        boolean isExternal = "Externo".equals(visitorTypeComboBox.getValue());
        if (isExternal) {
            if (!hostTextField.getText().isEmpty()) {
                newVisit.setHostId(Integer.parseInt(hostTextField.getText()));
            }
            if (!evidenceTextField.getText().isEmpty()) {
                newVisit.setEvidenceId(Integer.parseInt(evidenceTextField.getText()));
            }
        }

        visitService.registerNewVisit(newVisit);
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
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