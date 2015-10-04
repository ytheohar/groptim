package org.ytheohar.groptim.engineimport org.ytheohar.groptim.model.Constraintimport org.ytheohar.groptim.model.Expressionimport org.ytheohar.groptim.model.LPVar
/** * Any LP solver implementation needs to implement this interface  * for Groptim to be able to use it  *  * @author Yannis Theocharis */interface SolverEngine {
	def solve(boolean max, Expression objFunc, List<LPVar> vars, List<Constraint> constraints);}
