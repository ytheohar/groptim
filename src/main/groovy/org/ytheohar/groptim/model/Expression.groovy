package org.ytheohar.groptim.model

import org.ytheohar.groptim.LPSolver


class Expression implements Comparable<Expression> {

	LPSolver solver

	def plus(Expression e) {
		new BinaryExpression(this, e, Operator.PLUS)
	}

	def minus(Expression e) {
		new BinaryExpression(this, e, Operator.MINUS)
	}

	def multiply(Expression e) {
		new BinaryExpression(this, e, Operator.TIMES)
	}

	int compareTo(Expression e) {
		solver.addConstraint(new Constraint(this, (e as NumberExpression).num))
		return -1
	}
}
