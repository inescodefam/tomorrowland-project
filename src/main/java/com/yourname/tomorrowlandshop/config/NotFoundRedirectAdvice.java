package com.yourname.tomorrowlandshop.config;

import com.yourname.tomorrowlandshop.domain.exception.NotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class NotFoundRedirectAdvice {

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            NotFoundException.class
    })
    public ModelAndView handleNoHandlerFound() {
        return new ModelAndView("redirect:/products");
    }
}
