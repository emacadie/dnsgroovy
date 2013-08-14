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

public class FlagsSpockTest extends Specification {

    def "test_string"() {
	expect:
	"aa" == Flags.string(Flags.AA) // a regular one
	Flags.string(12).startsWith("flag") // one that doesn't exist

	when:
	    Flags.string(-1)
	then:
	    thrown( IllegalArgumentException.class )
	
	//  (max is 0xF)
	when:
	    Flags.string(0x10)
	then:
            thrown( IllegalArgumentException.class )
    }
	
    def "test_value"() {
	expect:
	Flags.CD == Flags.value("cd").byteValue() // regular one
	13 == Flags.value("FLAG13") // one thats undefined but within range
	-1 == Flags.value("FLAG" + 0x10) // one thats undefined but out of range
	-1 == Flags.value("THIS IS DEFINITELY UNKNOWN") // something that unknown
	-1 == Flags.value("") // empty string
    }
    
    def "test_isFlag"() {
	when:
	    Flags.isFlag(-1)
	then:
	    thrown( IllegalArgumentException.class )

	expect:
	Flags.isFlag(0)
	!Flags.isFlag(1) // opcode
	!Flags.isFlag(2)
	!Flags.isFlag(3)
	!Flags.isFlag(4)
	Flags.isFlag(5)
	Flags.isFlag(6)
	Flags.isFlag(7)
	Flags.isFlag(8)
	Flags.isFlag(9)
	Flags.isFlag(10)
	Flags.isFlag(11)
	!Flags.isFlag(12)
	!Flags.isFlag(13)
	!Flags.isFlag(14)
	!Flags.isFlag(14)
        when:
	    Flags.isFlag(16)
	then:
	    thrown( IllegalArgumentException.class )
    }

}
