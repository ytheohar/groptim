#!/usr/bin/env groovy

@Grab('org.apache.commons:commons-math3:3.5')
@Grab('org.ytheohar:groptim:1.0')

import org.ytheohar.groptim.LPSolver
import org.codehaus.groovy.control.*

if(args) {
	def lp = new LPSolver()
	Binding binding = new Binding(lp: lp, max: lp.&max, min: lp.&min)
	GroovyShell shell = new GroovyShell(binding);
	def (objectiveValue, vars) = shell.evaluate (new File(args[0]))
	println 'optimum = '+ objectiveValue
	vars.each {
		println it.label +' = '+ it.value
	}
} else {
	println "Usage: groptim <LP file>"
}
