package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;
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
}