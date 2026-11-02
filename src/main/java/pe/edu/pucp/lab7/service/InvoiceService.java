package pe.edu.pucp.lab7.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pe.edu.pucp.lab7.dto.InvoiceDetailForm;
import pe.edu.pucp.lab7.dto.InvoiceForm;
import pe.edu.pucp.lab7.entity.*;
import pe.edu.pucp.lab7.repository.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InvoiceService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            InvoiceRepository invoiceRepository) {

        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void registrar(InvoiceForm form, BindingResult errors) {

        // No se consulta ni se guarda si falló el binding o @Valid.
        if (errors.hasErrors()) {
            return;
        }

        Customer customer = customerRepository
                .findById(form.getCustomerId())
                .orElse(null);

        if (customer == null) {
            errors.rejectValue(
                    "customerId",
                    "customer.notFound",
                    "El cliente seleccionado no existe."
            );
        } else {
            boolean facturaIncompatible =
                    "Factura".equals(form.getType())
                            && !"RUC".equals(customer.getDocumentType());

            boolean boletaIncompatible =
                    "Boleta".equals(form.getType())
                            && !"DNI".equals(customer.getDocumentType());

            if (facturaIncompatible || boletaIncompatible) {
                errors.reject(
                        "invoice.incompatible",
                        "Factura requiere un cliente con RUC y Boleta requiere un cliente con DNI."
                );
            }
        }

        Set<Integer> seleccionados = new HashSet<>();

        for (int i = 0; i < form.getDetails().size(); i++) {
            Integer productId = form.getDetails().get(i).getProductId();

            if (!seleccionados.add(productId)) {
                errors.rejectValue(
                        "details[" + i + "].productId",
                        "product.duplicate",
                        "No puedes repetir un producto en el pedido."
                );
            }
        }

        if (errors.hasErrors()) {
            return;
        }

        List<Product> productos = productRepository.findAllForUpdate(
                seleccionados.stream().sorted().toList()
        );

        Map<Integer, Product> productosPorId = new HashMap<>();

        for (Product product : productos) {
            productosPorId.put(product.getId(), product);
        }

        // Se validan TODAS las líneas antes de modificar registros.
        for (int i = 0; i < form.getDetails().size(); i++) {
            InvoiceDetailForm linea = form.getDetails().get(i);
            Product product = productosPorId.get(linea.getProductId());

            if (product == null) {
                errors.rejectValue(
                        "details[" + i + "].productId",
                        "product.notFound",
                        "El producto seleccionado no existe."
                );

            } else if (linea.getQuantity() > product.getStock()) {
                errors.rejectValue(
                        "details[" + i + "].quantity",
                        "quantity.stock",
                        "Stock insuficiente. Disponible: " + product.getStock()
                );
            }
        }

        if (errors.hasErrors()) {
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setType(form.getType());
        invoice.setDate(form.getDate());
        invoice.setCustomer(customer);

        for (InvoiceDetailForm linea : form.getDetails()) {
            Product product = productosPorId.get(linea.getProductId());

            InvoiceDetail detail = new InvoiceDetail();
            detail.setProduct(product);
            detail.setQuantity(linea.getQuantity());
            detail.setPrice(product.getPrice());

            // Cálculo realizado en Java con el precio de la base de datos.
            detail.setSubtotal(
                    linea.getQuantity() * product.getPrice()
            );

            invoice.addDetail(detail);

            product.setStock(
                    product.getStock() - linea.getQuantity()
            );
        }

        // CascadeType.ALL guarda también los detalles.
        invoiceRepository.saveAndFlush(invoice);
    }
}