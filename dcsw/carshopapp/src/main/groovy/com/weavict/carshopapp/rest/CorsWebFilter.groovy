package com.weavict.carshopapp.rest

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


/**
 * Created by Justin on 2018/6/10.
 */
@WebFilter(filterName = "corswebfilter", urlPatterns = "/r/*")
class CorsWebFilter implements Filter
{
    CorsWebFilter() {

    }

    @Override
    void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response,
                  FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        println "__________" + request.getHeader("origin");
        if (request.getHeader("origin") in ["http://127.0.0.1:35594","http://localhost:9999","http://192.168.0.153:9999","http://localhost:8081","http://m.arkydesign.cn","https://m.arkydesign.cn",
                                            "https://m.daxiabang.club","http://m.daxiabang.club","http://image.arkydesign.cn","https://image.arkydesign.cn"])
        {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        }
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if (httpServletRequest.getMethod().toUpperCase().equals("OPTIONS")==true)
        {
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "content-type, accept");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST");
            httpServletResponse.setStatus(200);
//            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setCharacterEncoding("utf-8");
//            responseContentByte="{\"test\":\"OPTIONS\"}".getBytes();
        }
        chain?.doFilter(request, response);
    }

    @Override
    void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}
