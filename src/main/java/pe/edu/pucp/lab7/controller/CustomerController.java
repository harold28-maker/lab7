package pe.edu.pucp.lab7.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.lab7.entity.Customer;
import pe.edu.pucp.lab7.repository.CustomerRepository;

@Controller
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", customerRepository.findAll());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("customer", new Customer());
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Customer customer = buscarCliente(id);

        model.addAttribute("customer", customer);
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Si es una edición, el cliente debe existir.
        if (!bindingResult.hasFieldErrors("id")
                && customer.getId() != null) {
            buscarCliente(customer.getId());
        }

        // La longitud del documento depende del tipo seleccionado.
        if (!bindingResult.hasFieldErrors("document")
                && !bindingResult.hasFieldErrors("documentType")) {

            if ("DNI".equals(customer.getDocumentType())
                    && customer.getDocument().length() != 8) {

                bindingResult.rejectValue(
                        "document",
                        "document.dni",
                        "El DNI debe contener exactamente 8 dígitos."
                );
            }

            if ("RUC".equals(customer.getDocumentType())
                    && customer.getDocument().length() != 11) {

                bindingResult.rejectValue(
                        "document",
                        "document.ruc",
                        "El RUC debe contener exactamente 11 dígitos."
                );
            }
        }

        // Al editar, se excluye el documento del propio cliente.
        if (!bindingResult.hasErrors()) {
            boolean documentoRepetido;

            if (customer.getId() == null) {
                documentoRepetido =
                        customerRepository.existsByDocument(
                                customer.getDocument()
                        );
            } else {
                documentoRepetido =
                        customerRepository.existsByDocumentAndIdNot(
                                customer.getDocument(),
                                customer.getId()
                        );
            }

            if (documentoRepetido) {
                bindingResult.rejectValue(
                        "document",
                        "document.duplicate",
                        "Ya existe un cliente con este documento."
                );
            }
        }

        if (bindingResult.hasErrors()) {
            return "clientes/formulario";
        }

        try {
            customerRepository.saveAndFlush(customer);

        } catch (DataIntegrityViolationException exception) {
            // También controla una duplicación detectada por MySQL.
            bindingResult.rejectValue(
                    "document",
                    "document.conflict",
                    "No se pudo guardar. Verifica que el documento no esté registrado."
            );

            return "clientes/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cliente guardado correctamente."
        );

        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        buscarCliente(id);

        try {
            customerRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Cliente eliminado correctamente."
            );

        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se puede eliminar un cliente que tiene pedidos asociados."
            );
        }

        return "redirect:/clientes";
    }

    private Customer buscarCliente(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado."
                ));
    }
}