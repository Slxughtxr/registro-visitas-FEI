package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.entities.InstitutionalMember;
import mx.uv.fei.domain.enums.MemberType;

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

    public InstitutionalMember getInstitutionalMemberById(String matricula) throws DAOException {
        String query = "SELECT v.id_visitante, v.nombre, v.apellidos, v.correo, " +
                       "m.id_institucional, m.tipo " +
                       "FROM Visitante v " +
                       "INNER JOIN MiembroInstitucional m ON v.id_visitante = m.id_visitante " +
                       "WHERE m.id_institucional = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, matricula);

            try (java.sql.ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    InstitutionalMember member = new InstitutionalMember();
                    
                    member.setId(rs.getInt("id_visitante"));
                    member.setFirstName(rs.getString("nombre"));
                    member.setLastName(rs.getString("apellidos"));
                    member.setEmail(rs.getString("correo"));
                    
                    member.setInstitutionalId(rs.getString("id_institucional"));

                    String tipoEnBD = rs.getString("tipo");

                    member.setMemberType(MemberType.fromDatabaseValue(tipoEnBD));
                    
                    return member;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar miembro institucional con ID: " + matricula, e);
        }
        
        return null;
    }
}