package com.gym.crm.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {
    @GetMapping("/error-page")
    public String errorPage(HttpServletRequest request, Model model) {
        Object message = request.getAttribute("errorMessage");
        model.addAttribute("errorMessage", message != null ? message : "Невідома помилка");
        return "error-page"; // Thymeleaf шаблон error-page.html
    }
}
