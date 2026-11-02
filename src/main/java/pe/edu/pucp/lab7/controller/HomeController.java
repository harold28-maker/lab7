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

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("titulo", "Pedidos");
        model.addAttribute("descripcion",
                "Sección para registrar y consultar pedidos de venta.");

        return "seccion";
    }
}