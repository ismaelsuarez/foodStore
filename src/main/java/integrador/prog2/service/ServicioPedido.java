package integrador.prog2.service;

import integrador.prog2.dao.PedidoDAO;
import integrador.prog2.entities.Pedido;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;

import java.util.List;

public class ServicioPedido implements GenericService<Pedido> {

    private final PedidoDAO pedidoDAO;

    public ServicioPedido() {
        this.pedidoDAO = new PedidoDAO();
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return pedidoDAO.save(pedido);
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return pedidoDAO.findById(id).orElse(null);
    }

    @Override
    public List<Pedido> listar() {
        return pedidoDAO.findAll();
    }

    @Override
    public Pedido actualizar(Pedido pedido) {
        pedidoDAO.update(pedido);
        return pedido;
    }

    @Override
    public boolean eliminar(Long id) {
        return pedidoDAO.deleteById(id);
    }

    public boolean actualizarEstado(Long id, Estado nuevoEstado) {
        Pedido pedido = buscarPorId(id);

        if (pedido == null) {
            return false;
        }

        pedido.setEstado(nuevoEstado);
        pedidoDAO.update(pedido);
        return true;
    }

    public boolean actualizarFormaPago(Long id, FormaPago nuevaFormaPago) {
        Pedido pedido = buscarPorId(id);

        if (pedido == null) {
            return false;
        }

        pedido.setFormaPago(nuevaFormaPago);
        pedidoDAO.update(pedido);
        return true;
    }
}