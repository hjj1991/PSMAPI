package com.psm.api.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.psm.api.common.response.CommonResult;
import com.psm.api.common.response.service.ResponseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {
 
    private final ResponseService responseService;
 
//    @ExceptionHandler(Exception.class) 
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
//        return responseService.getFailResult();
//    }
    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("-1")), getMessage("사용자가 존재하지 않습니다."));
    }
    
    @ExceptionHandler(CCompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult companyNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("-1")), getMessage("사용자가 존재하지 않습니다."));
    }
    
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult passwordNotMatchExcepiton(HttpServletRequest request, PasswordNotMatchException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("-1")), getMessage("비밀번호가 일치하지 않습니다."));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
    	return responseService.getFailResult(Integer.valueOf(getMessage("-1")), getMessage("토큰이 만료됏삼."));
    }

private String getMessage(String string) {
	// TODO Auto-generated method stub
	return string;
}
    
    
}