package integrador.prog2.entities;

import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;
import java.time.LocalDate;
import java.util.ArrayList;

public class Pedido extends Base implements Calculable {

    private LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private ArrayList<DetallePedido> detallesPedido;

    public Pedido(FormaPago formaPago) {
        super();
        this.fecha = LocalDate.now();
        this.estado = Estado.CANCELADO;
        this.total = 0.0;
        this.formaPago = formaPago;
        this.detallesPedido = new ArrayList<>();
    }

    public Pedido(Usuario usuario, FormaPago formaPago) {
        this(formaPago);
        this.usuario = usuario;
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

        detallesPedido.removeIf(detalle -> detalle.getId() != null && detalle.getId().equals(id.longValue()));

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

    public Usuario getUsuario() {
        return usuario;
    }

    public ArrayList<DetallePedido> getDetallesPedido() {
        return detallesPedido;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + getId() +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", total=" + total +
                ", formaPago=" + formaPago +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                ", detalles=" + detallesPedido.size() +
                '}';
    }
}

