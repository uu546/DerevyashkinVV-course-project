package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Inventory;
import project.warehouse.entity.Location;
import project.warehouse.entity.Product;
import java.util.List;
import java.util.Optional;

public interface IInventoryRepository {

    /**
     * Остаток продукта в ячейке.
     * @param product Продукт.
     * @param location Ячейка.
     * @return Остатки продукта в ячейке.
     */
    Optional<Inventory> findByProductAndLocation(Product product, Location location);
    Optional<Inventory> findById(Integer id);
    void deleteById(Integer id);
    void addQuantity(Integer productId, Integer locationId, Integer quantity);
    void subtractQuantity(Integer productId, Integer locationId, Integer quantity);
    List<Inventory> findAllWithDetails();
    Inventory save(Inventory inventory);
    int getTotalStockByProduct(Integer productId);
}