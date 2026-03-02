package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
import mx.uv.fei.domain.entities.Visit;

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

    public List<Visit> getVisitsHistory(int limit, int offset) throws DAOException {
        List<Visit> visits = new ArrayList<>();
        String query = "SELECT * FROM Visita ORDER BY fecha_entrada DESC, hora_entrada DESC LIMIT ? OFFSET ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (
                java.sql.ResultSet resultSet = statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    Visit visit = new Visit(
                        resultSet.getInt("id_visitante"),
                        null,
                        null,
                        resultSet.getObject("fecha_entrada", java.time.LocalDate.class),
                        resultSet.getObject("hora_entrada", java.time.LocalTime.class),
                        resultSet.getString("asunto"),
                        resultSet.getBoolean("activa")
                    );

                    int hostId = resultSet.getInt("id_anfitrion");
                    if (!resultSet.wasNull()) { 
                        visit.setHostId(hostId); 
                    }

                    int evidenceId = resultSet.getInt("id_evidencia");
                    if (!resultSet.wasNull()) { 
                        visit.setEvidenceId(evidenceId); 
                    }

                    java.sql.Date exitDate = resultSet.getDate("fecha_salida");
                    if (exitDate != null) { 
                        visit.setExitDate(exitDate.toLocalDate()); 
                    }

                    java.sql.Time exitTime = resultSet.getTime("hora_salida");
                    if (exitTime != null) { 
                        visit.setExitTime(exitTime.toLocalTime()); 
                    }

                    visits.add(visit);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error SQL al consultar historial: ", e);
        }
        return visits;
    }
}