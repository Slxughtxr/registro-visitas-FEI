package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.infrastructure.exceptions.DAOException;

public class ExternalVisitorDAO {
    private final VisitorDAO visitorDAO = new VisitorDAO();

    public boolean insertExternalVisitor(ExternalVisitor external) throws DAOException {
        int visitorId = visitorDAO.insertVisitor(external);
        
        if (visitorId == -1) {
            throw new DAOException("Fallo la inserción en la tabla base Visitante.", null);
        }

        String query = "INSERT INTO Externo (id_visitante, id_identificacion, folio) VALUES (?, ?, ?)";
        
        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {    
            statement.setInt(1, visitorId);
            statement.setInt(2, external.getIdentificationTypeId());
            statement.setString(3, external.getDocumentFolio());

            return statement.executeUpdate() > 0;   
        } catch (SQLException e) {
            throw new DAOException("Error al insertar visitante externo: ", e);
        }
    }
}