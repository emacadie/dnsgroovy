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
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification

public class KEYRecordSpockTest extends Specification {

    def "test_ctor_0arg"() throws UnknownHostException {
	KEYRecord ar = new KEYRecord()

	expect:
	null == ar.getName()
	0 == ar.getType()
	0 == ar.getDClass()
	0 == ar.getTTL().intValue()
	0 == ar.getAlgorithm()
	0 == ar.getFlags()
	0 == ar.getFootprint()
	0 == ar.getProtocol()
	null == ar.getKey()
    }
    
    def "test_getObject"() {
	KEYRecord ar = new KEYRecord()
	Record r = ar.getObject()
	expect:
	r instanceof KEYRecord
    }

    def "test_ctor_7arg"() throws TextParseException {
	Name n = Name.fromString("My.Absolute.Name.")
	Name r = Name.fromString("My.Relative.Name")
	def key = [ 0, 1, 3, 5, 7, 9 ].collect { entry -> (byte) entry }
	def key_array = key.toArray(new byte[key.size])
	KEYRecord kr = new KEYRecord(n, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key_array)
	
	    expect:
	n == kr.getName()
	Type.KEY == kr.getType()
	DClass.IN == kr.getDClass()
	0x24AC == kr.getTTL().intValue()
	0x9832 == kr.getFlags()
	0x12 == kr.getProtocol()
	0x67 == kr.getAlgorithm()
	key_array == kr.getKey()

	// a relative name
	when:
	    new KEYRecord(r, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key_array)
	then:
	    thrown( RelativeNameException.class)
    }
    
    def "test_Protocol_string"() {
	expect:
	"DNSSEC" == KEYRecord.Protocol.string(KEYRecord.Protocol.DNSSEC) // a regular one
	// a unassigned value within range
	"254" == KEYRecord.Protocol.string(0xFE)
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
	KEYRecord.Protocol.IPSEC == KEYRecord.Protocol.value("IPSEC")
	// a unassigned value within range
	254 == KEYRecord.Protocol.value("254")
	// too low
	-1 == KEYRecord.Protocol.value("-2")
	// too high
	-1 == KEYRecord.Protocol.value("256")
    }

    def "test_Flags_value"() {
	expect:
	// numeric
	-1 == KEYRecord.Flags.value("-2") // lower bound
	0 == KEYRecord.Flags.value("0")
	0xAB35 == KEYRecord.Flags.value(0xAB35+"") // in the middle
	0xFFFF == KEYRecord.Flags.value(0xFFFF+"") // upper bound
	-1 == KEYRecord.Flags.value(0x10000+"")
	// textual
	KEYRecord.Flags.EXTEND == KEYRecord.Flags.value("EXTEND") // single
	// single invalid
	-1 == KEYRecord.Flags.value("NOT_A_VALID_NAME")
	// multiple
	(KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.ZONE) == KEYRecord.Flags.value("NOAUTH|ZONE|FLAG10")
	-1 == KEYRecord.Flags.value("NOAUTH|INVALID_NAME|FLAG10") // multiple invalid
	0 == KEYRecord.Flags.value("|") // pathological
    }
    
    def "test_rdataFromString"() throws IOException, TextParseException {
	// basic
	KEYRecord kr = new KEYRecord()
	Tokenizer st = new Tokenizer("NOAUTH|ZONE|FLAG10 EMAIL RSASHA1 AQIDBAUGBwgJ")
	kr.rdataFromString(st, null)
	def tempArray = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ].collect { entry -> (byte) entry }

	expect:
	(KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.ZONE) == kr.getFlags()
	KEYRecord.Protocol.EMAIL == kr.getProtocol()
	DNSSEC.Algorithm.RSASHA1 == kr.getAlgorithm()
	tempArray.toArray(new byte[tempArray.size]) == kr.getKey()

	when:
	// basic w/o key
	KEYRecord kr2 = new KEYRecord()
	Tokenizer st2 = new Tokenizer("NOAUTH|NOKEY|FLAG10 TLS ECC")
	    // kr2.rdataFromString(st2, null)
	
	then:
	// KEYRecord.Flags.NOAUTH|KEYRecord.Flags.FLAG10|KEYRecord.Flags.NOKEY == kr2.getFlags()
	// KEYRecord.Protocol.TLS == kr2.getProtocol()
	// DNSSEC.Algorithm.ECC == kr2.getAlgorithm()
	null == kr2.getKey()

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
