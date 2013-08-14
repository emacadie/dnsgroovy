// -*- Java -*-
//
// Copyright (c) 2005, Matthew J. Rutherford <rutherfo@cs.colorado.edu>
// Copyright (c) 2005, University of Colorado at Boulder
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
// 
// * Redistributions of source code must retain the above copyright
//   notice, this list of conditions and the following disclaimer.
// 
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
// 
// * Neither the name of the University of Colorado at Boulder nor the
//   names of its contributors may be used to endorse or promote
//   products derived from this software without specific prior written
//   permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
package info.shelfunit.DNS

import org.xbill.DNS.*

import spock.lang.Specification

public class OptionsSpockTest extends Specification {

    def setup() {
	// reset the options table before each test
	Options.clear()
    }
    
    def "test_set_1arg"()  {
	when:
	Options.set("Option1")
	then: "true" == Options.value("option1")

	when:
	Options.set("OPTION2")
	then:
	"true" == Options.value("option1")
	"true" == Options.value("OpTIOn2")

	when:
	Options.set("option2")
	then: "true" == Options.value("option2")
    }
    
    def "test_set_2arg"() {
	when:
	Options.set("OPTION1", "Value1")
	then: "value1" == Options.value("Option1")

	when:
	Options.set("option2", "value2")
	then:
	"value1" == Options.value("Option1")
	"value2" == Options.value("OPTION2")

	when:
	Options.set("OPTION2", "value2b")
	then:
	"value1" == Options.value("Option1")
	"value2b" == Options.value("option2")
    }
    
    def "test_check"() {
	expect: !Options.check("No Options yet")

	when:
	Options.set("First Option")
	then:
	!Options.check("Not a valid option name")
	Options.check("First Option")
	Options.check("FIRST option")
    }
    
    def "test_unset"() {
	// unset something non-existant
	Options.unset("Not an option Name")
	when:
	Options.set("Temporary Option")
	then: Options.check("Temporary Option")
	when:
	Options.unset("Temporary Option")
	then: !Options.check("Temporary Option")
	
	when:
	Options.set("Temporary Option")
	then: Options.check("Temporary Option")
	when:
	Options.unset("temporary option")
	then: !Options.check("Temporary Option")

	// unset something now that the table is non-null
	Options.unset("Still Not an Option Name")
    }

    def "test_value"() {
       
	Options.set("Testing Option")
	expect:
	null == Options.value("Table is Null")
	null == Options.value("Not an Option Name")

	"true" == Options.value("Testing OPTION")
    }

    def "test_intValue"() {
	expect: -1 == Options.intValue("Table is Null")
	
	when:
	Options.set("A Boolean Option")
	Options.set("An Int Option", "13")
	Options.set("Not An Int Option", "NotAnInt")
	Options.set("A Negative Int Value", "-1000")
	then:
	-1 == Options.intValue("A Boolean Option")
	-1 == Options.intValue("Not an Option NAME")
	13 == Options.intValue("an int option")
	-1 == Options.intValue("NOT an INT option")
	-1 == Options.intValue("A negative int Value")
    }

    def "test_systemProperty"() {
	System.setProperty("dnsjava.options", "booleanOption,valuedOption1=10,valuedOption2=NotAnInteger")
	
	Options.refresh()
	expect:
	Options.check("booleanOPTION")
	Options.check("booleanOption")
	Options.check("valuedOption1")
	Options.check("ValuedOption2")

	"true" == Options.value("booleanOption")
	-1 == Options.intValue("BOOLEANOPTION")
	"10" == Options.value("valuedOption1")
	10 == Options.intValue("valuedOption1")
	"notaninteger" == Options.value("VALUEDOPTION2")
	-1 == Options.intValue("valuedOption2")
    }
}
