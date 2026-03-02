package mx.uv.fei.domain.services.visits;

import mx.uv.fei.domain.dto.Pagination;
import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.VisitDAO;

import java.time.LocalDate;
import java.util.List;

public class VisitReportService {

    private final VisitDAO visitDAO = new VisitDAO();

    public List<Visit> getVisitHistory(LocalDate startDate, LocalDate endDate, Pagination pagination) throws ServiceException {
        validateDateRange(startDate, endDate);

        if (pagination == null) {
            pagination = new Pagination(1, 20); 
        }

        try {
            return visitDAO.getVisitsByDateRange(startDate, endDate, pagination);
        } catch (DAOException e) {
            throw new ServiceException("No se pudo recuperar el historial de visitas de la base de datos.", e);
        }
    }

    public int getTotalPages(LocalDate startDate, LocalDate endDate, int pageSize) throws ServiceException {
        validateDateRange(startDate, endDate);
        
        if (pageSize <= 0) {
            pageSize = 20;
        }

        try {
            int totalRecords = visitDAO.countVisitsByDateRange(startDate, endDate);
            return (int) Math.ceil((double) totalRecords / pageSize);
        } catch (DAOException e) {
            throw new ServiceException("No se pudo calcular el total de páginas para el reporte.", e);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) throws ServiceException {
        if (startDate == null || endDate == null) {
            throw new ServiceException("Debe especificar una fecha de inicio y una fecha de fin.");
        }
        if (startDate.isAfter(endDate)) {
            throw new ServiceException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }
}