package org.openldes.server.portconfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class UrlExcludingFilter extends OncePerRequestFilter {
    protected List<String> excludedUrls;

    protected UrlExcludingFilter() {
        super();
        excludedUrls = new ArrayList<>();
    }

    public void addExcludedUrl(String url) {
        excludedUrls.add(url);
    }
    public void addExcludedUrls(List<String> urls) {
        excludedUrls.addAll(urls);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return excludedUrls.stream().map(url->url.replace("/*", "")).anyMatch(path::contains);
    }

}
