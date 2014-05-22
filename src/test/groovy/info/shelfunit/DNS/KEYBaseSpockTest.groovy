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
import org.xbill.DNS.DNSInput
import org.xbill.DNS.DNSOutput
import org.xbill.DNS.DNSSEC
import org.xbill.DNS.KBSTestClass
import org.xbill.DNS.Name
import org.xbill.DNS.Options
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type

import java.io.IOException
import java.util.Arrays
import spock.lang.Specification
import org.xbill.DNS.utils.Base64

public class KEYBaseSpockTest extends Specification {
    /*
    private static class KBSTestClass extends KEYBase {
	public KBSTestClass(){}

	public KBSTestClass(Name name, int type, int dclass, long ttl,
			 int flags, int proto, int alg, byte[] key ) {
	    super(name, type, dclass, ttl, flags, proto, alg, key)
	}
	
	public Record getObject() {
	    return null
	}

	void rdataFromString(Tokenizer st, Name origin) throws IOException {
	}
    }
    */

    def "test_ctor"() throws TextParseException {
	KBSTestClass tc = new KBSTestClass()
	expect:
	0 == tc.getFlags()
	0 == tc.getProtocol()
	0 == tc.getAlgorithm()
	null == tc.getKey()

	when:
	Name n = Name.fromString("my.name.")
	def byte[] key = [ 0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7,
		    0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF ] 

	tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0xFF, 0xF, 0xE, key)

	then:
	n == tc.getName()
	Type.KEY == tc.getType()
	DClass.IN == tc.getDClass()
	100L == tc.getTTL()
	0xFF == tc.getFlags()
	0xF == tc.getProtocol()
	0xE == tc.getAlgorithm()
	key == tc.getKey()
    }

    def "test_rrFromWire"() throws IOException {
	def byte[] raw = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x19, 1, 2, 3, 4, 5 ]
	DNSInput dnsin = new DNSInput(raw)
	
	KBSTestClass tc = new KBSTestClass()
	tc.rrFromWire(dnsin)
	expect:
	0xABCD == tc.getFlags()
	0xEF == tc.getProtocol()
	0x19 == tc.getAlgorithm()

	when:
	def byte[] b_a = [ 1, 2, 3, 4, 5 ]
	then:
	b_a == tc.getKey()

	when:
	raw = [ (byte)0xBA, (byte)0xDA, (byte)0xFF, (byte)0x28 ] 
	dnsin = new DNSInput(raw)
	
	tc = new KBSTestClass()
	tc.rrFromWire(dnsin)
	then:
	0xBADA == tc.getFlags()
	0xFF == tc.getProtocol()
	0x28 == tc.getAlgorithm()
	null ==tc.getKey()
    }
    
    def "test_rrToString"() throws IOException, TextParseException {
	Name n = Name.fromString("my.name.")
	def byte[] key = [ 0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7,
		    0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF ] 

	KBSTestClass tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0xFF, 0xF, 0xE, null)

	String out = tc.rrToString()
	expect:
	"255 15 14" == out

	when:
	tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0xFF, 0xF, 0xE, key)
	out = tc.rrToString()
	then:
	("255 15 14 " + Base64.toString(key)) == out

	when:
	Options.set("multiline")
	out = tc.rrToString()
	then:
	("255 15 14 (\n\t" + Base64.toString(key) + " ) ; key_tag = 18509") == out
	cleanup:
	Options.unset("multiline")
    }
    
    def "test_getFootprint"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	def byte []key = [ 0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7,
		    0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF ] 
	    
	KBSTestClass tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0xFF, 0xF, DNSSEC.Algorithm.RSAMD5, key)
	
	int foot = tc.getFootprint()
	// second-to-last and third-to-last bytes of key for RSAMD5
	expect:
	0xD0E == foot
	foot == tc.getFootprint()

	when:
	// key with an odd number of bytes
	def byte[] b_a = [ 0x12, 0x34, 0x56 ]
	tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0x89AB, 0xCD, 0xEF, b_a )

	// rrToWire gives: { 0x89, 0xAB, 0xCD, 0xEF, 0x12, 0x34, 0x56 }
	// 89AB + CDEF + 1234 + 5600 = 1BCFE
	// 1BFCE + 1 = 1BFCF & FFFF = BFCF
	foot = tc.getFootprint()
	then:
	0xBFCF == foot
	foot == tc.getFootprint()

	// empty
	when:
	tc = new KBSTestClass()
	then:
	0 == tc.getFootprint()
    }

    def "test_rrToWire"() throws IOException, TextParseException {
	Name n = Name.fromString("my.name.")
	def byte[] key = [ 0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7,
		    0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF ] 

	KBSTestClass tc = new KBSTestClass(n, Type.KEY, DClass.IN, 100L, 0x7689, 0xAB, 0xCD, key)
       
	def byte[] exp = [ (byte)0x76, (byte)0x89, (byte)0xAB, (byte)0xCD, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ]

	DNSOutput o = new DNSOutput()

	// canonical
	tc.rrToWire(o, null, true)
	expect:
	exp == o.toByteArray()

	when:
	// not canonical
	o = new DNSOutput()
	tc.rrToWire(o, null, false)
	then:
	exp == o.toByteArray()
    }

}
