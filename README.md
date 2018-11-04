# Deep Scap SSM
A minimal seed template for a [Scalismo](https://github.com/unibas-gravis/scalismo) build able to sample using different probability distributions scapulae bones.

It can also measure the different features of the scapulae automatically (critical shoulder angle, tilt, version, acromion angle curvature of the glene with sphere fitting).

This template uses scalismo a scala framework for statistical shape modelling built by the GRAVIS department of the University of Basel.

Some deep learning and linear regression results can be found on the following Kaggle Kernel :

https://www.kaggle.com/iham97/deepscapulassm

Once you have [sbt](http://www.scala-sbt.org/release/tutorial/Setup.html) installed, you can clone this project to build an application making use of Scalismo.


### Compiling executable jars
To compile your application as an executable Jar, you can use the assembly command:
~~~
sbt assembly
~~~
This will dump an executable jar file in the target/scala-2.12/ directory. To run the jar:

~~~
java -jar target/scala-2.12/executable.jar
~~~

The name as well as the Main class to be used for the executable jar can be changed in the [build.sbt](https://github.com/unibas-gravis/minimal-scalismo-seed/blob/master/build.sbt) file
