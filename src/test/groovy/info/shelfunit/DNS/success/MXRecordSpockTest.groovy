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

import java.util.Arrays;
import spock.lang.Specification

public class MXRecordSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    def "test_getObject"() {
	MXRecord d = new MXRecord();
	Record r = d.getObject();
	expect: mga.that(r instanceof MXRecord);
    }

    def "test_ctor_5arg"() throws TextParseException {
	when:
	Name n = Name.fromString("My.Name.");
	Name m = Name.fromString("My.OtherName.");

	MXRecord d = new MXRecord(n, DClass.IN, 0xABCDEL, 0xF1, m);

	then:
	mgu.equals(n, d.getName());
	mgu.equals(Type.MX, d.getType());
	mgu.equals(DClass.IN, d.getDClass());
	mgu.equals(0xABCDEL, d.getTTL());
	mgu.equals(0xF1, d.getPriority());
	mgu.equals(m, d.getTarget());
	mgu.equals(m, d.getAdditionalName());
    }
    
    def "test_rrToWire"() throws TextParseException {
	Name n = Name.fromString("My.Name.");
	Name m = Name.fromString("M.O.n.");
	
	MXRecord mr = new MXRecord(n, DClass.IN, 0xB12FL, 0x1F2B, m );

	// canonical
	when:
	DNSOutput dout = new DNSOutput();
	mr.rrToWire(dout, null, true);
	byte[] out = dout.toByteArray();
	def exp = [ 0x1F, 0x2B, 1, 'm', 1, 'o', 1, 'n', 0 ].collect { entry -> (byte) entry }
	byte[] b_array = exp.toArray(new byte[exp.size()])
	then:
	mga.that(Arrays.equals(b_array, out));

	// case sensitive
	when:
	dout = new DNSOutput();
	mr.rrToWire(dout, null, false);
	out = dout.toByteArray();
	exp = [ 0x1F, 0x2B, 1, 'M', 1, 'O', 1, 'n', 0 ].collect { entry -> (byte) entry }
	then:
	mga.that(Arrays.equals(exp.toArray(new byte[exp.size()]), out));
    }

}
