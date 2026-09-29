package vn.edu.hcmute.uteshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Controller
public class PageErrorController {
    @GetMapping("/help")
    public String help() {
        return "help";
    }
}

@ControllerAdvice
class PageExceptionAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(IllegalArgumentException error, Model model) {
        model.addAttribute("message", error.getMessage());
        return "error";
    }
}
