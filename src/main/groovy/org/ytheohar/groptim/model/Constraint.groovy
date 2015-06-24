package org.ytheohar.groptim.model

import groovy.transform.Canonical

@Canonical
class Constraint {
	Expression e
	double b
	boolean leq=true
}
