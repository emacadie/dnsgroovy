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

import spock.lang.Specification
import org.xbill.DNS.DClass

public class DClassSpockTest extends Specification {

    def "test_string"() {
	expect:
	// a regular one
	"IN" == DClass.string(DClass.IN)

	// one with an alias
	"CH" == DClass.string(DClass.CH)

	// one that doesn't exist
	DClass.string(20).startsWith("CLASS")

	when:
	    DClass.string(-1)
	// fail("IllegalArgumentException not thrown")
	then:
	thrown( IllegalArgumentException.class )
	
	//  (max is 0xFFFF)
	when:
	    DClass.string(0x10000)
	// fail("IllegalArgumentException not thrown")
	then:
	thrown( IllegalArgumentException.class )
    }

    def "test_value"() {
	expect:
	// regular one
	DClass.NONE == DClass.value("NONE")

	// one with alias
	DClass.HS == DClass.value("HS")
	DClass.HS == DClass.value("HESIOD")

	// one thats undefined but within range
	21 == DClass.value("CLASS21")

	// something that unknown
	-1 == DClass.value("THIS IS DEFINITELY UNKNOWN")

	// empty string
	-1 == DClass.value("")
    }
}
