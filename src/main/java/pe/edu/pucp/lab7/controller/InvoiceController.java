package pe.edu.pucp.lab7.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.lab7.dto.InvoiceDetailForm;
import pe.edu.pucp.lab7.dto.InvoiceForm;
import pe.edu.pucp.lab7.repository.*;
import pe.edu.pucp.lab7.service.InvoiceService;

import java.time.LocalDate;

@Controller
@RequestMapping("/pedidos")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            InvoiceService invoiceService) {

        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", invoiceRepository.findAll());
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        InvoiceForm form = new InvoiceForm();

        form.setDate(LocalDate.now());
        form.getDetails().add(new InvoiceDetailForm());

        model.addAttribute("invoiceForm", form);
        cargarOpciones(model);

        return "pedidos/formulario";
    }

    // Añade una fila mediante POST, sin guardar el pedido.
    @PostMapping(value = "/guardar", params = "accion=agregar")
    public String agregar(
            @ModelAttribute("invoiceForm") InvoiceForm form,
            BindingResult bindingResult,
            Model model) {

        form.getDetails().add(new InvoiceDetailForm());
        cargarOpciones(model);

        return "pedidos/formulario";
    }

    @PostMapping(value = "/guardar", params = "accion=guardar")
    public String guardar(
            @Valid @ModelAttribute("invoiceForm") InvoiceForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!bindingResult.hasErrors()) {
            try {
                invoiceService.registrar(form, bindingResult);

            } catch (DataIntegrityViolationException exception) {
                bindingResult.reject(
                        "invoice.persistence",
                        "No se pudo registrar el pedido. Revisa los datos e inténtalo nuevamente."
                );
            }
        }

        if (bindingResult.hasErrors()) {
            cargarOpciones(model);
            return "pedidos/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Pedido registrado correctamente."
        );

        return "redirect:/pedidos";
    }

    private void cargarOpciones(Model model) {
        model.addAttribute("clientes", customerRepository.findAll());
        model.addAttribute("productos", productRepository.findAll());
    }
}
