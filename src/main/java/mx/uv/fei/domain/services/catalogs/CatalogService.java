package mx.uv.fei.domain.services.catalogs;

import mx.uv.fei.domain.exceptions.ServiceException;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.infrastructure.repositories.CatalogDAO;

import java.util.Map;

public class CatalogService {
    
    private final CatalogDAO catalogDAO = new CatalogDAO();

    public Map<String, Integer> getIdentificationTypes() throws ServiceException {
        try {
            return catalogDAO.getActiveIdentificationTypes();
        } catch (DAOException e) {
            throw new ServiceException("No se pudo cargar la lista de identificaciones desde la base de datos.", e);
        }
    }

    public Map<String, Integer> getHosts() throws ServiceException {
        try {
            return catalogDAO.getActiveHosts();
        } catch (DAOException e) {
            throw new ServiceException("No se pudo cargar la lista de anfitriones.", e);
        }
    }

    public Map<String, Integer> getEvidences() throws ServiceException {
        try {
            return catalogDAO.getActiveEvidences();
        } catch (DAOException e) {
            throw new ServiceException("No se pudo cargar la lista de evidencias.", e);
        }
    }
}