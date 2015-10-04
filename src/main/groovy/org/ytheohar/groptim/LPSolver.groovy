package org.ytheohar.groptim

import org.apache.commons.math3.linear.OpenMapRealVector
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.optim.MaxIter
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.linear.LinearConstraint
import org.apache.commons.math3.optim.linear.LinearConstraintSet
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction
import org.apache.commons.math3.optim.linear.NonNegativeConstraint
import org.apache.commons.math3.optim.linear.Relationship
import org.apache.commons.math3.optim.linear.SimplexSolver
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.ytheohar.groptim.model.BinaryExpression
import org.ytheohar.groptim.model.Constraint
import org.ytheohar.groptim.model.Expression
import org.ytheohar.groptim.model.LPVar
import org.ytheohar.groptim.model.NumberExpression
import org.ytheohar.groptim.model.Operator


class LPSolver {
	private static final MaxIter DEFAULT_MAX_ITER = new MaxIter(100)

	GoalType type
	Expression objFunc
	double objectiveValue
	def vars = []
	def constraints = []
	def nameToVar = [:]
	int index

	LPSolver() {
		Number.metaClass.multiply = { Expression e ->
			new BinaryExpression(new NumberExpression(delegate), e, Operator.TIMES)
		}
		Number.metaClass.getC { new NumberExpression(delegate) }
	}

	def registerVar(String name) {
		def var = new LPVar(name, index, this)
		index++
		vars << var
		nameToVar[name] = var
		var
	}

	def propertyMissing(String name) {
		nameToVar[name] ?: registerVar(name)
	}

	def max(Closure c) {
		type = GoalType.MAXIMIZE
		optimize c
	}

	def min(Closure c) {
		type = GoalType.MINIMIZE
		optimize c
	}

	def optimize(Closure c) {
		c.delegate = this
		objFunc = c()
		['subject': { it -> ['to': { Closure cTo-> subjectTo(cTo)}]},
			'subjectTo': { it -> subjectTo(it)} ]
	}

	def addConstraint(Constraint c) {
		constraints << c
	}

	def subjectTo(Closure c) {
		c.delegate = this
		c()
		solve()
	}

	def solve() {
		LinearObjectiveFunction f = toApacheObjFunc(objFunc);
		def apacheConstraints = constraints.collect { toApacheConstraint(it) }

		SimplexSolver solver = new SimplexSolver();
		PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(apacheConstraints),
				type, new NonNegativeConstraint(true));

		objectiveValue = solution.value
		solution.point.eachWithIndex { it, index ->
			vars[index].value = it
		}
		[objectiveValue, vars]
	}

	def toSparseVector(Expression e) {
		RealVector v = new OpenMapRealVector(nameToVar.size());
		def c = e.fill(v, 1)
		[v, c]
	}

	def toApacheObjFunc(Expression e) {
		def (v, c) = toSparseVector(e)
		new LinearObjectiveFunction(v, c);
	}

	def toApacheConstraint(Constraint constraint) {
		def (v, x) = toSparseVector(constraint.e)
		new LinearConstraint(v, constraint.leq ? Relationship.LEQ : Relationship.EQ, constraint.b)
	}
}
