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
import pe.edu.pucp.lab7.entity.Product;
import pe.edu.pucp.lab7.repository.ProductRepository;

@Controller
@RequestMapping("/productos")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productRepository.findAll());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("product", new Product());
        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Product product = buscarProducto(id);

        model.addAttribute("product", product);
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Alternativa implementada: messages.properties.
        // Spring registra los errores de conversión en BindingResult
        // y utiliza los mensajes typeMismatch personalizados.

        if (!bindingResult.hasFieldErrors("id")
                && product.getId() != null) {
            buscarProducto(product.getId());
        }

        if (!bindingResult.hasFieldErrors("name")) {
            boolean nombreRepetido;

            if (product.getId() == null) {
                nombreRepetido =
                        productRepository.existsByName(product.getName());

            } else if (!bindingResult.hasFieldErrors("id")) {
                nombreRepetido =
                        productRepository.existsByNameAndIdNot(
                                product.getName(),
                                product.getId()
                        );

            } else {
                nombreRepetido = false;
            }

            if (nombreRepetido) {
                bindingResult.rejectValue(
                        "name",
                        "name.duplicate",
                        "Ya existe un producto con este nombre."
                );
            }
        }

        if (bindingResult.hasErrors()) {
            return "productos/formulario";
        }

        try {
            productRepository.saveAndFlush(product);

        } catch (DataIntegrityViolationException exception) {
            bindingResult.rejectValue(
                    "name",
                    "name.conflict",
                    "No se pudo guardar. Verifica que el nombre no esté registrado."
            );

            return "productos/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Producto guardado correctamente."
        );

        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        buscarProducto(id);

        try {
            productRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Producto eliminado correctamente."
            );

        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se puede eliminar un producto que tiene pedidos asociados."
            );
        }

        return "redirect:/productos";
    }

    private Product buscarProducto(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado."
                ));
    }
}