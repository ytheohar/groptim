# groptim
Groptim is a Groovy DSL for Linear Programming. It uses the [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/) and its [Simplex](https://en.wikipedia.org/wiki/Simplex_algorithm) implementation (see org.apache.commons.math3.optim.linear.SimplexSolver) to solve the LP instances. 

For more details read: http://ytheohar.blogspot.co.uk/2015/06/groptim-groovy-dsl-for-linear.html

# Dependencies & License
Groptim is an open source project licensed under [the Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt). 
[Gradle](https://gradle.org/) is used for building and dependency management.
The only compile time dependency is the [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/), while testing is done with [Spock](http://spockframework.github.io/spock/docs/1.0/index.html).

# Run with Groovy code
```groovy
  def lp = new LPSolver()
 
  lp.max {
    15*x0 + 10*x1 + 7.c
  } subjectTo {
    x0 <= 2.c
    x1 <= 3.c
    x0 + x1 <= 4.c
    -x0 - x1 <= -4.c
  }
  
  assert lp.x0.value == 2.0
  assert lp.x1.value == 2.0
  assert lp.objectiveValue == 57.0
```
# Run as a script
Create a file (e.g. named with 'test.lp') with the following content
```txt
max {
  15*x0 + 10*x1 + 7.c
} subjectTo {
  x0 <= 2.c
  x1 <= 3.c
  x0 + x1 <= 4.c
  -x0 - x1 <= -4.c
}
```

Then, given that you have groovy installed and added in your path, run:
```txt
> cd groptim/script
> groptim test.lp
optimum = 57.0
x0 = 2.0
x1 = 2.0
```
