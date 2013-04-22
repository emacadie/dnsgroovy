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

public class ExtendedFlagsSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    def "test_string"() {
	expect:
	// a regular one
	mgu.equals("do", ExtendedFlags.string(ExtendedFlags.DO))

	// one that doesn't exist
	mga.that(ExtendedFlags.string(1).startsWith("flag"))

	when:
	    ExtendedFlags.string(-1)
	then:
	    thrown( IllegalArgumentException.class )
	
	//  (max is 0xFFFF)
	try {
	    ExtendedFlags.string(0x10000)
	    fail("IllegalArgumentException not thrown")
	}
	catch( IllegalArgumentException e ){
	}
    }

    def "test_value"() {
	expect:
	// regular one
	mgu.equals(ExtendedFlags.DO, ExtendedFlags.value("do"))

	// one thats undefined but within range
	mgu.equals(16, ExtendedFlags.value("FLAG16"))

	// one thats undefined but out of range
	mgu.equals(-1, ExtendedFlags.value("FLAG" + 0x10000))

	// something that unknown
	mgu.equals(-1, ExtendedFlags.value("THIS IS DEFINITELY UNKNOWN"))

	// empty string
	mgu.equals(-1, ExtendedFlags.value(""))
    }
}
