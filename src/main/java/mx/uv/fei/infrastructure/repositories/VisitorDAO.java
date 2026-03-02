package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.entities.ExternalVisitor;
import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.entities.Visitor;

public class VisitorDAO {
    public int insertVisitor(Visitor visitor) throws DAOException {
        String query = "INSERT INTO Visitante (nombre, apellidos, correo) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, visitor.getFirstName());
            statement.setString(2, visitor.getLastName());
            statement.setString(3, visitor.getEmail());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (
                    ResultSet rs = statement.getGeneratedKeys()
                ) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar visitante base: ", e);
        }
        return generatedId;
    }

    public Visitor getVisitorByInternalId(int visitorId) throws DAOException {
        String query = "SELECT v.*, m.id_institucional, e.folio \n" +
                       "FROM Visitante v \n" +
                       "LEFT JOIN MiembroInstitucional m ON v.id_visitante = m.id_visitante \n" +
                       "LEFT JOIN Externo e ON v.id_visitante = e.id_visitante \n" +
                       "WHERE v.id_visitante = ?";
                        
        Visitor visitor = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, visitorId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String matricula = resultSet.getString("id_institucional"); 
                    String folio = resultSet.getString("folio");

                    if (matricula != null && !matricula.trim().isEmpty()) {
                        InstitutionalMember member = new InstitutionalMember();
                        member.setId(resultSet.getInt("id_visitante"));
                        member.setFirstName(resultSet.getString("nombre"));
                        member.setLastName(resultSet.getString("apellidos"));
                        member.setEmail(resultSet.getString("correo"));
                        
                        member.setInstitutionalId(matricula); 
                        visitor = member;

                    } else if (folio != null && !folio.trim().isEmpty()) {
                        ExternalVisitor external = new ExternalVisitor();
                        external.setId(resultSet.getInt("id_visitante"));
                        external.setFirstName(resultSet.getString("nombre"));
                        external.setLastName(resultSet.getString("apellidos"));
                        external.setEmail(resultSet.getString("correo"));
                        
                        external.setDocumentFolio(folio); 
                        visitor = external;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el visitante por su ID interno: " + visitorId, e);
        }

        return visitor;
    }
}