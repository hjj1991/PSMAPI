package com.psm.api.common.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	 
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        // status를 401 에러로 지정
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        Map<String, String> map = new HashMap<String, String>();
        
        
        map.put("code", "999"); //토큰이 만료되었거나 없으면 코드 999를 리턴한다. 
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(map); 

 
        PrintWriter out = response.getWriter();
        out.print(json);

    }
}

