package src.repository;

import java.util.List;

public interface Repository<T> {

    T findById(int id);

    List<T> findAll();

    void save(T entity);
}