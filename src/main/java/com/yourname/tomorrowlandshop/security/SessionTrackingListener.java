package com.yourname.tomorrowlandshop.security;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SessionTrackingListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTrackingListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOGGER.info("Session created: {}", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOGGER.info("Session destroyed: {}", se.getSession().getId());
    }
}
