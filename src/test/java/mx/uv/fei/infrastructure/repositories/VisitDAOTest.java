package mx.uv.fei.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.entities.Visitor;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.dto.Pagination;

public class VisitDAOTest {

    @Test
    public void testInsertVisitSuccess() throws DAOException {
        VisitorDAO visitorDAO = new VisitorDAO();
        Visitor dummyVisitor = new ExternalVisitor(); 
        dummyVisitor.setFirstName("Visitante");
        dummyVisitor.setLastName("De Prueba");
        dummyVisitor.setEmail("prueba.visita@uv.mx");

        int validVisitorId = visitorDAO.insertVisitor(dummyVisitor);
        
        assertTrue(validVisitorId > 0, "Debe crearse el visitante base para probar la visita.");

        VisitDAO visitDAO = new VisitDAO();
        Visit testVisit = new Visit(
            validVisitorId, 
            null,     
            null,     
            LocalDate.now(),
            LocalTime.now(), 
            "Entrega de documentación escolar", 
            true         
        );

        boolean isSaved = visitDAO.insertVisit(testVisit);

        assertTrue(isSaved, "La visita debería registrarse correctamente en la BD");
    }

    @Test
    public void testRegisterCheckOutSuccess() throws DAOException {
        VisitorDAO visitorDAO = new VisitorDAO();
        Visitor dummyVisitor = new ExternalVisitor(); 
        dummyVisitor.setFirstName("Visitante Salida");
        dummyVisitor.setLastName("De Prueba");
        dummyVisitor.setEmail("prueba.salida@uv.mx");
        int validVisitorId = visitorDAO.insertVisitor(dummyVisitor);
    
        VisitDAO visitDAO = new VisitDAO();
        Visit testVisit = new Visit(
            validVisitorId, null, null, 
            LocalDate.now(), LocalTime.now(), 
            "Prueba de Check-out", 
            true 
        );
        visitDAO.insertVisit(testVisit);

        boolean isCheckedOut = visitDAO.registerCheckOut(
            validVisitorId, 
            LocalDate.now(), 
            LocalTime.now()
        );

        assertTrue(isCheckedOut, "La visita activa debería actualizarse con la fecha y hora de salida");
    }

     @Test
    public void testGetVisitsByDateRangeEmpty() throws DAOException {
        VisitDAO visitDAO = new VisitDAO();
        LocalDate start = LocalDate.of(1900, 1, 1);
        LocalDate end   = LocalDate.of(1900, 1, 2);
        Pagination page = new Pagination(10, 0);

        List<Visit> historial = visitDAO.getVisitsByDateRange(start, end, page);

        assertNotNull(historial, "La lista no debe ser nula incluso si no hay registros");
        assertTrue(historial.isEmpty(), "Debiera regresar lista vacía para periodo sin visitas");
    }
}