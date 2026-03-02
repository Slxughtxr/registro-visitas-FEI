package mx.uv.fei.domain.services.visits;

import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.VisitDAO;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitExitService {
    
    private final VisitDAO visitDAO = new VisitDAO();

    public void terminateActiveVisit(int visitorId) throws ServiceException {
        if (visitorId <= 0) {
            throw new ServiceException("Identificador de visitante inválido.");
        }

        try {
            LocalDate leaveDate = LocalDate.now();
            LocalTime leaveTime = LocalTime.now();

            boolean success = visitDAO.registerCheckOut(visitorId, leaveDate, leaveTime);

            if (!success) {
                throw new ServiceException("No se encontró ninguna visita activa para este visitante. Es posible que ya haya registrado su salida.");
            }
            
        } catch (DAOException e) {
            throw new ServiceException("Error al comunicarse con la base de datos para registrar la salida.", e);
        }
    }
}