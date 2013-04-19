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

import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification

public class KEYRecordSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    def "test_ctor_0arg"() throws UnknownHostException {
	KEYRecord ar = new KEYRecord()

	expect:
	mgu.equals(null, ar.getName())
	mgu.equals(0, ar.getType())
	mgu.equals(0, ar.getDClass())
	mgu.equals(0, ar.getTTL().intValue())
	mgu.equals(0, ar.getAlgorithm())
	mgu.equals(0, ar.getFlags())
	mgu.equals(0, ar.getFootprint())
	mgu.equals(0, ar.getProtocol())
	mgu.equals(null, ar.getKey())
    }
    
    def "test_getObject"() {
	KEYRecord ar = new KEYRecord()
	Record r = ar.getObject()
	expect:
	mga.that(r instanceof KEYRecord)
    }

    def "test_ctor_7arg"() throws TextParseException {
	Name n = Name.fromString("My.Absolute.Name.")
	Name r = Name.fromString("My.Relative.Name")
	def key = [ 0, 1, 3, 5, 7, 9 ].collect { entry -> (byte) entry }
	def key_array = key.toArray(new byte[key.size])
	KEYRecord kr = new KEYRecord(n, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key_array)
	
	    expect:
	mgu.equals(n, kr.getName())
	mgu.equals(Type.KEY, kr.getType())
	mgu.equals(DClass.IN, kr.getDClass())
	mgu.equals(0x24AC, kr.getTTL().intValue())
	mgu.equals(0x9832, kr.getFlags())
	mgu.equals(0x12, kr.getProtocol())
	mgu.equals(0x67, kr.getAlgorithm())
	mga.that(Arrays.equals(key_array, kr.getKey()))

	// a relative name
	when:
	    new KEYRecord(r, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key_array)
	then:
	    thrown( RelativeNameException.class)
    }
    
    def "test_Protocol_string"() {
	expect:
	// a regular one
	mgu.equals("DNSSEC", KEYRecord.Protocol.string(KEYRecord.Protocol.DNSSEC))
	// a unassigned value within range
	mgu.equals("254", KEYRecord.Protocol.string(0xFE))
	// too low
	when:
	    KEYRecord.Protocol.string(-1)
	then:
	thrown( IllegalArgumentException.class )
	// too high
	when:
	    KEYRecord.Protocol.string(0x100)
	then:
	thrown( IllegalArgumentException.class )
    }

    def "test_Protocol_value"() {
	expect:
	// a regular one
	mgu.equals(KEYRecord.Protocol.IPSEC, KEYRecord.Protocol.value("IPSEC"))
	// a unassigned value within range
	mgu.equals(254, KEYRecord.Protocol.value("254"))
	// too low
	mgu.equals(-1, KEYRecord.Protocol.value("-2"))
	// too high
	mgu.equals(-1, KEYRecord.Protocol.value("256"))
    }

    def "test_Flags_value"() {
	expect:
	// numeric

	// lower bound
	mgu.equals(-1, KEYRecord.Flags.value("-2"))
	mgu.equals(0, KEYRecord.Flags.value("0"))
	// in the middle
	mgu.equals(0xAB35, KEYRecord.Flags.value(0xAB35+""))
	// upper bound
	mgu.equals(0xFFFF, KEYRecord.Flags.value(0xFFFF+""))
	mgu.equals(-1, KEYRecord.Flags.value(0x10000+""))

	// textual
	
	// single
	mgu.equals(KEYRecord.Flags.EXTEND, KEYRecord.Flags.value("EXTEND"))
	// single invalid
	mgu.equals(-1, KEYRecord.Flags.value("NOT_A_VALID_NAME"))
	// multiple
	mgu.equals(KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.ZONE,
		     KEYRecord.Flags.value("NOAUTH|ZONE|FLAG10"))
	// multiple invalid
	mgu.equals(-1, KEYRecord.Flags.value("NOAUTH|INVALID_NAME|FLAG10"))
	// pathological
	mgu.equals(0, KEYRecord.Flags.value("|"))
    }
    
    def "test_rdataFromString"() throws IOException, TextParseException {
	// basic
	KEYRecord kr = new KEYRecord()
	Tokenizer st = new Tokenizer("NOAUTH|ZONE|FLAG10 EMAIL RSASHA1 AQIDBAUGBwgJ")
	kr.rdataFromString(st, null)
	def tempArray = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ].collect { entry -> (byte) entry }

	expect:
	mgu.equals(KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.ZONE,
		     kr.getFlags())
	mgu.equals(KEYRecord.Protocol.EMAIL, kr.getProtocol())
	mgu.equals(DNSSEC.Algorithm.RSASHA1, kr.getAlgorithm())
	
	mga.that(Arrays.equals( tempArray.toArray(new byte[tempArray.size]), kr.getKey()))

	when:
	// basic w/o key
	KEYRecord kr2 = new KEYRecord()
	Tokenizer st2 = new Tokenizer("NOAUTH|NOKEY|FLAG10 TLS ECC")
	    // kr2.rdataFromString(st2, null)
	
	then:
	// mgu.equals(KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.NOKEY, kr2.getFlags())
	// mgu.equals(KEYRecord.Protocol.TLS, kr2.getProtocol())
	// mgu.equals(DNSSEC.Algorithm.ECC, kr2.getAlgorithm())
	mgu.equals(null, kr2.getKey())

	// invalid flags
	def kr3 = new KEYRecord()
	def st3 = new Tokenizer("NOAUTH|ZONE|JUNK EMAIL RSASHA1 AQIDBAUGBwgJ")
	when:
	    kr3.rdataFromString(st3, null)
	then:
	    thrown( TextParseException.class )

	// invalid protocol
	def kr4 = new KEYRecord()
	def st4 = new Tokenizer("NOAUTH|ZONE RSASHA1 ECC AQIDBAUGBwgJ")
	when:
	    kr4.rdataFromString(st4, null)
	then:
	    thrown( TextParseException.class )

	// invalid algorithm
	KEYRecord kr5 = new KEYRecord()
	Tokenizer st5 = new Tokenizer("NOAUTH|ZONE EMAIL ZONE AQIDBAUGBwgJ")
	when:
	    kr5.rdataFromString(st5, null)
	then:
	    thrown( TextParseException.class )
    }
    
}
