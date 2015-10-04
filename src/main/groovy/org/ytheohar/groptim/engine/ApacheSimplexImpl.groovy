package org.ytheohar.groptim.engine

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
import org.ytheohar.groptim.model.Constraint
import org.ytheohar.groptim.model.Expression
import org.ytheohar.groptim.model.LPVar


class ApacheSimplexImpl implements SolverEngine {
	private static final MaxIter DEFAULT_MAX_ITER = new MaxIter(100)

	def solve(boolean max, Expression objFunc, List<LPVar> vars, List<Constraint> constraints) {
		int size = vars.size()
		LinearObjectiveFunction f = toApacheObjFunc(objFunc, size);
		def apacheConstraints = constraints.collect { toApacheConstraint(it, size) }

		SimplexSolver solver = new SimplexSolver();
		GoalType type = max ? GoalType.MAXIMIZE : GoalType.MINIMIZE
		PointValuePair solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(apacheConstraints),
				type, new NonNegativeConstraint(false));

		solution.point.eachWithIndex { it, index ->
			vars[index].value = it
		}
		solution.value
	}

	private def toSparseVector(Expression e, int size) {
		RealVector v = new OpenMapRealVector(size);
		def c = e.fill(v, 1)
		[v, c]
	}

	private def toApacheObjFunc(Expression e, int size) {
		def (v, c) = toSparseVector(e, size)
		new LinearObjectiveFunction(v, c);
	}

	private def toApacheConstraint(Constraint constraint, int size) {
		def (v, x) = toSparseVector(constraint.e, size)
		new LinearConstraint(v, constraint.leq ? Relationship.LEQ : Relationship.EQ, constraint.b)
	}
}
