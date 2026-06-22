package integrador.prog2.service;

import integrador.prog2.dao.ProductoDAO;
import integrador.prog2.entities.Producto;
import integrador.prog2.exception.ErrorAplicacion;

import java.util.List;

public class ServicioProducto {

    private final ProductoDAO productoDAO;

    public ServicioProducto() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Producto> listar() {
        return productoDAO.findAll();
    }

    public Producto crear(Producto producto) {
        if (producto.getPrecio() < 0 || producto.getStock() < 0) {
            throw new ErrorAplicacion("El precio y el stock deben ser mayores o iguales a 0.");
        }
        return productoDAO.save(producto);
    }

    public void editar(Producto producto) {
        if (producto.getPrecio() < 0 || producto.getStock() < 0) {
            throw new ErrorAplicacion("El precio y el stock deben ser mayores o iguales a 0.");
        }
        productoDAO.update(producto);
    }

    public boolean eliminar(Long id) {
        return productoDAO.deleteById(id);
    }
}
