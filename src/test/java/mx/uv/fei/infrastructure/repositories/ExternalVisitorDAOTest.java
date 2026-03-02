package mx.uv.fei.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.infrastructure.exceptions.DAOException;

public class ExternalVisitorDAOTest {

    @Test
    public void testInsertExternalVisitorSuccess() throws DAOException {
        ExternalVisitorDAO dao = new ExternalVisitorDAO();
        ExternalVisitor invitado = new ExternalVisitor(
            "Carlos", 
            "Slim", 
            "carlos.slim@telmex.com", 
            1, 
            "INE-999888"
        );
        
        boolean isSaved = dao.insertExternalVisitor(invitado);

        assertTrue(isSaved, "El visitante externo debería guardarse correctamente en las tablas Visitante y Externo");
    }
}