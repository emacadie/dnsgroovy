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

public class RcodeSpockTest extends Specification {
	
    def "test_string"() {
	expect:
	// a regular one
	"NXDOMAIN" == Rcode.string(Rcode.NXDOMAIN)

	// one with an alias
	"NOTIMP" == Rcode.string(Rcode.NOTIMP)

	// one that doesn't exist
	Rcode.string(20).startsWith("RESERVED")

	when:
	    Rcode.string(-1)
	then:
	    thrown( IllegalArgumentException.class )
	
	//  (max is 0xFFF)
	when:
	    Rcode.string(0x1000)
	then:
	    thrown( IllegalArgumentException.class )
    }
	
    def "test_TSIGstring"() {
	expect:
	// a regular one
	"BADSIG" == Rcode.TSIGstring(Rcode.BADSIG)

	// one that doesn't exist
	Rcode.TSIGstring(20).startsWith("RESERVED")

	when:
	    Rcode.TSIGstring(-1)
	then:
	    thrown( IllegalArgumentException.class )
	
	//  (max is 0xFFFF)
	when:
	    Rcode.string(0x10000)
	then:
	    thrown( IllegalArgumentException.class )
    }

    def "test_value"() {
	expect:
	// regular one
	Rcode.FORMERR == Rcode.value("FORMERR")

	// one with alias
	Rcode.NOTIMP == Rcode.value("NOTIMP")
	Rcode.NOTIMP == Rcode.value("NOTIMPL")

	// one thats undefined but within range
	35 == Rcode.value("RESERVED35")

	// one thats undefined but out of range
	-1 == Rcode.value("RESERVED" + 0x1000)

	// something that unknown
	-1 == Rcode.value("THIS IS DEFINITELY UNKNOWN")

	// empty string
	-1 == Rcode.value("")
    }
}
