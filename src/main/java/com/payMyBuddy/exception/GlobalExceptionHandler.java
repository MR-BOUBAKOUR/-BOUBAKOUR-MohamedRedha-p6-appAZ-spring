package com.payMyBuddy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalanceException(InsufficientBalanceException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Transaction impossible - solde insuffisant: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transactions";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.error("Resource not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Erreur interne inattendue", ex);
        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
        return "redirect:/error";
    }
}
