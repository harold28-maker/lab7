package pe.edu.pucp.lab7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("titulo", "Clientes");
        model.addAttribute("descripcion",
                "Sección para registrar, consultar y actualizar clientes.");

        return "seccion";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("titulo", "Catálogo");
        model.addAttribute("descripcion",
                "Sección para consultar productos, precios y stock disponible.");

        return "seccion";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("titulo", "Pedidos");
        model.addAttribute("descripcion",
                "Sección para registrar y consultar pedidos de venta.");

        return "seccion";
    }
}