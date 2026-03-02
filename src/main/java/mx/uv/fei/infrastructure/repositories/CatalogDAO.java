package mx.uv.fei.infrastructure.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mx.uv.fei.infrastructure.database.DatabaseConnection;
import mx.uv.fei.infrastructure.exceptions.DAOException;

public class CatalogDAO {

    public Map<String, Integer> getActiveIdentificationTypes() throws DAOException {
        Map<String, Integer> catalogMap = new HashMap<>();
        
        String query = "SELECT id_identificacion, nombre FROM Cat_Identificacion WHERE activo = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_identificacion");
                String name = resultSet.getString("nombre");
                catalogMap.put(name, id);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al consultar el catálogo de identificaciones.", e);
        }

        return catalogMap;
    }

    public Map<String, Integer> getActiveHosts() throws DAOException {
        Map<String, Integer> catalogMap = new HashMap<>();
      
        String query = "SELECT id_anfitrion, nombre FROM Anfitrion WHERE activo = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                catalogMap.put(resultSet.getString("nombre"), resultSet.getInt("id_anfitrion"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el catálogo de anfitriones.", e);
        }
        return catalogMap;
    }

    public Map<String, Integer> getActiveEvidences() throws DAOException {
        Map<String, Integer> catalogMap = new HashMap<>();
        
        String query = "SELECT id_evidencia, nombre FROM Cat_Evidencia WHERE activo = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                catalogMap.put(resultSet.getString("nombre"), resultSet.getInt("id_evidencia"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el catálogo de evidencias.", e);
        }
        return catalogMap;
    }
}