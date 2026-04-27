package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Product;
import java.util.Optional;

public interface IProductRepository {
    Optional<Product> findById(Integer id);
    void deleteById(Integer id);
    Product save(Product product);
}