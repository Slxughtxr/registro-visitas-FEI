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
import mx.uv.fei.domain.services.catalogs.CatalogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VisitRegistrationController {
    @FXML private TextField identifierTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private ComboBox<String> visitorTypeComboBox;
    @FXML private ComboBox<String> hostComboBox;
    @FXML private ComboBox<String> evidenceComboBox;

    @FXML private ComboBox<String> identificationTypeComboBox; 

    @FXML private TextField subjectTextField;
    @FXML private VBox externalFieldsContainer;
    @FXML private TextField hostTextField;
    @FXML private TextField evidenceTextField;

    @FXML private Button registerButton;

    private final VisitorRegistrationService visitorService = new VisitorRegistrationService();
    private final VisitEntryService visitService = new VisitEntryService();
    private final CatalogService catalogService = new CatalogService();

    private Visitor currentVisitor;
    
    private Map<String, Integer> idCatalogMap = new HashMap<>();
    private Map<String, Integer> hostCatalogMap = new HashMap<>();
    private Map<String, Integer> evidenceCatalogMap = new HashMap<>();

    @FXML
    public void initialize() {
        visitorTypeComboBox.setItems(FXCollections.observableArrayList("Interno (FEI/UV)", "Externo"));
        
        try {
            idCatalogMap = catalogService.getIdentificationTypes();
            hostCatalogMap = catalogService.getHosts();
            evidenceCatalogMap = catalogService.getEvidences();
            
            if (identificationTypeComboBox != null) {
                identificationTypeComboBox.setItems(FXCollections.observableArrayList(idCatalogMap.keySet()));
            }
            if (hostComboBox != null) {
                hostComboBox.setItems(FXCollections.observableArrayList(hostCatalogMap.keySet()));
            }
            if (evidenceComboBox != null) {
                evidenceComboBox.setItems(FXCollections.observableArrayList(evidenceCatalogMap.keySet()));
            }
        } catch (ServiceException e) {
            showErrorMessage("Error de Catálogos", "No se pudieron cargar las listas desplegables. Contacte al administrador.");
        }
        
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

    @FXML
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
            
            // NUEVA LÓGICA: Validar y asignar el ID del catálogo
            String selectedIdType = identificationTypeComboBox.getValue();
            if (selectedIdType == null || selectedIdType.isEmpty()) {
                throw new ServiceException("Debe seleccionar un tipo de identificación para el visitante externo.");
            }
            
            // Recuperamos el int (1, 2, 3...) usando el nombre seleccionado
            int idIdentificacion = idCatalogMap.get(selectedIdType);
            
            // ATENCIÓN: Asegúrate de que tu clase ExternalVisitor tenga este método
            newExternal.setIdentificationTypeId(idIdentificacion); 
            
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
            String selectedHost = hostComboBox.getValue();
            if (selectedHost != null && !selectedHost.isEmpty()) {
                newVisit.setHostId(hostCatalogMap.get(selectedHost));
            }
            
            String selectedEvidence = evidenceComboBox.getValue();
            if (selectedEvidence != null && !selectedEvidence.isEmpty()) {
                newVisit.setEvidenceId(evidenceCatalogMap.get(selectedEvidence));
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