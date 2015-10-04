package org.ytheohar.groptim.model

import groovy.transform.EqualsAndHashCode

import org.apache.commons.math3.linear.RealVector
import org.ytheohar.groptim.LPSolver

@EqualsAndHashCode(includes = 'label')
class LPVar extends NumberExpression {
	String label
	double value
	int index
	LPVar(String label, int index, LPSolver solver) {
		this.label = label
		this.solver = solver
		this.index = index
	}

	def fill(RealVector v, int sign) {
		v.setEntry(index, sign*1)
		0
	}

	def negative() {
		new BinaryExpression(new NumberExpression(-1), this, Operator.TIMES)
	}

	@Override
	public String toString() {
		return label
	}
}
