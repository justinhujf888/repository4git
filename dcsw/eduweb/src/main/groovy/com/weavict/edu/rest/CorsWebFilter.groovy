package com.weavict.edu.rest

import cn.hutool.core.io.IoUtil
//import org.apache.coyote.http11.upgrade.servlet31.ReadListener

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse


/**
 * Created by Justin on 2018/6/10.
 */
//@WebFilter(filterName = "corswebfilter", urlPatterns = "/r/*")
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
//        println IoUtil.read(httpServletRequest.getInputStream());

//        if (request.getHeader("token")!=null)
//        {
//            DES crypt = new DES(OtherUtils.givePropsValue("publickey"));
//            println URLDecoder.decode(crypt.decrypt(request.getHeader("token")),"utf-8");
//        }
        println "========================filter===================";
        if (request.getHeader("origin") in ["http://localhost:5173"])
        {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        }
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if (httpServletRequest.getMethod().toUpperCase().equals("OPTIONS")==true)
        {
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "content-type, accept, token");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST");
            httpServletResponse.setStatus(200);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setCharacterEncoding("utf-8");
//            responseContentByte="{\"test\":\"OPTIONS\"}".getBytes();
        }

//        println "v";
//        ServletRequest requestWrapper = new MyHttpServletRequestWrapper(request as HttpServletRequest);
//        println "a";
//        requestWrapper.setParameter("status","OK");
//        println ".........................................................";
//        chain?.doFilter(requestWrapper,response);

        chain?.doFilter(request, response);
    }

    @Override
    void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}

class MyHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    final Map<String,String[]> parameterMap;

    MyHttpServletRequestWrapper(HttpServletRequestWrapper request)
    {
        super(request);
        println "MyHttpServletRequestWrapper";
        parameterMap = new HashMap<>(request.getParameterMap());
    }

    @Override
    String[] getParameterValues(String parameter)
    {
        String[] results = super.getParameterValues(parameter);
        if (results == null)
            return null;
        int count = results.length;
        String[] trimResults = new String[count];
        for (int i = 0; i < count; i++) {
            trimResults[i] = results[i].trim();
        }
        return trimResults;
    }

    void setParameter(String name,String...value)
    {
        parameterMap.put(name,value);
    }
}
