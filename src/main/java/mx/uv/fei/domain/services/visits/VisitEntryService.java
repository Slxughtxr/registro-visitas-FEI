package mx.uv.fei.domain.services.visits;

import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.VisitDAO;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitEntryService {

    private final VisitDAO visitDAO = new VisitDAO();

    public void registerNewVisit(Visit visit) throws ServiceException {
        validateVisitData(visit);

        if (visit.getEntryDate() == null) {
            visit.setEntryDate(LocalDate.now());
        }
        if (visit.getEntryTime() == null) {
            visit.setEntryTime(LocalTime.now());
        }
        visit.setActive(true);

        try {
            boolean success = visitDAO.insertVisit(visit);
            if (!success) {
                throw new ServiceException("No se pudo registrar la entrada de la visita en el sistema.");
            }
        } catch (DAOException e) {
            throw new ServiceException("Ocurrió un error al intentar guardar la visita en la base de datos.", e);
        }
    }

    private void validateVisitData(Visit visit) throws ServiceException {
        if (visit == null) {
            throw new ServiceException("Los datos de la visita están vacíos.");
        }
        if (visit.getVisitorId() <= 0) {
            throw new ServiceException("Es necesario identificar o registrar al visitante antes de darle acceso.");
        }
        if (visit.getSubject() == null || visit.getSubject().trim().isEmpty()) {
            throw new ServiceException("El motivo o asunto de la visita es obligatorio.");
        }
    }
}