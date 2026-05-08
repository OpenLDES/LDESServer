package org.openldes.server.portconfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class AdminEndpointFilter extends OncePerRequestFilter {
    private final String adminPort;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEndpointFilter.class);
    public AdminEndpointFilter(String adminPort) {
        this.adminPort = adminPort;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        int requestPort = request.getLocalPort();
        if (requestPort != Integer.parseInt(adminPort)) {
            LOGGER.warn("Denying request from {} intended for admin API on port {} received on port {}", request.getRemoteHost(), adminPort, requestPort);
            response.setStatus(404);
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
