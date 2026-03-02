package mx.uv.fei.domain.services.visitors;

import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.ExternalVisitorDAO;
import mx.uv.fei.infrastructure.repositories.InstitutionalMemberDAO;
import mx.uv.fei.domain.exceptions.ServiceException;

import mx.uv.fei.domain.entities.Visitor;

public class VisitorSearchService {
    private final InstitutionalMemberDAO institutionalDAO = new InstitutionalMemberDAO();
    private final ExternalVisitorDAO externalDAO = new ExternalVisitorDAO();

    public Visitor findVisitorById(String identifier) throws ServiceException {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        try {
            Visitor visitor = institutionalDAO.getInstitutionalMemberById(identifier);
            
            if (visitor == null) {
                visitor = externalDAO.getExternalVisitorById(identifier);
            }
            
            return visitor;
        } catch (DAOException e) {
            throw new ServiceException("Error de conexión al buscar el identificador.", e);
        }
    }
}