package com.tessi.cxm.pfl.ms32.exception;

import lombok.Getter;

@Getter
public class DomainException extends Exception {
	
	private int code;

	public DomainException(String error, int code) {
		super(error);
		this.code = code;
	}

	public DomainException(String error) {
		super(error);
	}

}
