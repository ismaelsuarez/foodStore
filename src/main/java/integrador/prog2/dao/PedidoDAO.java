package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Pedido;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.exception.ErrorBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAO implements IBaseDAO<Pedido> {

    private static final String SELECT_BASE = """
            SELECT id, fecha, estado, total, forma_pago, eliminado, created_at
            FROM pedido
            """;

    @Override
    public List<Pedido> findAll() {
        String sql = SELECT_BASE + " WHERE eliminado = FALSE ORDER BY id";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                pedidos.add(crearPedido(resultSet));
            }

            return pedidos;

        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar los pedidos.", e);
        }
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        String sql = SELECT_BASE + " WHERE id = ? AND eliminado = FALSE";

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(crearPedido(resultSet));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo buscar el pedido.", e);
        }
    }

    @Override
    public Pedido save(Pedido pedido) {
        String sql = """
                INSERT INTO pedido (fecha, estado, total, forma_pago)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, Date.valueOf(pedido.getFecha()));
            statement.setString(2, pedido.getEstado().name());
            statement.setDouble(3, pedido.getTotal());
            statement.setString(4, pedido.getFormaPago().name());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getLong(1));
                    return pedido;
                }
                throw new ErrorBaseDatos("No se obtuvo el id generado para el pedido.");
            }

        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo crear el pedido.", e);
        }
    }

    @Override
    public void update(Pedido pedido) {
        String sql = """
                UPDATE pedido
                SET estado = ?, total = ?, forma_pago = ?
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pedido.getEstado().name());
            statement.setDouble(2, pedido.getTotal());
            statement.setString(3, pedido.getFormaPago().name());
            statement.setLong(4, pedido.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new ErrorBaseDatos("No se actualizó ningún pedido.");
            }

        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo actualizar el pedido.", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                UPDATE pedido
                SET eliminado = TRUE
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo eliminar el pedido.", e);
        }
    }

    private Pedido crearPedido(ResultSet resultSet) throws SQLException {
        FormaPago formaPago = FormaPago.valueOf(resultSet.getString("forma_pago"));

        Pedido pedido = new Pedido(formaPago);

        pedido.setId(resultSet.getLong("id"));
        pedido.setEstado(Estado.valueOf(resultSet.getString("estado")));
        pedido.setTotal(resultSet.getDouble("total"));
        pedido.setEliminado(resultSet.getBoolean("eliminado"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            pedido.setCreatedAt(createdAt.toLocalDateTime());
        }

        return pedido;
    }
}