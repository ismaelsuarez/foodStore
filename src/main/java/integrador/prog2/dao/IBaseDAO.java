package integrador.prog2.dao;

import java.util.List;
import java.util.Optional;

public interface IBaseDAO<T> {

    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T entity);

    void update(T entity);

    boolean deleteById(Long id);
}
