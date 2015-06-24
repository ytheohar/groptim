package org.ytheohar.groptim.model;

public enum Operator {

	PLUS('+'), MINUS('-'), TIMES('*')

	String sign
	Operator(String sign) {
		this.sign = sign
	}

	@Override
	public String toString() {
		return sign
	}
}
