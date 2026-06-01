package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Product;
import java.util.List;
import java.util.Optional;

public interface IProductRepository {
    Optional<Product> findById(Integer id);
    void deleteById(Integer id);
    List<Product> findAll();
    Product save(Product product);
}