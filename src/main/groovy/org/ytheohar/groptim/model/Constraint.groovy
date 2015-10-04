package org.ytheohar.groptim.model

import groovy.transform.Canonical

/**
 * Models a linear constraints
 *
 * @author Yannis Theocharis
 */
@Canonical
class Constraint {
	Expression e
	double b
	boolean leq=true
}
