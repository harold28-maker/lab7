package pe.edu.pucp.lab7.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.pucp.lab7.entity.Product;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select p from Product p
            where p.id in :ids
            order by p.id
            """)
    List<Product> findAllForUpdate(@Param("ids") List<Integer> ids);
}