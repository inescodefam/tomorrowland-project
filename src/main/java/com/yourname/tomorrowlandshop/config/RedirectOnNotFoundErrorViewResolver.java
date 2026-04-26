package com.yourname.tomorrowlandshop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class RedirectOnNotFoundErrorViewResolver implements ErrorViewResolver {

    private static final String PRODUCTS_REDIRECT = "redirect:/products";

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView(PRODUCTS_REDIRECT);
        }
        return null;
    }
}
