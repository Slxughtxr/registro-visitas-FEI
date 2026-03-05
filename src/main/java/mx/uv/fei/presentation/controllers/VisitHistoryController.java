package mx.uv.fei.presentation.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;

import mx.uv.fei.domain.dto.Pagination;
import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.entities.Visitor;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.domain.services.visits.VisitReportService;
import mx.uv.fei.presentation.utils.WindowManager;
import mx.uv.fei.domain.services.visitors.VisitorSearchService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class VisitHistoryController {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Visit> visitsTable;
    @FXML private TableColumn<Visit, String> visitorIdentifierColumn;
    @FXML private TableColumn<Visit, String> visitorTypeColumn;
    @FXML private TableColumn<Visit, String> subjectColumn;
    @FXML private TableColumn<Visit, LocalDate> dateColumn;
    @FXML private TableColumn<Visit, LocalTime> timeColumn;
    @FXML private TableColumn<Visit, String> statusColumn;

    @FXML private Label pageLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private final VisitReportService reportService = new VisitReportService();
    private final VisitorSearchService visitorSearchService = new VisitorSearchService();
    
    private int currentPage = 1;
    private final int pageSize = 15; 
    private int maxPages = 1;

    @FXML
    public void initialize() {
        visitorIdentifierColumn.setCellValueFactory(cellData -> {
            try {
                Visitor visitor = visitorSearchService.findVisitorByInternalId(cellData.getValue().getVisitorId());
                
                if (visitor != null) {
                    if (visitor instanceof InstitutionalMember) {
                        return new SimpleStringProperty(((InstitutionalMember) visitor).getInstitutionalId());
                    } else if (visitor instanceof ExternalVisitor) {
                        return new SimpleStringProperty(((ExternalVisitor) visitor).getDocumentFolio());
                    }
                }
            } catch (ServiceException e) {
                return new SimpleStringProperty("Error al recuperar");
            }
            return new SimpleStringProperty("Desconocido");
        });

        visitorTypeColumn.setCellValueFactory(cellData -> {
            try {
                Visitor visitor = visitorSearchService.findVisitorByInternalId(cellData.getValue().getVisitorId());
                
                if (visitor != null) {
                    String type = (visitor instanceof InstitutionalMember) ? "Miembro Institucional" : "Externo";
                    return new SimpleStringProperty(type);
                }
            } catch (ServiceException e) {
                return new SimpleStringProperty("Error al recuperar");
            }
            return new SimpleStringProperty("Desconocido");
        });

        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("entryTime"));

        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            return new SimpleStringProperty(isActive ? "Activa" : "Finalizada");
        });

        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        endDatePicker.setValue(LocalDate.now());

        loadData();

        visitsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && visitsTable.getSelectionModel().getSelectedItem() != null) {
                Visit selectedVisit = visitsTable.getSelectionModel().getSelectedItem();
                showVisitDetails(selectedVisit);
            }
        });
    }

    @FXML
    public void onSearchClick() {
        currentPage = 1;
        loadData();
    }

    @FXML
    public void onPreviousPageClick() {
        if (currentPage > 1) {
            currentPage--;
            loadData();
        }
    }

    @FXML
    public void onNextPageClick() {
        if (currentPage < maxPages) {
            currentPage++;
            loadData();
        }
    }

    private void loadData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        try {
            maxPages = reportService.getTotalPages(start, end, pageSize);
            if (maxPages == 0) maxPages = 1;

            Pagination pagination = new Pagination(currentPage, pageSize);

            List<Visit> visits = reportService.getVisitHistory(start, end, pagination);
            
            ObservableList<Visit> visitObservableList = FXCollections.observableArrayList(visits);
            visitsTable.setItems(visitObservableList);

            updatePaginationUI();

        } catch (ServiceException e) {
            showErrorMessage("Error de Consulta", e.getMessage());
        }
    }

    private void updatePaginationUI() {
        pageLabel.setText("Página " + currentPage + " de " + maxPages);
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= maxPages);
    }

    private void showVisitDetails(Visit visit) {
        try {
            Visitor visitor = visitorSearchService.findVisitorByInternalId(visit.getVisitorId());
            
            StringBuilder details = new StringBuilder();
            
            renderVisitorDetails(visitor, details);
            
            details.append("\n--- DATOS DE LA VISITA ---\n");
            details.append("Motivo/Asunto: ").append(visit.getSubject()).append("\n");
            details.append("Entrada: ").append(visit.getEntryDate()).append(" a las ").append(visit.getEntryTime()).append("\n");
            
            renderVisitStatus(visit, details);
           
            if (isValidId(visit.getHostId())) {
                details.append("\nID Anfitrión visitado: ").append(visit.getHostId()).append("\n");
            }
            if (isValidId(visit.getEvidenceId())) {
                details.append("ID Evidencia dejada: ").append(visit.getEvidenceId()).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles de la Visita");
            alert.setHeaderText("Información completa del registro"); 
            alert.setContentText(details.toString());
            
            alert.getDialogPane().setMinWidth(400);
            alert.showAndWait();

        } catch (ServiceException e) {
            showErrorMessage("Error de Consulta", "No se pudo cargar la información completa del visitante.");
        }
    }

    private boolean isValidId(Integer id){
        return id != null && id > 0;
    }

    private void renderVisitorDetails(Visitor visitor, StringBuilder details){
        details.append("--- DATOS DEL VISITANTE ---\n");
        if (visitor != null) {
            details.append("Nombre: ").append(visitor.getFirstName()).append(" ").append(visitor.getLastName()).append("\n");
            details.append("Correo: ").append(visitor.getEmail()).append("\n");
                
            if (visitor instanceof InstitutionalMember) {
                details.append("Tipo: Miembro Institucional (FEI/UV)\n");
                details.append("Matrícula/No. Personal: ").append(((InstitutionalMember) visitor).getInstitutionalId()).append("\n");
            } else if (visitor instanceof ExternalVisitor) {
                details.append("Tipo: Visitante Externo\n");
                details.append("Folio de Identificación: ").append(((ExternalVisitor) visitor).getDocumentFolio()).append("\n");
           }
        } else {
            details.append("Advertencia: Visitante no encontrado en la base de datos.\n");
        }
    }

    private void renderVisitStatus(Visit visit, StringBuilder details){
         if (!visit.isActive()) {
                String exitDate = (visit.getExitDate() != null) ? visit.getExitDate().toString() : "No registrada";
                String exitTime = (visit.getExitTime() != null) ? visit.getExitTime().toString() : "No registrada";
                details.append("Salida: ").append(exitDate).append(" a las ").append(exitTime).append("\n");
                details.append("Estado: Finalizada\n");
        } else {
            details.append("Estado: En curso (Activa)\n");
        }
    }

    @FXML
    public void onBackClick() {
        try {
            WindowManager.changeScene("MainMenu.fxml", "Menú Principal - Control de Accesos");
        } catch (IOException e) {
            showErrorMessage("Error", "No se pudo volver al menú principal.");
        }
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}