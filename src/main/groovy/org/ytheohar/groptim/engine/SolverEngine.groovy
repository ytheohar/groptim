package org.ytheohar.groptim.engineimport org.ytheohar.groptim.model.Constraintimport org.ytheohar.groptim.model.Expressionimport org.ytheohar.groptim.model.LPVar
interface SolverEngine {
	def solve(boolean max, Expression objFunc, List<LPVar> vars, List<Constraint> constraints);}
