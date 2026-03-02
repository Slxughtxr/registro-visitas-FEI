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