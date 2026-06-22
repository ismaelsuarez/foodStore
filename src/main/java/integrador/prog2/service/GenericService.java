package integrador.prog2.service;

import java.util.List;

public interface GenericService<T> {

    T guardar(T entidad);

    T buscarPorId(Long id);

    List<T> listar();

    T actualizar(T entidad);

    boolean eliminar(Long id);
}