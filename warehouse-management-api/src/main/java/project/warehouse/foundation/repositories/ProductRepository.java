package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Product;
import project.warehouse.foundation.interfaces.IProductRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
@Repository
public class ProductRepository  implements IProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Product> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    @Override
    public List<Product> findAll() {
        String jpql = "SELECT p FROM Product p";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (id == null) {
            return;
        }
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    @Override
    @Transactional
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }
}