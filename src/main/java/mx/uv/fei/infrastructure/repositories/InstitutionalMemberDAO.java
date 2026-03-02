package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.entities.InstitutionalMember;

public class InstitutionalMemberDAO {
    private final VisitorDAO visitorDAO = new VisitorDAO();

    public boolean insertInstitutionalMember(InstitutionalMember member) throws DAOException {
        int visitorId = visitorDAO.insertVisitor(member);
        
        if (visitorId == -1) {
            throw new DAOException("Fallo la inserción en la tabla base Visitante.", null);
        }

        String query = "INSERT INTO MiembroInstitucional (id_visitante, id_institucional, tipo) VALUES (?, ?, ?)";
        
        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {    
            statement.setInt(1, visitorId);
            statement.setString(2, member.getInstitutionalId());
            statement.setString(3, member.getMemberType().getDatabaseValue());

            return statement.executeUpdate() > 0; 
        } catch (SQLException e) {
            throw new DAOException("Error al insertar miembro institucional en la BD: ", e);
        }
    }
}