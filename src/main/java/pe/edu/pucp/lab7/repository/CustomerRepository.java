package pe.edu.pucp.lab7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.lab7.entity.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, Integer id);
}
