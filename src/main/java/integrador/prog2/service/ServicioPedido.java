package integrador.prog2.service;

import integrador.prog2.dao.ProductoDAO;
import integrador.prog2.dao.UsuarioDAO;
import integrador.prog2.entities.DetallePedido;
import integrador.prog2.dao.PedidoDAO;
import integrador.prog2.entities.Pedido;
import integrador.prog2.entities.Producto;
import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.exception.ErrorAplicacion;

import java.util.List;

public class ServicioPedido implements GenericService<Pedido> {

    private final PedidoDAO pedidoDAO;
    private final ProductoDAO productoDAO;
    private final UsuarioDAO usuarioDAO;

    public ServicioPedido() {
        this(new PedidoDAO(), new ProductoDAO(), new UsuarioDAO());
    }

    public ServicioPedido(PedidoDAO pedidoDAO, ProductoDAO productoDAO, UsuarioDAO usuarioDAO) {
        this.pedidoDAO = pedidoDAO;
        this.productoDAO = productoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        validarPedido(pedido);
        return pedidoDAO.save(pedido);
    }

    public Pedido crearPedido(Long usuarioId, FormaPago formaPago, List<Long> productosId, List<Integer> cantidades) {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new ErrorAplicacion("No existe un usuario activo con id " + usuarioId + "."));

        if (formaPago == null) {
            throw new ErrorAplicacion("La forma de pago es obligatoria.");
        }

        if (productosId == null || cantidades == null || productosId.size() != cantidades.size() || productosId.isEmpty()) {
            throw new ErrorAplicacion("El pedido debe tener al menos un detalle.");
        }

        Pedido pedido = new Pedido(usuario, formaPago);

        for (int i = 0; i < productosId.size(); i++) {
            Long productoId = productosId.get(i);
            Producto producto = productoDAO.findById(productoId)
                    .orElseThrow(() -> new ErrorAplicacion("No existe un producto activo con id " + productoId + "."));

            if (!Boolean.TRUE.equals(producto.getDisponible())) {
                throw new ErrorAplicacion("El producto " + producto.getNombre() + " no esta disponible.");
            }

            DetallePedido detalle = pedido.addDetallePedido(cantidades.get(i), producto);
            if (detalle == null || !Boolean.TRUE.equals(detalle.getValido())) {
                throw new ErrorAplicacion("No se pudo agregar el producto " + producto.getNombre() + " al pedido.");
            }
        }

        return guardar(pedido);
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
        if (id == null || nuevoEstado == null) {
            return false;
        }

        Pedido pedido = buscarPorId(id);

        if (pedido == null) {
            return false;
        }

        pedido.setEstado(nuevoEstado);
        pedidoDAO.update(pedido);
        return true;
    }

    public boolean actualizarFormaPago(Long id, FormaPago nuevaFormaPago) {
        if (id == null || nuevaFormaPago == null) {
            return false;
        }

        Pedido pedido = buscarPorId(id);

        if (pedido == null) {
            return false;
        }

        pedido.setFormaPago(nuevaFormaPago);
        pedidoDAO.update(pedido);
        return true;
    }

    private void validarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new ErrorAplicacion("El pedido es obligatorio.");
        }

        if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
            throw new ErrorAplicacion("El pedido debe tener un usuario.");
        }

        if (pedido.getDetallesPedido().isEmpty()) {
            throw new ErrorAplicacion("El pedido debe tener al menos un detalle.");
        }
    }
}
