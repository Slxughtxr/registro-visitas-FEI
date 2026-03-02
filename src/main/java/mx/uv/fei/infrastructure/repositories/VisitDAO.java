package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.entities.Visit;
import mx.uv.fei.domain.dto.Pagination;

public class VisitDAO {
    public boolean insertVisit(Visit visit) throws DAOException {
        String query = "INSERT INTO Visita (id_visitante, id_anfitrion, id_evidencia, fecha_entrada, hora_entrada, asunto, activa) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, visit.getVisitorId());
            statement.setObject(2, visit.getHostId(), Types.INTEGER);
            statement.setObject(3, visit.getEvidenceId(), Types.INTEGER);
            statement.setObject(4, visit.getEntryDate());
            statement.setObject(5, visit.getEntryTime());
            statement.setString(6, visit.getSubject());
            statement.setBoolean(7, visit.isActive());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error SQL al registrar visita: ", e);
        }
    }

    public boolean registerCheckOut(int visitorId, java.time.LocalDate leaveDate, java.time.LocalTime leaveTime) throws DAOException {
        String query = "UPDATE Visita SET fecha_salida = ?, hora_salida = ?, activa = ? WHERE id_visitante = ? AND activa = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setObject(1, leaveDate);
            statement.setObject(2, leaveTime);
            statement.setBoolean(3, false); 
            statement.setInt(4, visitorId);
            statement.setBoolean(5, true); 

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error SQL al registrar salida: ", e);
        }
    }

    public List<Visit> getVisitsByDateRange(LocalDate startDate, LocalDate endDate, Pagination pagination) throws DAOException {
        List<Visit> visits = new ArrayList<>();
        String query = "SELECT * FROM Visita WHERE fecha_entrada BETWEEN ? AND ? ORDER BY fecha_entrada DESC, hora_entrada DESC LIMIT ? OFFSET ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            
            statement.setInt(3, pagination.getPageSize());
            statement.setInt(4, pagination.getOffset());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Visit visit = new Visit();
                    visit.setVisitorId(rs.getInt("id_visitante"));
                    
                    visit.setHostId((Integer) rs.getObject("id_anfitrion"));
                    visit.setEvidenceId((Integer) rs.getObject("id_evidencia"));
                
                    visit.setEntryDate(rs.getObject("fecha_entrada", LocalDate.class));
                    visit.setEntryTime(rs.getObject("hora_entrada", java.time.LocalTime.class));
                    visit.setSubject(rs.getString("asunto"));
                    visit.setActive(rs.getBoolean("activa"));

                    visits.add(visit);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el historial paginado de visitas.", e);
        }
        
        return visits;
    }

    public int countVisitsByDateRange(LocalDate startDate, LocalDate endDate) throws DAOException {
        String query = "SELECT COUNT(*) FROM Visita WHERE fecha_entrada BETWEEN ? AND ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al contar el total de visitas.", e);
        }
        
        return 0;
    }
}