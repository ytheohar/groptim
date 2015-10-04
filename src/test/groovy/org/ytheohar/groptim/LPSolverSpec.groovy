package org.ytheohar.groptim

import spock.lang.Specification

// Test cases from org.apache.commons.math3.optim.linear.SimplexSolverTest
class LPSolverSpec extends Specification {

	def 'from SimplexSolverTest.testSimplexSolver' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			15*x0 + 10*x1 + 7.c
		} subjectTo {
			x0 <= 2.c
			x1 <= 3.c
			x0 + x1 <= 4.c
			-x0 - x1 <= -4.c
		}

		then:
		lp.x0.value == 2.0
		lp.x1.value == 2.0
		lp.objectiveValue == 57.0
	}

	def 'from SimplexSolverTest.testSimplexSolver subject() to syntax' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			15*x0 + 10*x1 + 7.c
		} subject() to {
			x0 <= 2.c
			x1 <= 3.c
			x0 + x1 <= 4.c
			-x0 - x1 <= -4.c
		}

		then:
		lp.x0.value == 2.0
		lp.x1.value == 2.0
		lp.objectiveValue == 57.0
	}

	def 'from SimplexSolverTest.testSingleVariableAndConstraint' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 3*x0 } subjectTo { x0 <= 10.c }

		then:
		lp.x0.value == 10.0
		lp.objectiveValue == 30.0
	}

	def 'from SimplexSolverTest.testMinimization' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min {
			-2*x0 + x1 - 5.c
		} subjectTo {
			x0 + 2*x1 <= 6.c
			3*x0 + 2*x1 <= 12.c
			-x0 <= 0.c
			-x1 <= 0.c
		}

		then:
		lp.x0.value == 4.0
		lp.x1.value == 0.0
		lp.objectiveValue == -13.0
	}

	def 'from SimplexSolverTest.testMinimization subject() to syntax' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min {
			-2*x0 + x1 - 5.c
		} subject() to {
			x0 + 2*x1 <= 6.c
			3*x0 + 2*x1 <= 12.c
			-x0 <= 0.c
			-x1 <= 0.c
		}

		then:
		lp.x0.value == 4.0
		lp.x1.value == 0.0
		lp.objectiveValue == -13.0
	}

	def 'from SimplexSolverTest.testInfeasibleSolution' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 15*x0 } subjectTo {
			x0 <= 1.c
			-x0 <= -3.c
		}

		then:
		thrown(org.apache.commons.math3.optim.linear.NoFeasibleSolutionException)
	}

	def 'from SimplexSolverTest.testUnboundedSolution' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			15*x0 + 10*x1
		} subjectTo {  x0 <= 2.c  }

		then:
		thrown(org.apache.commons.math3.optim.linear.UnboundedSolutionException)
	}

	def 'from SimplexSolverTest.testRestrictVariablesToNonNegative' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			409*x0 + 523*x1 + 70*x2 + 204*x3 + 339*x4
		} subjectTo {
			43*x0 +   56*x1 + 345*x2 +  56*x3 +    5*x4 <= 4567456.c
			12*x0 +   45*x1 +   7*x2 +  56*x3 +   23*x4 <= 56454.c
			8*x0 +  768*x1 +           34*x3 + 7456*x4 <= 1923421.c
			-12342*x0 - 2342*x1 - 34*x2 - 678*x3 - 2342*x4 <= -4356.c
			45*x0 +  678*x1 +  76*x2 +  52*x3 +   23*x4 <= 456356.c
			-45*x0 - 678*x1 - 76*x2 - 52*x3 - 23*x4 >= -456356.c
			-x0 <= 0.c
			-x1 <= 0.c
			-x2 <= 0.c
			-x3 <= 0.c
			-x4 <= 0.c
		}

		then:
		lp.x0.value.trunc(10) == 2902.9278350515
		lp.x1.value.trunc(10) == 480.4192439862
		lp.x2.value == 0.0
		lp.x3.value == 0.0
		lp.x4.value == 0.0
		lp.objectiveValue.trunc(6) == 1438556.749140
	}

	def 'from SimplexSolverTest.testEpsilon' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			10*x0 + 5*x1 + x2
		} subjectTo {
			9*x0 +  8*x1        <=  17.c
			-9*x0 - 8*x1        <= -17.c
			7*x1 + 8*x2 <=   7.c
			10*x0 +       + 2*x2 <=  10.c
		}

		then:
		lp.x0.value == 1.0
		lp.x1.value == 1.0
		lp.x2.value == 0.0
		lp.objectiveValue == 15.0
	}

	def 'from SimplexSolverTest.testTrivialModel' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { x0 + x1 } subjectTo {
			x0 + x1 <= 0.c
		}

		then:
		lp.x0.value == 0.0
		lp.x1.value == 0.0
		lp.objectiveValue == 0.0
	}

	def 'from SimplexSolverTest.testMath713NegativeVariable' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min { x0 + x1 } subjectTo {
			x0 <= 	1.c
			-x0 <= -1.c
			-x1 <= 0.c
		}

		then:
		lp.x0.value == 1.0
		lp.x1.value == 0.0
		lp.objectiveValue == 1.0
	}

	def 'from SimplexSolverTest.testMath434UnfeasibleSolution' () {
		given:
		def lp = new LPSolver()
		def epsilon = 1e-6;

		when:
		lp.min { x0 } subjectTo {
			(epsilon/2)*x0 + 0.5*x1 <= 0.c
			-(epsilon/2)*x0 - 0.5*x1 <= 0.c
			1e-3*x0 + 0.1*x1 <= 10.c
			-1e-3*x0 - 0.1*x1 <= -10.c
			-x0 <= 0.c
			-x1 <= 0.c
		}

		then:
		thrown(org.apache.commons.math3.optim.linear.NoFeasibleSolutionException)
	}

	def 'from SimplexSolverTest.testMath434PivotRowSelection' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min { x0 } subjectTo {
			-200*x0 <= 	-1.c
			-100*x0 <= -0.499900001.c
		}

		then:
		lp.x0.value == 0.005
		lp.objectiveValue == 0.005
	}

	def 'from SimplexSolverTest.testMath272' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min {
			2*x0 + 2*x1 + x2
		} subjectTo {
			-x0 - x1     <= -1.c
			-x0 - x2 <= -1.c
			-x1     <= -1.c
		}

		then:
		lp.x0.value == 0.0
		lp.x1.value == 1.0
		lp.x2.value == 1.0
		lp.objectiveValue == 3.0
	}

	def 'from SimplexSolverTest.testMath286' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			0.8*x0 + 0.2*x1 + 0.7*x2 + 0.3*x3 + 0.6*x4 + 0.4*x5
		} subjectTo {
			x0 + x2 + x4 <= 23.c
			-x0 - x2 - x4 <= -23.c
			x1 + x3 + x5 <= 23.c
			-x1 - x3 - x5 <= -23.c
			-x0 <= -10.c
			-x2 <= -8.c
			-x4 <= -5.c
			-x1 <= 0.c
			-x3 <= 0.c
			-x5 <= 0.c
		}

		then:
		lp.x0.value == 10.0
		lp.x1.value == 0.0
		lp.x2.value == 8.0
		lp.x3.value == 0.0
		lp.x4.value == 5.0
		lp.x5.value == 23.0
		lp.objectiveValue.trunc(1) == 25.8
	}

	def 'from SimplexSolverTest.testDegeneracy' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min { 0.8*x0 + 0.7*x1 } subjectTo {
			x0 + x1 <= 18.c
			-x0 <= -10.c
			-x1 <= -8.c
		}

		then:
		lp.x0.value == 10.0
		lp.x1.value == 8.0
		lp.objectiveValue == 13.6
	}

	def 'from SimplexSolverTest.testMath288' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max { 7*x0 + 3*x1 } subjectTo {
			3*x0 - 5*x2 <= 0.c
			2*x0 - 5*x3 <= 0.c
			3*x1 - 5*x3 <= 0.c
			x0 <= 1.c
			x1 <= 1.c
		}

		then:
		lp.x0.value == 1.0
		lp.x1.value == 1.0
		lp.x2.value.trunc(5) == 0.60000
		lp.x3.value.trunc(5) == 0.60000
		lp.objectiveValue == 10.0
	}

	def 'from SimplexSolverTest.testMath290GEQ' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min { x0 + 5*x1 } subjectTo {
			-2*x0 <= 1.c
			-x0 <= 0.c
			-x1 <= 0.c
		}

		then:
		lp.x0.value == 0
		lp.x1.value == 0
		lp.objectiveValue == 0
	}

	def 'from SimplexSolverTest.testMath290LEQ' () {
		given:
		def lp = new LPSolver()

		when:
		lp.min { x0 + 5*x1 } subjectTo {
			2*x0 <= -1.c
			-x0 <= 0.c
			-x1 <= 0.c
		}

		then:
		thrown(org.apache.commons.math3.optim.linear.NoFeasibleSolutionException)
	}

	def 'from SimplexSolverTest.testMath293' () {
		given:
		def lp = new LPSolver()

		when:
		lp.max {
			0.8*x0 + 0.2*x1 + 0.7*x2 + 0.3*x3 + 0.4*x4 + 0.6*x5
		} subjectTo {
			x0 + x2 + x4 <= 30.c
			-x0 - x2 - x4 <= -30.c
			x1 + x3 + x5 <= 30.c
			-x1 - x3 - x5 <= -30.c
			-0.8*x0 - 0.2*x1 <= -10.c
			-0.7*x2 - 0.3*x3 <= -10.c
			-0.4*x4 - 0.6*x5 <= -10.c
			-x0 <= 0.c
			-x1 <= 0.c
			-x2 <= 0.c
			-x3 <= 0.c
			-x4 <= 0.c
			-x5 <= 0.c
		}

		then:
		lp.x0.value.trunc(3) == 15.714
		lp.x1.value == 0
		lp.x2.value.trunc(3) == 14.285
		lp.x3.value == 0
		lp.x4.value == 0
		lp.x5.value == 30.0
		lp.objectiveValue.trunc(4) == 40.5714
	}

}
