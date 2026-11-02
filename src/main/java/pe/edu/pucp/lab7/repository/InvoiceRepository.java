package pe.edu.pucp.lab7.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.lab7.entity.Invoice;

import java.util.List;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Integer> {

    @Override
    @EntityGraph(attributePaths = {
            "customer",
            "details",
            "details.product"
    })
    List<Invoice> findAll();
}