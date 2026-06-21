package integrador.prog2.service;

import integrador.prog2.entities.Pedido;

import java.util.ArrayList;
import java.util.List;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;

public class ServicioPedido implements GenericService<Pedido> {

    private final List<Pedido> pedidos;

    public ServicioPedido() {
        this.pedidos = new ArrayList<>();
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        pedidos.add(pedido);
        return pedido;
    }

    @Override
    public Pedido buscarPorId(Long id) {

        for (Pedido pedido : pedidos) {
            if (pedido.getId().equals(id) && !pedido.isEliminado()) {
                return pedido;
            }
        }

        return null;
    }

    @Override
    public List<Pedido> listar() {

        List<Pedido> activos = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            if (!pedido.isEliminado()) {
                activos.add(pedido);
            }
        }

        return activos;
    }

    @Override
    public Pedido actualizar(Pedido pedidoActualizado) {

        Pedido pedido = buscarPorId(pedidoActualizado.getId());

        if (pedido != null) {
            return pedidoActualizado;
        }

        return null;
    }

    @Override
    public boolean eliminar(Long id) {

        Pedido pedido = buscarPorId(id);

        if (pedido != null) {
            pedido.setEliminado(true);
            return true;
        }

        return false;
    }

    public boolean actualizarEstado(Long id, Estado nuevoEstado) {

        Pedido pedido = buscarPorId(id);

        if (pedido != null) {
            pedido.setEstado(nuevoEstado);
            return true;
        }

        return false;
    }

    public boolean actualizarFormaPago(Long id, FormaPago nuevaFormaPago) {

        Pedido pedido = buscarPorId(id);

        if (pedido != null) {
            pedido.setFormaPago(nuevaFormaPago);
            return true;
        }

        return false;
    }
}