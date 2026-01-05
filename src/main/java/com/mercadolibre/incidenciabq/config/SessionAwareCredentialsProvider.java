package com.mercadolibre.incidenciabq.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.mercadolibre.incidenciabq.service.SessionCredentialsManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

/**
 * Request-scoped bean that provides credentials for the current HTTP session
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionAwareCredentialsProvider {

    @Autowired(required = false)
    private HttpSession httpSession;

    @Autowired
    private SessionCredentialsManager sessionCredentialsManager;

    @Autowired
    private BigQueryConfig bigQueryConfig;

    /**
     * Get credentials for the current session
     * @return GoogleCredentials for the current session or default credentials
     * @throws IOException if credentials cannot be loaded
     */
    public GoogleCredentials getCredentials() throws IOException {
        if (httpSession != null) {
            return sessionCredentialsManager.getCredentials(
                httpSession,
                bigQueryConfig.getServiceAccountKeyPath()
            );
        } else {
            // No session (e.g., background jobs), use default
            return sessionCredentialsManager.getCredentials(
                null,
                bigQueryConfig.getServiceAccountKeyPath()
            );
        }
    }

    /**
     * Check if the current session has custom credentials
     * @return true if session has custom credentials, false otherwise
     */
    public boolean hasSessionCredentials() {
        if (httpSession != null) {
            return sessionCredentialsManager.hasSessionCredentials(httpSession);
        }
        return false;
    }

    /**
     * Get the session ID
     * @return session ID or "NO_SESSION" if no session
     */
    public String getSessionId() {
        if (httpSession != null) {
            return sessionCredentialsManager.getSessionCredentialsId(httpSession);
        }
        return "NO_SESSION";
    }
}

