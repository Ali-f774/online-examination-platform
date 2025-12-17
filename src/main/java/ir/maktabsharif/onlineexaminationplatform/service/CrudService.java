package ir.maktabsharif.onlineexaminationplatform.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CrudService<T> {

    T addOrUpdate(@NotNull T t);

    T findById(@Min(1) Long id);

    List<T> findAll();

    void deleteById(@Min(1) Long id);

}
