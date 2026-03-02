package mx.uv.fei.domain.services.visitors;

import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.ExternalVisitorDAO;
import mx.uv.fei.infrastructure.repositories.InstitutionalMemberDAO;

public class VisitorRegistrationService {
    
    private final InstitutionalMemberDAO institutionalDAO = new InstitutionalMemberDAO();
    private final ExternalVisitorDAO externalDAO = new ExternalVisitorDAO();

    public void registerInstitutionalMember(InstitutionalMember member) throws ServiceException {
        validateBasicData(member.getFirstName(), member.getLastName(), member.getEmail());
        
        if (member.getInstitutionalId() == null || member.getInstitutionalId().trim().isEmpty()) {
            throw new ServiceException("La matrícula o número de personal es obligatorio.");
        }
        
        if (member.getMemberType() == null) {
            throw new ServiceException("Debe seleccionar un tipo de miembro institucional.");
        }

        try {
            boolean success = institutionalDAO.insertInstitutionalMember(member);
            if (!success) {
                throw new ServiceException("No se pudo registrar al miembro institucional en el sistema.");
            }
        } catch (DAOException e) {
            throw new ServiceException("Error de base de datos al registrar al miembro institucional.", e);
        }
    }

    public void registerExternalVisitor(ExternalVisitor external) throws ServiceException {
        validateBasicData(external.getFirstName(), external.getLastName(), external.getEmail());
        
        if (external.getDocumentFolio() == null || external.getDocumentFolio().trim().isEmpty()) {
            throw new ServiceException("El folio de la identificación es obligatorio para externos.");
        }

        try {
            boolean success = externalDAO.insertExternalVisitor(external);
            if (!success) {
                throw new ServiceException("No se pudo registrar al visitante externo en el sistema.");
            }
        } catch (DAOException e) {
            throw new ServiceException("Error de base de datos al registrar al visitante externo.", e);
        }
    }

    private void validateBasicData(String firstName, String lastName, String email) throws ServiceException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ServiceException("El nombre del visitante no puede estar vacío.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ServiceException("Los apellidos del visitante no pueden estar vacíos.");
        }
    }
}