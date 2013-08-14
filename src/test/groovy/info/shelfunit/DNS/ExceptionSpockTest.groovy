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

package info.shelfunit.DNS

import org.xbill.DNS.*

import java.io.IOException
import spock.lang.Specification

public class ExceptionSpockTest extends Specification {

    def "test_InvalidDClassException"() {
	IllegalArgumentException e = new InvalidDClassException(10)
	expect:
	 "Invalid DNS class: 10" == e.getMessage() 
    }
    
    def "test_InvalidTTLException"() {
	IllegalArgumentException e = new InvalidTTLException(32345)
	expect:
	 "Invalid DNS TTL: 32345" == e.getMessage() 
    }

    def "test_InvalidTypeException"() {
	IllegalArgumentException e = new InvalidTypeException(32345)
	expect:
	 "Invalid DNS type: 32345" == e.getMessage() 
    }

    def "test_NameTooLongException"() {
	WireParseException e = new NameTooLongException()
	expect:  null ==  e.getMessage() 

	when:
	e = new NameTooLongException("This is my too long name")
	then:
	 "This is my too long name" == e.getMessage() 
    }

    def "test_RelativeNameException"() throws TextParseException {
	IllegalArgumentException e = new RelativeNameException("This is my relative name")
	expect:  "This is my relative name" == e.getMessage() 

	when:
	e = new RelativeNameException(Name.fromString("relative"))
	then:
	"'relative' is not an absolute name" == e.getMessage()
    }

    def "test_TextParseException"() {
	IOException e = new TextParseException()
	expect:  null ==  e.getMessage() 

	when:
	e = new TextParseException( "This is my message" )
	then:
	 "This is my message" == e.getMessage() 
    }

    def "test_WireParseException"() {
	IOException e = new WireParseException()
	expect:  null ==  e.getMessage() 

	when:
	e = new WireParseException( "This is my message" )
	then:
	 "This is my message" == e.getMessage() 
    }

    def "test_ZoneTransferException"() {
	Exception e = new ZoneTransferException()
	expect:  null ==  e.getMessage() 

	when:
	e = new ZoneTransferException( "This is my message" )
	then:
	 "This is my message" == e.getMessage() 
    }

}
