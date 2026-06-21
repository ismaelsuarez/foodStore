package integrador.prog2.service;

import integrador.prog2.dao.CategoriaDAO;
import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.ErrorAplicacion;

import java.util.List;

public class ServicioCategoria {

    private final CategoriaDAO categoriaDAO;

    public ServicioCategoria() {
        this(new CategoriaDAO());
    }

    public ServicioCategoria(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public List<Categoria> listar() {
        return categoriaDAO.findAll();
    }

    public Categoria buscarPorId(Long id) {
        validarId(id);
        return categoriaDAO.findById(id)
                .orElseThrow(() -> new ErrorAplicacion("No existe una categoria activa con id " + id + "."));
    }

    public Categoria crear(String nombre, String descripcion) {
        String nombreLimpio = validarNombre(nombre);
        String descripcionLimpia = limpiarTexto(descripcion);

        if (categoriaDAO.existsByNombre(nombreLimpio)) {
            throw new ErrorAplicacion("Ya existe una categoria con ese nombre.");
        }

        Categoria categoria = new Categoria(nombreLimpio, descripcionLimpia);
        return categoriaDAO.save(categoria);
    }

    public Categoria actualizar(Long id, String nombre, String descripcion) {
        Categoria categoria = buscarPorId(id);

        String nombreLimpio = limpiarTexto(nombre);
        if (nombreLimpio != null && !mismoTexto(categoria.getNombre(), nombreLimpio)) {
            if (categoriaDAO.existsByNombre(nombreLimpio)) {
                throw new ErrorAplicacion("Ya existe una categoria con ese nombre.");
            }
            categoria.setNombre(nombreLimpio);
        }

        String descripcionLimpia = limpiarTexto(descripcion);
        if (descripcionLimpia != null) {
            categoria.setDescripcion(descripcionLimpia);
        }

        categoriaDAO.update(categoria);
        return categoria;
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);

        if (categoriaDAO.countProductosActivosByCategoriaId(categoria.getId()) > 0) {
            throw new ErrorAplicacion("No se puede eliminar la categoria porque tiene productos activos asociados.");
        }

        boolean eliminada = categoriaDAO.deleteById(categoria.getId());
        if (!eliminada) {
            throw new ErrorAplicacion("No existe una categoria activa con id " + id + ".");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ErrorAplicacion("El id debe ser un numero positivo.");
        }
    }

    private String validarNombre(String nombre) {
        String nombreLimpio = limpiarTexto(nombre);
        if (nombreLimpio == null) {
            throw new ErrorAplicacion("El nombre de la categoria es obligatorio.");
        }
        return nombreLimpio;
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        String textoLimpio = texto.trim();
        return textoLimpio.isEmpty() ? null : textoLimpio;
    }

    private boolean mismoTexto(String texto1, String texto2) {
        return texto1 != null && texto2 != null && texto1.equalsIgnoreCase(texto2);
    }
}
