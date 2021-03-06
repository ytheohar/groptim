package org.ytheohar.groptim.model

import groovy.transform.TupleConstructor

import org.apache.commons.math3.linear.RealVector

/**
 * Models a constant number
 *
 * @author Yannis Theocharis
 */
@TupleConstructor
class NumberExpression extends Expression {
	double num

	def negative() {
		new NumberExpression(-num)
	}

	def fill(RealVector v, int sign) {
		sign*num
	}

	@Override
	public String toString() {
		num
	}
}
