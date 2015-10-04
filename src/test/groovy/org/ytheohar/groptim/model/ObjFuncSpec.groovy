package org.ytheohar.groptim.model

import org.ytheohar.groptim.LPSolver

import spock.lang.Specification
import spock.lang.Unroll

class ObjFuncSpec extends Specification {

	def 'objective function: 7' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 7.c }

		then:
		lp.objFunc.num == 7
	}

	def 'objective function: x0' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0 }

		then:
		lp.objFunc == lp.x0
	}

	def 'objective function: x0 + 7' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0 + 7.c }

		then:
		lp.objFunc.left == lp.x0
		lp.objFunc.right.num == 7
		lp.objFunc.op == Operator.PLUS
	}

	def 'objective function: x0 - 7' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0 - 7.c }

		then:
		lp.objFunc.left == lp.x0
		lp.objFunc.right.num == 7
		lp.objFunc.op == Operator.MINUS
	}

	@Unroll
	def 'objective function: #coef*x0 + #c' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef*x0 + c }

		then:
		lp.objFunc.left.left.num == coef
		lp.objFunc.left.right == lp.x0
		lp.objFunc.left.op == Operator.TIMES

		lp.objFunc.right == c
		lp.objFunc.op == Operator.PLUS

		where:
		coef | c
		-3 	 | 1.c
		2	 | 0.c
		0	 | -5.c
	}

	@Unroll
	def 'objective function: #coef*x0 - #c' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef*x0 - c }

		then:
		lp.objFunc.left.left.num == coef
		lp.objFunc.left.right == lp.x0
		lp.objFunc.left.op == Operator.TIMES

		lp.objFunc.right == c
		lp.objFunc.op == Operator.MINUS

		where:
		coef | c
		-3 	 | 1.c
		2	 | 0.c
		0	 | -5.c
	}

	@Unroll
	def 'objective function: #coef0*x0 + #coef1*x1 + #c' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 + coef1*x1 + c }

		then:
		lp.objFunc.left.left.left.num == coef0
		lp.objFunc.left.left.right == lp.x0
		lp.objFunc.left.left.op == Operator.TIMES

		lp.objFunc.left.right.left.num == coef1
		lp.objFunc.left.right.right == lp.x1
		lp.objFunc.left.right.op == Operator.TIMES

		lp.objFunc.left.op == Operator.PLUS

		lp.objFunc.right == c
		lp.objFunc.op == Operator.PLUS

		where:
		coef0 	| coef1 | c
		-3 	 	|2		| 1.c
		2	 	|-1		| 0.c
		0	 	|-4		| -5.c
	}

	@Unroll
	def 'objective function: #coef0*x0 - #coef1*x1' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 - coef1*x1 - 5.c}

		then:
		lp.objFunc.left.left.left.num == coef0
		lp.objFunc.left.left.right == lp.x0
		lp.objFunc.left.left.op == Operator.TIMES

		lp.objFunc.left.right.left.num == coef1
		lp.objFunc.left.right.right == lp.x1
		lp.objFunc.left.right.op == Operator.TIMES

		lp.objFunc.left.op == Operator.MINUS

		lp.objFunc.right.num == 5
		lp.objFunc.op == Operator.MINUS

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	def 'non-linear, illegal objective function: x0*x1' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0*x1 }

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Non linear expressions not allowed.'
	}

	def 'non-linear, illegal objective function: x0*x1 + 7' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0*x1 + 7 }

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Non linear expressions not allowed.'
	}

	def 'non-linear, illegal objective function: 2*x0*x1' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 2*x0*x1}

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Non linear expressions not allowed.'
	}

	def 'non-linear, illegal objective function: (x0 + 5)*x1' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { (x0 + 5.c)*x1}

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Non linear expressions not allowed.'
	}

	def 'non-linear, illegal objective function: (x0 + 5)*(x1 - 3)' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { (x0 + 5.c)*(x1-3.c) }

		then:
		IllegalStateException ex = thrown()
		ex.message == 'Non linear expressions not allowed.'
	}

	@Unroll
	def 'apache objective function: #coef0*x0 + #coef1*x1 + 5.c' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 + coef1*x1 + 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == coef0
		o.coefficients.getEntry(lp.x1.index) == coef1
		o.constantTerm == 5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: #coef0*x0 + #coef1*x1 - 5.c' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 + coef1*x1 - 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == coef0
		o.coefficients.getEntry(lp.x1.index) == coef1
		o.constantTerm == -5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: #coef0*x0 - #coef1*x1 + 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 - coef1*x1 + 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == coef0
		o.coefficients.getEntry(lp.x1.index) == -coef1
		o.constantTerm == 5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: #coef0*x0 - #coef1*x1 - 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 - coef1*x1 - 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == coef0
		o.coefficients.getEntry(lp.x1.index) == -coef1
		o.constantTerm == -5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 + #coef1*x1 + 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 + coef1*x1 + 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.coefficients.getEntry(lp.x1.index) == coef1
		o.constantTerm == 5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 + #coef1*x1 - 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 + coef1*x1 - 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.coefficients.getEntry(lp.x1.index) == +coef1
		o.constantTerm == -5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 - #coef1*x1 + 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 - coef1*x1 + 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.coefficients.getEntry(lp.x1.index) == -coef1
		o.constantTerm == 5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 - #coef1*x1 - 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 - coef1*x1 - 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.coefficients.getEntry(lp.x1.index) == -coef1
		o.constantTerm == -5

		where:
		coef0 	| coef1
		3 	 	|2
		-3 	 	|2
		3	 	|-2
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 + 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 + 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.constantTerm == 5

		where:
		coef0 	| _
		3 	 	|_
		-3 	 	|_
	}

	@Unroll
	def 'apache objective function: -#coef0*x0 - 5' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 - 5.c}
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.constantTerm == -5

		where:
		coef0 	| _
		3 	 	|_
		-3 	 	|_
	}

	@Unroll
	def 'apache objective function: #coef0*x0' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { coef0*x0 }
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == coef0
		o.constantTerm == 0

		where:
		coef0 	|_
		3 	 	|_
		-3 	 	|_
	}

	@Unroll
	def 'apache objective function: -#coef0*x0' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { -coef0*x0 }
		def o = lp.toApacheObjFunc(lp.objFunc)

		then:
		o.coefficients.getEntry(lp.x0.index) == -coef0
		o.constantTerm == 0

		where:
		coef0 	| _
		3 	 	|_
		-3 	 	|_
	}
}
