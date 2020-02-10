package com.psm.api.common.exception;

public class PasswordNotMatchException extends RuntimeException {
	   public PasswordNotMatchException(String msg, Throwable t) {
	        super(msg, t);
	    }
	     
	    public PasswordNotMatchException(String msg) {
	        super(msg);
	    }
	     
	    public PasswordNotMatchException() {
	        super();
	    }
	}