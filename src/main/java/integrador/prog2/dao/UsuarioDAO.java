package integrador.prog2.dao;

import integrador.prog2.config.ConexionDB;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
import integrador.prog2.exception.ErrorBaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IBaseDAO<Usuario> {

    private static final String SELECT_BASE = """
            SELECT id, nombre, apellido, mail, celular, contrasenia, rol, eliminado, created_at
            FROM usuario
            """;

    @Override
    public List<Usuario> findAll() {
        String sql = SELECT_BASE + " WHERE eliminado = FALSE ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(crearUsuario(resultSet));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudieron listar los usuarios.", e);
        }
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        String sql = SELECT_BASE + " WHERE id = ? AND eliminado = FALSE";

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(crearUsuario(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo buscar el usuario.", e);
        }
    }

    @Override
    public Usuario save(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (nombre, apellido, mail, celular, contrasenia, rol)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getMail());
            statement.setString(4, usuario.getCelular());
            statement.setString(5, usuario.getContrasena());
            statement.setString(6, usuario.getRol().name());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                    return usuario;
                }
                throw new ErrorBaseDatos("No se obtuvo el id generado para el usuario.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo crear el usuario.", e);
        }
    }

    @Override
    public void update(Usuario usuario) {
        String sql = """
                UPDATE usuario
                SET nombre = ?, apellido = ?, mail = ?, celular = ?, contrasenia = ?, rol = ?
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getMail());
            statement.setString(4, usuario.getCelular());
            statement.setString(5, usuario.getContrasena());
            statement.setString(6, usuario.getRol().name());
            statement.setLong(7, usuario.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new ErrorBaseDatos("No se actualizó ningún usuario.");
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                UPDATE usuario
                SET eliminado = TRUE
                WHERE id = ? AND eliminado = FALSE
                """;

        try (Connection connection = ConexionDB.getConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo eliminar el usuario.", e);
        }
    }

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setMail(rs.getString("mail"));
        usuario.setCelular(rs.getString("celular"));
        usuario.setContrasena(rs.getString("contrasenia"));
        usuario.setRol(Rol.valueOf(rs.getString("rol")));
        usuario.setEliminado(rs.getBoolean("eliminado"));
        return usuario;
    }
}
