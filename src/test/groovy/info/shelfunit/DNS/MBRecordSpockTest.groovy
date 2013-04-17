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
package	info.shelfunit.DNS

import org.xbill.DNS.*

import junit.framework.TestCase
import spock.lang.Specification

public class MBRecordSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
	
    def "test_ctor_0arg"() {
	MBRecord d = new MBRecord()
	expect:
	mgu.equals(d.getName(),null)
	mgu.equals(d.getAdditionalName(),null)
	mgu.equals(d.getMailbox(),null)
    }

    def "test_ctor_4arg"() throws TextParseException {
	when:
	Name n = Name.fromString("my.name.")
	Name a = Name.fromString("my.alias.")

	MBRecord d = new MBRecord(n, DClass.IN, 0xABCDEL, a)

	then:
	mgu.equals(n, d.getName())
	mgu.equals(Type.MB, d.getType())
	mgu.equals(DClass.IN, d.getDClass())
	mgu.equals(0xABCDEL, d.getTTL())
	mgu.equals(a, d.getAdditionalName())
	mgu.equals(a, d.getMailbox())
	
    }

    def "test_getObject"() {
	MBRecord d = new MBRecord()
	Record r = d.getObject()
	expect: r instanceof MBRecord
    }

}

