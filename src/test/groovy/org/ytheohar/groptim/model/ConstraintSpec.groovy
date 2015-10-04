package org.ytheohar.groptim.model

import org.ytheohar.groptim.LPSolver

import spock.lang.Specification

class ConstraintSpec extends Specification {

	def 'constraint: x0 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo { x0 <= 4.c }

		then:
		lp.constraints[0].e == lp.x0
		lp.constraints[0].leq
		lp.constraints[0].b == 4
	}

	def 'constraint with various comparison symbols' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			x0 <= 4.c
			x0 + x1 <= 4.c
			x0 < 5.c
			x0 + x1 < 5.c
			x0 == 2.c
			x0 + x1 == 2.c
			x0 >= 1.c
			x0 + x1 >= 1.c
			x0 > 0.c
			x0 + x1 > 0.c
		}

		then: 'all comparison (<, >, >=, ==) symbols are deemed <='
		lp.constraints.findAll { it.leq }.size() == lp.constraints.size()
	}

	def 'constraint: x0 + x1 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			x0 + x1 <= 4.c
		}

		then:
		lp.constraints[0].e.left == lp.x0
		lp.constraints[0].e.right == lp.x1
		lp.constraints[0].e.op == Operator.PLUS
		lp.constraints[0].leq
		lp.constraints[0].b == 4
	}

	def 'constraint: x0 - x1 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			x0 - x1 <= 4.c
		}
		def c = lp.solverEngine.toApacheConstraint(lp.constraints[0], 2)

		then:
		lp.constraints[0].e.left == lp.x0
		lp.constraints[0].e.right == lp.x1
		lp.constraints[0].e.op == Operator.MINUS
		lp.constraints[0].leq
		lp.constraints[0].b == 4

		c.coefficients.getEntry(lp.x0.index) == 1
		c.coefficients.getEntry(lp.x1.index) == -1
	}

	def 'constraint: x0 - #coef2*x1 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			- x0 - coef2*x1 <= 4.c
		}
		def c = lp.solverEngine.toApacheConstraint(lp.constraints[0], 2)

		then:
		c.coefficients.getEntry(lp.x0.index) == -1
		c.coefficients.getEntry(lp.x1.index) == -coef2

		where:
		coef2 |_
		0 |_
		2 |_
		-5 |_
	}

	def 'apache constraint: -x0 - x1 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			-x0 - x1 <= 4.c
		}
		def c = lp.solverEngine.toApacheConstraint(lp.constraints[0], 2)

		then:
		c.coefficients.getEntry(lp.x0.index) == -1.0
		c.coefficients.getEntry(lp.x1.index) == -1.0
	}

	def 'apache constraint: -x0 + -x1 <= 4' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 0.c } subjectTo {
			- x0 - x1 <= 4.c
		}
		def c = lp.solverEngine.toApacheConstraint(lp.constraints[0], 2)

		then:
		c.coefficients.getEntry(lp.x0.index) == -1.0
		c.coefficients.getEntry(lp.x1.index) == -1.0
	}
}
