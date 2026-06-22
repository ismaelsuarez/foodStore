package integrador.prog2.entities;

public class DetallePedido extends Base {

    private Integer cantidad;
    private Double subtotal;
    private Boolean valido;
    private Producto producto;
    private Long pedidoId;

    public DetallePedido() {
        super();
        this.subtotal = 0.0;
        this.valido = true;
    }

    public DetallePedido(Integer cantidad, Producto producto) {
        super();
        this.cantidad = cantidad;
        this.producto = producto;
        this.subtotal = 0.0;
        this.valido = false;
        validarProducto();
    }

    public DetallePedido(Producto producto, Integer cantidad) {
        this(cantidad, producto);
    }

    private void validarProducto() {
        if (producto == null) {
            System.out.println("No se puede crear un detalle sin producto.");
            this.valido = false;
            this.subtotal = 0.0;
            return;
        }

        if (producto.validarVenta(cantidad)) {
            calcularSubtotal();
            this.valido = true;
        } else {
            System.out.println("El detalle no es válido para el producto '" + producto.getNombre() + "'.");
            this.valido = false;
            this.subtotal = 0.0;
        }
    }

    private void calcularSubtotal() {
        this.subtotal = cantidad * producto.getPrecio();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Boolean getValido() {
        return valido;
    }

    public Producto getProducto() {
        return producto;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + getId() +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                ", valido=" + valido +
                ", pedidoId=" + pedidoId +
                ", producto=" + producto +
                '}';
    }
}
