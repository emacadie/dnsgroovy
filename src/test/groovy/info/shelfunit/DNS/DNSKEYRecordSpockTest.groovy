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

import	org.xbill.DNS.*

import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification

public class DNSKEYRecordSpockTest extends Specification {

    def "test_ctor_0arg"() throws UnknownHostException {
	when:
	DNSKEYRecord ar = new DNSKEYRecord()

	then:
	ar.getName() == null
	0 == ar.getType()
	0 == ar.getDClass()
	0 == ar.getTTL().intValue()
	0 == ar.getAlgorithm()
	0 == ar.getFlags()
	0 == ar.getFootprint()
	0 == ar.getProtocol()
	ar.getKey() == null
    }
    
    def "test_getObject"() {
	when:
	DNSKEYRecord ar = new DNSKEYRecord()
	Record r = ar.getObject()
	then:
	r instanceof DNSKEYRecord
    }
    
    def "test_ctor_7arg"() throws TextParseException {
	when:
	Name n = Name.fromString("My.Absolute.Name.")
	Name r = Name.fromString("My.Relative.Name")
	def key = [ 0, 1, 3, 5, 7, 9 ].collect{ entry -> (byte) entry }

	DNSKEYRecord kr = new DNSKEYRecord(n, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key.toArray(new byte[key.size]))
	
	then:
	n == kr.getName()
	Type.DNSKEY == kr.getType()
	DClass.IN == kr.getDClass()
	0x24AC == kr.getTTL().intValue()
	0x9832 == kr.getFlags()
	0x12 == kr.getProtocol()
	0x67 == kr.getAlgorithm()
	key.toArray(new byte[key.size]) == kr.getKey()

	// a relative name
	when:
	    new DNSKEYRecord(r, DClass.IN, 0x24AC, 0x9832, 0x12, 0x67, key.toArray(new byte[key.size]) )
	    // fail("RelativeNameException not thrown")
	then:
	thrown( RelativeNameException.class )
    }

    def "test_rdataFromString"() throws IOException, TextParseException {
	when:
	// basic
	DNSKEYRecord kr = new DNSKEYRecord()
	Tokenizer st = new Tokenizer(0xABCD + " " + 0x81 + " RSASHA1 AQIDBAUGBwgJ")
	kr.rdataFromString(st, null)
	def b_a = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ].collect{ entry -> ( byte ) entry }

	then:
	0xABCD == kr.getFlags()
	0x81 == kr.getProtocol()
	DNSSEC.Algorithm.RSASHA1 == kr.getAlgorithm()
	b_a.toArray(new byte[b_a.size]) == kr.getKey()

	// invalid algorithm

	when:
	    kr = new DNSKEYRecord()
	    st = new Tokenizer(0x1212 + " " + 0xAA + " ZONE AQIDBAUGBwgJ")
	    kr.rdataFromString(st, null)
	    // fail("TextParseException not thrown")
	then:
	thrown( TextParseException.class )
    }

}
