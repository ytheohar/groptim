package org.ytheohar.groptim

import org.ytheohar.groptim.engine.ApacheSimplexImpl;
import org.ytheohar.groptim.engine.SolverEngine;
import org.ytheohar.groptim.model.BinaryExpression
import org.ytheohar.groptim.model.Constraint
import org.ytheohar.groptim.model.Expression
import org.ytheohar.groptim.model.LPVar
import org.ytheohar.groptim.model.NumberExpression
import org.ytheohar.groptim.model.Operator


class LPSolver {

	boolean max
	Expression objFunc
	def vars = []
	def constraints = []
	def nameToVar = [:]
	int index
	SolverEngine solverEngine
	double objectiveValue

	LPSolver() {
		this(new ApacheSimplexImpl())
	}

	LPSolver(SolverEngine solverEngine) {
		this.solverEngine = solverEngine

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
		max = true
		optimize c
	}

	def min(Closure c) {
		max = false
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
		objectiveValue = solverEngine.solve(max, objFunc, vars, constraints)
	}
}
