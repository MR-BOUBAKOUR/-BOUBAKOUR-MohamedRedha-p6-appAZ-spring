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
        logger.warn(ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transactions";
    }

    @ExceptionHandler(SelfAddContactException.class)
    public String handleSelfAddContactException(SelfAddContactException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/contacts";
    }

    @ExceptionHandler(ConflictException.class)
    public String handleConflictException(ConflictException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/accounts";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        if (ex.getMessage().equals("Bénéficiaire non trouvé.")) {
            return "redirect:/contacts";
        }
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error(String.valueOf(ex));
        redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
        return "redirect:/error";
    }
}
