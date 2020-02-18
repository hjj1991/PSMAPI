package com.psm.api.common.exception;

public class CCompanyNotFoundException extends RuntimeException {
	   public CCompanyNotFoundException(String msg, Throwable t) {
	        super(msg, t);
	    }
	     
	    public CCompanyNotFoundException(String msg) {
	        super(msg);
	    }
	     
	    public CCompanyNotFoundException() {
	        super();
	    }
	}