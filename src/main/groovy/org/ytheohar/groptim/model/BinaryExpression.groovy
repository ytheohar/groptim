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

		if (left.solver == null && left.solver == right.solver) {
			throw new IllegalStateException("At least one operand needs to have solver set.")
		}
		if (left.solver != null && right.solver != null && left.solver != right.solver) {
			throw new IllegalStateException("The operand are not allowed to have different solvers set.")
		}
		this.solver = left.solver ?: right.solver
	}

	def fill(RealVector v, int sign) {
		fill(v, left, right, sign)
	}

	def fill(RealVector v, LPVar l, LPVar r, int sign) {
		v.setEntry(l.index, sign*1)
		v.setEntry(r.index, sign*val)
		0
	}

	def fill(RealVector v, NumberExpression l, LPVar r, int sign) {
		def newVal = sign*val*l.num
		v.setEntry(r.index, newVal)
		0
	}

	def fill(RealVector v, BinaryExpression l, LPVar r, int sign) {
		def newVal = sign*val
		l.fill(v, sign)
		v.setEntry(r.index, newVal)
		0
	}

	def fill(RealVector v, LPVar l, BinaryExpression r, int sign) {
		def newVal = sign*val
		v.setEntry(l.index, 1)
		r.fill(v, newVal)
	}

	def fill(RealVector v, BinaryExpression l, BinaryExpression r, int sign) {
		def newVal = sign*val
		l.fill(v, sign) + r.fill(v, newVal)
	}

	def fill(RealVector v, BinaryExpression l, NumberExpression r, int sign) {
		def newVal = sign*val
		l.fill(v, sign)
		newVal*r.num
	}

	@Override
	public String toString() {
		return '['+ left.toString() +op+ right.toString()+ ']';
	}
}
