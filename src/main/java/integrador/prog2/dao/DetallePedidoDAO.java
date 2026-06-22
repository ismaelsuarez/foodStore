package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.entities.Producto;
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

public class DetallePedidoDAO implements IBaseDAO<DetallePedido> {

    private static final String SELECT_BASE = """
            SELECT dp.id, dp.cantidad, dp.subtotal, dp.pedido_id, dp.producto_id,
                   dp.eliminado, dp.created_at,
                   p.nombre, p.precio, p.descripcion, p.stock, p.imagen, p.disponible, p.categoria_id
            FROM detalle_pedido dp
            INNER JOIN producto p ON dp.producto_id = p.id
            """;

    @Override
    public List<DetallePedido> findAll() {
        String sql = SELECT_BASE + " WHERE dp.eliminado = FALSE ORDER BY dp.id";
        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                detalles.add(crearDetalle(resultSet));
            }

            return detalles;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar los detalles.", e);
        }
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
        String sql = SELECT_BASE + " WHERE dp.id = ? AND dp.eliminado = FALSE";

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(crearDetalle(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo buscar el detalle.", e);
        }
    }

    public List<DetallePedido> findByPedidoId(Long pedidoId) {
        String sql = SELECT_BASE + " WHERE dp.pedido_id = ? AND dp.eliminado = FALSE ORDER BY dp.id";
        List<DetallePedido> detalles = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, pedidoId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    detalles.add(crearDetalle(resultSet));
                }
            }

            return detalles;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar los detalles del pedido.", e);
        }
    }

    @Override
    public DetallePedido save(DetallePedido detalle) {
        return saveForPedido(detalle.getPedidoId(), detalle);
    }

    public DetallePedido saveForPedido(Long pedidoId, DetallePedido detalle) {
        try (Connection connection = ConexionDB.getConexion()) {
            return saveForPedido(connection, pedidoId, detalle);
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo crear el detalle.", e);
        }
    }

    DetallePedido saveForPedido(Connection connection, Long pedidoId, DetallePedido detalle) throws SQLException {
        String sql = """
                INSERT INTO detalle_pedido (cantidad, subtotal, pedido_id, producto_id)
                VALUES (?, ?, ?, ?)
                """;

        validarDetalle(pedidoId, detalle);
        detalle.setPedidoId(pedidoId);

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, detalle.getCantidad());
            statement.setDouble(2, detalle.getSubtotal());
            statement.setLong(3, pedidoId);
            statement.setLong(4, detalle.getProducto().getId());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle.setId(generatedKeys.getLong(1));
                    return detalle;
                }
                throw new ErrorBaseDatos("No se obtuvo el id generado para el detalle.");
            }
        }
    }

    @Override
    public void update(DetallePedido detalle) {
        String sql = """
                UPDATE detalle_pedido
                SET cantidad = ?, subtotal = ?, producto_id = ?
                WHERE id = ? AND eliminado = FALSE
                """;

        validarDetalle(1L, detalle);

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, detalle.getCantidad());
            statement.setDouble(2, detalle.getSubtotal());
            statement.setLong(3, detalle.getProducto().getId());
            statement.setLong(4, detalle.getId());

            if (statement.executeUpdate() == 0) {
                throw new ErrorBaseDatos("No se actualizo ningun detalle.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo actualizar el detalle.", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                UPDATE detalle_pedido
                SET eliminado = TRUE
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo eliminar el detalle.", e);
        }
    }

    void deleteByPedidoId(Connection connection, Long pedidoId) throws SQLException {
        String sql = """
                UPDATE detalle_pedido
                SET eliminado = TRUE
                WHERE pedido_id = ? AND eliminado = FALSE
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pedidoId);
            statement.executeUpdate();
        }
    }

    private void validarDetalle(Long pedidoId, DetallePedido detalle) {
        if (pedidoId == null || detalle == null || detalle.getProducto() == null || detalle.getProducto().getId() == null) {
            throw new ErrorBaseDatos("El detalle debe tener pedido y producto.");
        }
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new ErrorBaseDatos("La cantidad del detalle debe ser mayor a cero.");
        }
    }

    private DetallePedido crearDetalle(ResultSet resultSet) throws SQLException {
        Producto producto = new Producto();
        producto.setId(resultSet.getLong("producto_id"));
        producto.setNombre(resultSet.getString("nombre"));
        producto.setPrecio(resultSet.getDouble("precio"));
        producto.setDescripcion(resultSet.getString("descripcion"));
        producto.setStock(resultSet.getInt("stock"));
        producto.setImagen(resultSet.getString("imagen"));
        producto.setDisponible(resultSet.getBoolean("disponible"));

        Categoria categoria = new Categoria();
        categoria.setId(resultSet.getLong("categoria_id"));
        producto.setCategoria(categoria);

        DetallePedido detalle = new DetallePedido();
        detalle.setId(resultSet.getLong("id"));
        detalle.setCantidad(resultSet.getInt("cantidad"));
        detalle.setSubtotal(resultSet.getDouble("subtotal"));
        detalle.setPedidoId(resultSet.getLong("pedido_id"));
        detalle.setProducto(producto);
        detalle.setEliminado(resultSet.getBoolean("eliminado"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            detalle.setCreatedAt(createdAt.toLocalDateTime());
        }

        return detalle;
    }
}
