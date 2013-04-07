This project is a port of dnsjava to Groovy.   

The doc page: http://commons.apache.org/math/userguide/linear.html  
Javadoc: http://commons.apache.org/math/apidocs/index.html?org/apache/commons/math3/linear/package-tree.html   
The Spock docs: Currently, only http://javadoc.spockframework.org/latest is published (and http://groovydoc.spockframework.org/latest for Groovydoc). Starting with the next release, we'll publish a version for each release. In the meantime, you can also use: http://evgeny-goldin.org/javadoc/spock-core/ Other docs are at http://docs.spockframework.org/en/latest/   

After much pain and suffering, I got Maven to make the JUnit javadoc:  
file:///home/ericm/tmp/Java/junit/target/site/apidocs/index.html  

A few gradle commands. I will update this when I add more classes:    

To run a single test, do this:  
gradle -Dtest.single=HelloSpock test -info   
gradle -Dtest.single=StepwiseExtension test -info  
   

To run with Groovy:   
gradle runGroovy -PmainClass="org.lookup"    
    



