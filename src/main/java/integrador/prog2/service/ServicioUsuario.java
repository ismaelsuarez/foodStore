package integrador.prog2.service;

import integrador.prog2.dao.UsuarioDAO;
import integrador.prog2.entities.Usuario;
import integrador.prog2.exception.ErrorAplicacion;

import java.util.List;

public class ServicioUsuario {

    private final UsuarioDAO usuarioDAO;

    public ServicioUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> listar() {
        return usuarioDAO.findAll();
    }

    public Usuario crear(Usuario usuario) {
        if (usuario.getMail() == null || usuario.getMail().isBlank()) {
            throw new ErrorAplicacion("El mail no puede estar vacío.");
        }
        return usuarioDAO.save(usuario);
    }

    public void editar(Usuario usuario) {
        if (usuario.getMail() == null || usuario.getMail().isBlank()) {
            throw new ErrorAplicacion("El mail no puede estar vacío.");
        }
        usuarioDAO.update(usuario);
    }

    public boolean eliminar(Long id) {
        return usuarioDAO.deleteById(id);
    }
}
