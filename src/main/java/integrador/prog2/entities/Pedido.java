package integrador.prog2.entities;

import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import integrador.prog2.entities.Calculable;

import java.time.LocalDate;
import java.util.ArrayList;

public class Pedido extends Base implements Calculable {

    private LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private ArrayList<DetallePedido> detallesPedido;

    public Pedido(FormaPago formaPago) {
        super();
        this.fecha = LocalDate.now();
        this.estado = Estado.CANCELADO;
        this.total = 0.0;
        this.formaPago = formaPago;
        this.detallesPedido = new ArrayList<>();
    }


    public DetallePedido addDetallePedido(Integer cantidad, Producto producto) {
        if (findDetallePedidoByProducto(producto) != null) {
            System.out.println("El producto ya existe en el pedido. No se permiten duplicados.");
            calcularTotal();
            validarPedido();
            return null;
        }

        DetallePedido detalle = new DetallePedido(cantidad, producto);

        if (Boolean.TRUE.equals(detalle.getValido())) {
            detallesPedido.add(detalle);
            this.estado = Estado.PENDIENTE;
        }

        calcularTotal();
        validarPedido();
        return detalle;
    }


    public DetallePedido addDetallePedido(Producto producto, Integer cantidad) {
        return addDetallePedido(cantidad, producto);
    }

    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        if (producto == null) {
            return null;
        }

        for (DetallePedido detalle : detallesPedido) {
            if (detalle.getProducto() != null && detalle.getProducto().equals(producto)) {
                return detalle;
            }
        }

        return null;
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        detallesPedido.removeIf(detalle ->
                detalle.getProducto() != null && detalle.getProducto().equals(producto)
        );

        calcularTotal();
        validarPedido();
    }

    public void deleteDetallePedidoById(Integer id) {
        if (id == null) {
            return;
        }

        detallesPedido.removeIf(detalle -> detalle.getId().equals(id.longValue()));

        calcularTotal();
        validarPedido();
    }

    private void validarPedido() {
        if (detallesPedido.isEmpty()) {
            this.estado = Estado.CANCELADO;
            return;
        }

        boolean tieneDetalleValido = false;

        for (DetallePedido detalle : detallesPedido) {
            if (Boolean.TRUE.equals(detalle.getValido())) {
                tieneDetalleValido = true;
                break;
            }
        }

        this.estado = tieneDetalleValido ? Estado.PENDIENTE : Estado.CANCELADO;
    }

    @Override
    public void calcularTotal() {
        double suma = 0.0;

        for (DetallePedido detalle : detallesPedido) {
            if (Boolean.TRUE.equals(detalle.getValido())) {
                suma += detalle.getSubtotal();
            }
        }

        this.total = suma;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public Double getTotal() {
        return total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public ArrayList<DetallePedido> getDetallesPedido() {
        return detallesPedido;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public Pedido actualizar(Pedido pedidoActualizado) {

        Pedido pedido = buscarPorId(pedidoActualizado.getId());

        if (pedido != null) {

            pedido.setEstado(pedidoActualizado.getEstado());
            pedido.setFormaPago(pedidoActualizado.getFormaPago());
            pedido.setTotal(pedidoActualizado.getTotal());

            return pedido;
        }

        return null;
    }
}
