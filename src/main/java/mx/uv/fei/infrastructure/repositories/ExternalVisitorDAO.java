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
            external.setId(visitorId);

            return statement.executeUpdate() > 0;   
        } catch (SQLException e) {
            throw new DAOException("Error al insertar visitante externo: ", e);
        }
    }

    public ExternalVisitor getExternalVisitorById(String folio) throws DAOException {
        String query = "SELECT v.id_visitante, v.nombre, v.apellidos, v.correo, " +
                       "e.id_identificacion, e.folio " +
                       "FROM Visitante v " +
                       "INNER JOIN Externo e ON v.id_visitante = e.id_visitante " +
                       "WHERE e.folio = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, folio);

            try (java.sql.ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    ExternalVisitor visitor = new ExternalVisitor();
                
                    visitor.setId(rs.getInt("id_visitante")); 
                    visitor.setFirstName(rs.getString("nombre"));
                    visitor.setLastName(rs.getString("apellidos"));
                    visitor.setEmail(rs.getString("correo"));
                    visitor.setIdentificationTypeId(rs.getInt("id_identificacion"));
                    visitor.setDocumentFolio(rs.getString("folio"));
                    
                    return visitor;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar visitante externo por folio: " + folio, e);
        }
    
        return null; 
    }
}