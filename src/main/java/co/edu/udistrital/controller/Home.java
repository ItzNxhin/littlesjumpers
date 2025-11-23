package co.edu.udistrital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    @GetMapping("/")
    public String home() {
        return "html/main";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "html/admin/dashboard";
    }

    @GetMapping("/acudiente/menu")
    public String acudienteMenu() {
        return "html/acudiente/menu";
    }

    @GetMapping("/docente/menu")
    public String docenteMenu() {
        return "html/docente/menu";
    }
}
