package cc.redpen.server.api;

import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

class CORSFilterTest {
    private CORSFilter filter = new CORSFilter();
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private FilterChain chain = mock(FilterChain.class);

    @Test
    void doNothingForRegularRequests() throws Exception {
        filter.doFilter(request, response, chain);
        verify(response, never()).addHeader(anyString(), anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    void addAllowOriginForCORSRequests() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://blah.com");
        filter.doFilter(request, response, chain);
        verify(response).addHeader("Access-Control-Allow-Origin", "http://blah.com");
        verify(chain).doFilter(request, response);
    }

    @Test
    void doNotGenerateResponseForOPTIONSRequests() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeader("Origin")).thenReturn("http://blah.com");
        filter.doFilter(request, response, chain);
        verify(response).addHeader("Access-Control-Allow-Origin", "http://blah.com");
        verify(chain, never()).doFilter(request, response);
    }
}