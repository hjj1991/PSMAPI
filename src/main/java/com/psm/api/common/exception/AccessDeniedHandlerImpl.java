package com.psm.api.common.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AccessDeniedHandlerImpl extends org.springframework.security.web.access.AccessDeniedHandlerImpl {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FOUND);
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().print("{\"result:\" : \"하이루\"");
//        response.getWriter().flush();
		response.getWriter().write("Access Denied... Forbidden");
        System.out.println("아오!!!!!하이룽!!");

    }

}
