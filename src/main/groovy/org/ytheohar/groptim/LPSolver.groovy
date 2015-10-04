package org.ytheohar.groptim

import org.ytheohar.groptim.engine.ApacheSimplexImpl
import org.ytheohar.groptim.engine.SolverEngine
import org.ytheohar.groptim.model.BinaryExpression
import org.ytheohar.groptim.model.Constraint
import org.ytheohar.groptim.model.Expression
import org.ytheohar.groptim.model.LPVar
import org.ytheohar.groptim.model.NumberExpression
import org.ytheohar.groptim.model.Operator

/**
 * Implements the Groptim DSL
 * 
 * @author Yannis Theocharis
 */
class LPSolver {

	boolean max
	Expression objFunc
	def vars = []
	def constraints = []
	def nameToVar = [:]
	int index
	SolverEngine solverEngine
	double objectiveValue

	/**
	 * Default constructor. Uses Apache Math Simplex implementation to
	 * solve the LP instances. 
	 */
	LPSolver() {
		this(new ApacheSimplexImpl())
	}

	/**
	 * Constructor that allows to pass any LP solver implementation to solve
	 * the LP instances with. To do so, you need to implement the SolverEngine
	 * interface, as a wrapper around the specific solver implementation.
	 * 
	 * @param solverEngine the specific LP solver implementation, usually a simplex implementation
	 */
	LPSolver(SolverEngine solverEngine) {
		this.solverEngine = solverEngine

		Number.metaClass.multiply = { Expression e ->
			new BinaryExpression(new NumberExpression(delegate), e, Operator.TIMES)
		}
		Number.metaClass.getC { new NumberExpression(delegate) }
	}

	private def registerVar(String name) {
		def var = new LPVar(name, index, this)
		index++
		vars << var
		nameToVar[name] = var
		var
	}

	def propertyMissing(String name) {
		nameToVar[name] ?: registerVar(name)
	}

	/**
	 * Builds the objective function out of the input block 
	 * and returns a map that 'accepts' (has as keys) the keywords 
	 * 'subject' and 'subjectTo'
	 * 
	 * @param c the objective function block
	 */
	def max(Closure c) {
		max = true
		optimize c
	}

	def min(Closure c) {
		max = false
		optimize c
	}

	private def optimize(Closure c) {
		c.delegate = this
		objFunc = c()
		['subject': { it -> ['to': { Closure cTo-> subjectTo(cTo)}]},
			'subjectTo': { it -> subjectTo(it)} ]
	}

	def addConstraint(Constraint c) {
		constraints << c
	}

	/**
	 * Builds the constraints and delegates the LP instances
	 * to the underlying LP solver implementation to solve it
	 * 
	 * @param c the constraints block
	 */
	private def subjectTo(Closure c) {
		c.delegate = this
		c()
		objectiveValue = solverEngine.solve(max, objFunc, vars, constraints)
	}
}
