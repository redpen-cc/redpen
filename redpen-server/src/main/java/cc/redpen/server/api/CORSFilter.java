package cc.redpen.server.api;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows CORS (Cross-Origin AJAX requests), so that RedPen API can be used from a different domain
 */
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
            response.setHeader("Access-Control-Max-Age", "1728000");

            if (request.getMethod().equals("OPTIONS")) return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() {
    }

    @Override public void init(FilterConfig filterConfig) throws ServletException {
    }
}