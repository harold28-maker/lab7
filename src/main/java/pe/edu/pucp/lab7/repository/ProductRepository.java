package pe.edu.pucp.lab7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.lab7.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}