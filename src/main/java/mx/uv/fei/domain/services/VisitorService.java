package mx.uv.fei.domain.services;

import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.ExternalVisitorDAO;
import mx.uv.fei.infrastructure.repositories.InstitutionalMemberDAO;
import mx.uv.fei.domain.entities.Visitor;

public class VisitorService {
    private final InstitutionalMemberDAO institutionalDAO = new InstitutionalMemberDAO();
    private final ExternalVisitorDAO externalDAO = new ExternalVisitorDAO();

    public Visitor findVisitorById(String identifier) throws DAOException {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        Visitor visitor = institutionalDAO.getInstitutionalMemberById(identifier);

        if (visitor == null) {
            visitor = externalDAO.getExternalVisitorById(identifier);
        }

        return visitor;
    }
}