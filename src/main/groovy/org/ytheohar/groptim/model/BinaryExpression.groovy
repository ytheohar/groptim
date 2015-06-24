package org.ytheohar.groptim.model

import org.apache.commons.math3.linear.RealVector

class BinaryExpression extends NumberExpression {
	Expression left
	Expression right
	Operator op
	def val = op==Operator.MINUS ? -1 : 1

	BinaryExpression(Expression left, Expression right, Operator op) {
		if (op == Operator.TIMES && left.class != NumberExpression && right.class != NumberExpression) {
			throw new IllegalStateException("Non linear expressions not allowed.")
		}

		this.left = left
		this.right = right
		this.op = op
		val = op==Operator.MINUS ? -1 : 1
		
		if (left.solver) {
			this.solver = left.solver
		} else {
			this.solver = right.solver
		}
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, int sign) {
		fill(v, vars, left, right, sign)
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, LPVar l, LPVar r, int sign) {
		v.setEntry(vars[l], sign*1)
		v.setEntry(vars[r], sign*val)
		0
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, NumberExpression l, LPVar r, int sign) {
		def newVal = sign*val*l.num
		v.setEntry(vars[r], newVal)
		0
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, BinaryExpression l, LPVar r, int sign) {
		def newVal = sign*val
		l.fill(v, vars, sign)
		v.setEntry(vars[r], newVal)
		0
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, LPVar l, BinaryExpression r, int sign) {
		def newVal = sign*val
		v.setEntry(vars[l], 1)
		r.fill(v, vars, newVal)
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, BinaryExpression l, BinaryExpression r, int sign) {
		def newVal = sign*val
		def c = l.fill(v, vars, sign)
		c += r.fill(v, vars, newVal)
	}

	def fill(RealVector v, Map<LPVar, Integer> vars, BinaryExpression l, NumberExpression r, int sign) {
		def newVal = sign*val
		l.fill(v, vars, sign)
		newVal*r.num
	}

	@Override
	public String toString() {
		return '['+ left.toString() +op+ right.toString()+ ']';
	}

}
