package org.ytheohar.groptim.model

import org.apache.commons.math3.linear.RealVector
import org.ytheohar.groptim.LPSolver

class LPVar extends NumberExpression {
	String label
	double value
	LPVar(String label, LPSolver solver) {
		this.label = label
		this.solver = solver
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, int sign) {
		v.setEntry(vars[this], sign*1)
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
