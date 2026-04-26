package com.yourname.tomorrowlandshop.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class NotFoundRedirectAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound() {
        return new ModelAndView("redirect:/products");
    }
}
