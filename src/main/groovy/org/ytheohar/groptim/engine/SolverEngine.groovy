package org.ytheohar.groptim.engine
interface SolverEngine {
	def solve(boolean max, Expression objFunc, List<LPVar> vars, List<Constraint> constraints);