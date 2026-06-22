package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.ErrorBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAO implements IBaseDAO<Producto> {

    private static final String SELECT_BASE = """
            SELECT id, nombre, precio, descripcion, stock, imagen, disponible, categoria_id, eliminado, created_at
            FROM producto
            """;

    @Override
    public List<Producto> findAll() {
        String sql = SELECT_BASE + " WHERE eliminado = FALSE ORDER BY id";
        List<Producto> productos = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productos.add(crearProducto(resultSet));
            }
            return productos;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar los productos.", e);
        }
    }

    @Override
    public Optional<Producto> findById(Long id) {
        String sql = SELECT_BASE + " WHERE id = ? AND eliminado = FALSE";

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(crearProducto(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo buscar el producto.", e);
        }
    }

    @Override
    public Producto save(Producto producto) {
        String sql = """
                INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, producto.getNombre());
            statement.setDouble(2, producto.getPrecio());
            statement.setString(3, producto.getDescripcion());
            statement.setInt(4, producto.getStock());
            statement.setString(5, producto.getImagen());
            statement.setBoolean(6, producto.getDisponible());
            statement.setLong(7, producto.getCategoria().getId());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    producto.setId(generatedKeys.getLong(1));
                    return producto;
                }
                throw new ErrorBaseDatos("No se obtuvo el id generado para el producto.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo crear el producto.", e);
        }
    }

    @Override
    public void update(Producto producto) {
        String sql = """
                UPDATE producto
                SET nombre = ?, precio = ?, descripcion = ?, stock = ?, imagen = ?, disponible = ?, categoria_id = ?
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, producto.getNombre());
            statement.setDouble(2, producto.getPrecio());
            statement.setString(3, producto.getDescripcion());
            statement.setInt(4, producto.getStock());
            statement.setString(5, producto.getImagen());
            statement.setBoolean(6, producto.getDisponible());
            statement.setLong(7, producto.getCategoria().getId());
            statement.setLong(8, producto.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new ErrorBaseDatos("No se actualizó ningún producto.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo actualizar el producto.", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                UPDATE producto
                SET eliminado = TRUE
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo eliminar el producto.", e);
        }
    }

    private Producto crearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setStock(rs.getInt("stock"));
        producto.setImagen(rs.getString("imagen"));
        producto.setDisponible(rs.getBoolean("disponible"));
        producto.setEliminado(rs.getBoolean("eliminado"));

        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("categoria_id"));
        producto.setCategoria(categoria);

        return producto;
    }
}
