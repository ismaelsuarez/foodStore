package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.ErrorBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAO implements IBaseDAO<Categoria> {

    private static final String SELECT_BASE = """
            SELECT id, nombre, descripcion, eliminado, created_at
            FROM categoria
            """;

    @Override
    public List<Categoria> findAll() {
        String sql = SELECT_BASE + " WHERE eliminado = FALSE ORDER BY id";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categorias.add(crearCategoria(resultSet));
            }
            return categorias;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar las categorias.", e);
        }
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        String sql = SELECT_BASE + " WHERE id = ? AND eliminado = FALSE";

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(crearCategoria(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo buscar la categoria.", e);
        }
    }

    @Override
    public Categoria save(Categoria categoria) {
        String sql = """
                INSERT INTO categoria (nombre, descripcion)
                VALUES (?, ?)
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, categoria.getNombre());
            statement.setString(2, categoria.getDescripcion());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getLong(1));
                    return categoria;
                }
                throw new ErrorBaseDatos("No se obtuvo el id generado para la categoria.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo crear la categoria.", e);
        }
    }

    @Override
    public void update(Categoria categoria) {
        String sql = """
                UPDATE categoria
                SET nombre = ?, descripcion = ?
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoria.getNombre());
            statement.setString(2, categoria.getDescripcion());
            statement.setLong(3, categoria.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new ErrorBaseDatos("No se actualizo ninguna categoria.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo actualizar la categoria.", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                UPDATE categoria
                SET eliminado = TRUE
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo eliminar la categoria.", e);
        }
    }

    public boolean existsByNombre(String nombre) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombre);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total") > 0;
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo validar el nombre de la categoria.", e);
        }
    }

    public int countProductosActivosByCategoriaId(Long categoriaId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM producto
                WHERE categoria_id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, categoriaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo consultar si la categoria tiene productos.", e);
        }
    }

    private Categoria crearCategoria(ResultSet resultSet) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(resultSet.getLong("id"));
        categoria.setNombre(resultSet.getString("nombre"));
        categoria.setDescripcion(resultSet.getString("descripcion"));
        categoria.setEliminado(resultSet.getBoolean("eliminado"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            categoria.setCreatedAt(createdAt.toLocalDateTime());
        }

        return categoria;
    }
}
