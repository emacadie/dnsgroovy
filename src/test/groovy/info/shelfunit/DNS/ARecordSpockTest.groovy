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

public class ARecordSpockTest extends Specification {
    def Name m_an, m_rn
    def InetAddress m_addr
    def String m_addr_string
    def byte[] m_addr_bytes
    def long m_ttl

    def setup() throws TextParseException, UnknownHostException {
	m_an = Name.fromString("My.Absolute.Name.")
	m_rn = Name.fromString("My.Relative.Name")
	m_addr_string = "193.160.232.5"
	m_addr = InetAddress.getByName(m_addr_string)
	m_addr_bytes = m_addr.getAddress()
	m_ttl = 0x13579
    }

    def "test_ctor_0arg"() throws UnknownHostException {
	ARecord ar = new ARecord()
	expect:
	null == ar.getName()
	0 == ar.getType()
	0 == ar.getDClass()
	0 == ar.getTTL().intValue()
	// InetAddress.getByName("0.0.0.0") == ar.getAddress()
    }
    
    def "test_getObject"() {
	ARecord ar = new ARecord()
	Record r = ar.getObject()
        expect: 
        r instanceof ARecord
    }
    
    def "test_ctor_4arg"() {
	when:
	ARecord ar = new ARecord(m_an, DClass.IN, m_ttl, m_addr)
	then:
	m_an == ar.getName()
	Type.A == ar.getType()
	DClass.IN == ar.getDClass()
	m_ttl == ar.getTTL()
	m_addr == ar.getAddress()

	// a relative name
	when:
	    new ARecord(m_rn, DClass.IN, m_ttl, m_addr)
	then:
	thrown( RelativeNameException.class )

	// an IPv6 address
	when:
	    new ARecord(m_an, DClass.IN, m_ttl,
			InetAddress.getByName("2001:0db8:85a3:08d3:1319:8a2e:0370:7334"))
	    fail("IllegalArgumentException not thrown")
	then:
	thrown( IllegalArgumentException.class )
    }

    /*
    public void test_rrFromWire() throws IOException
    {
	DNSInput di = new DNSInput(m_addr_bytes)
	ARecord ar = new ARecord()

	// ar.rrFromWire(di)
	
	// m_addr == ar.getAddress()
    }
    */

    def "test_rdataFromString"() throws IOException {
	Tokenizer t = new Tokenizer(m_addr_string)
	ARecord ar = new ARecord()

	ar.rdataFromString(t, null)

	// m_addr == ar.getAddress()

	// invalid address
	t = new Tokenizer("193.160.232")
	ar = new ARecord()
	when:
	    ar.rdataFromString(t, null)
	then:
	thrown( TextParseException.class )
    }
    
    def "test_rrToString"() {
	ARecord ar = new ARecord(m_an, DClass.IN, m_ttl, m_addr)
	expect: m_addr_string == ar.rrToString()
    }
    
    def "test_rrToWire"() {
	when:
	ARecord ar = new ARecord(m_an, DClass.IN, m_ttl, m_addr)
	DNSOutput dout = new DNSOutput()

	ar.rrToWire(dout, null, true)
	then:
	m_addr_bytes == dout.toByteArray()
	when:
	dout = new DNSOutput()
	ar.rrToWire(dout, null, false)
	then:
	m_addr_bytes == dout.toByteArray()
    }

}
