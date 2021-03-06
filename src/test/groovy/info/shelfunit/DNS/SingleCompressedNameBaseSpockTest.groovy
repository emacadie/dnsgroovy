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

import org.xbill.DNS.DClass
import org.xbill.DNS.DNSOutput
import org.xbill.DNS.Name
import org.xbill.DNS.SCNBTestSpockClass
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type

import java.io.IOException
import spock.lang.Specification

public class SingleCompressedNameBaseSpockTest extends Specification {

    /*
    private void assertEquals( byte[] exp, byte[] act ) {
	assertTrue(java.util.Arrays.equals(exp, act))
    } 
    
    private static class SCNBTestSpockClass extends SingleCompressedNameBase   {
	public SCNBTestSpockClass(){}

	public SCNBTestSpockClass(Name name, int type, int dclass, long ttl, Name singleName, String desc ) {
	    super(name, type, dclass, ttl, singleName, desc)
	}
	
	public Name getSingleName()	{
	    return super.getSingleName()
	}

	public Record getObject() {
	    return null
	}
    }
*/
    def "test_ctor"() throws TextParseException {
	SCNBTestSpockClass tc = new SCNBTestSpockClass()
	expect:
	null == tc.getSingleName()

	when:
	Name n = Name.fromString("my.name.")
	Name sn = Name.fromString("my.single.name.")

	tc = new SCNBTestSpockClass(n, Type.A, DClass.IN, 100L, sn, "The Description")
	then:
	n == tc.getName()
	Type.A == tc.getType()
	DClass.IN == tc.getDClass()
	100L == tc.getTTL()
	sn == tc.getSingleName()
    }
    
    def "test_rrToWire"() throws IOException, TextParseException {
	Name n = Name.fromString("my.name.")
	Name sn = Name.fromString("My.Single.Name.")

	// non-canonical (case sensitive)
	SCNBTestSpockClass tc = new SCNBTestSpockClass(n, Type.A, DClass.IN, 100L, sn, "The Description")
	def byte[] exp = [ 2, 'M', 'y', 6, 'S', 'i', 'n', 'g', 'l', 'e', 4, 'N', 'a', 'm', 'e', 0 ]

	DNSOutput dout = new DNSOutput()
	tc.rrToWire(dout, null, false)
	
	byte[] out = dout.toByteArray()
	expect:
	exp == out

	when:
	// canonical (lowercase)
	tc = new SCNBTestSpockClass(n, Type.A, DClass.IN, 100L, sn, "The Description")
	exp = [ 2, 'm', 'y', 6, 's', 'i', 'n', 'g', 'l', 'e', 4, 'n', 'a', 'm', 'e', 0 ]

	dout = new DNSOutput()
	tc.rrToWire(dout, null, true)
	
	out = dout.toByteArray()
	then:
	exp == out
    }

}
