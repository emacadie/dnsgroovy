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

import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification

import org.xbill.DNS.*

public class AAAARecordSpockTest extends Specification {

    def Name m_an, m_rn
    def InetAddress m_addr
    def String m_addr_string
    def byte[] m_addr_bytes
    def long m_ttl

    def setup() throws TextParseException, UnknownHostException {
	m_an = Name.fromString("My.Absolute.Name.")
	m_rn = Name.fromString("My.Relative.Name")
	m_addr_string = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	m_addr = InetAddress.getByName(m_addr_string)
	m_addr_bytes = m_addr.getAddress()
	m_ttl = 0x13579
    }

    def "test_ctor_0arg"() throws UnknownHostException {
	AAAARecord ar = new AAAARecord()
	expect:
	null ==  ar.getName()
	0 ==  ar.getType()
	0 ==  ar.getDClass()
	0L ==  ar.getTTL()
	null ==  ar.getAddress()
    }

    def "test_getObject"() {
        when:
	AAAARecord ar = new AAAARecord()
	Record r = ar.getObject()

	then:
	r instanceof AAAARecord
    }
    
    def "test_ctor_4arg"(){
	when:
	AAAARecord ar = new AAAARecord(m_an, DClass.IN, m_ttl, m_addr)
	then:
	m_an ==  ar.getName()
	Type.AAAA ==  ar.getType()
	DClass.IN ==  ar.getDClass()
	m_ttl ==  ar.getTTL()
	m_addr ==  ar.getAddress()

	// a relative name
	when:
	    new AAAARecord(m_rn, DClass.IN, m_ttl, m_addr)
	then:
	thrown( RelativeNameException.class)

	// an IPv4 address
	when:
	    new AAAARecord(m_an, DClass.IN, m_ttl,
			InetAddress.getByName("192.168.0.1"))
	
	then:
	thrown( IllegalArgumentException.class)
	    // catch( UnknownHostException e ){ fail(e.getMessage()) }
    }
    /*
TODO: FIX THIS
    def "test_rrFromWire"() throws IOException {
	when:
	DNSInput di = new DNSInput(m_addr_bytes)
	AAAARecord ar = new AAAARecord()

	ar.rrFromWire(di)
	then:
	m_addr ==  ar.getAddress()
    }
*/
    def "test_rdataFromString"() throws IOException {
	Tokenizer t = new Tokenizer(m_addr_string)
	AAAARecord ar = new AAAARecord()
	
	ar.rdataFromString(t, null)

	expect: m_addr ==  ar.getAddress()

	// invalid address
	Tokenizer t2 = new Tokenizer("193.160.232.1")
	AAAARecord ar2 = new AAAARecord()
	when:
	    ar2.rdataFromString(t2, null)
	then:
	thrown( TextParseException.class)
    }

    def "test_rrToString"() {
	AAAARecord ar = new AAAARecord(m_an, DClass.IN, m_ttl, m_addr)
	expect:
	m_addr_string ==  ar.rrToString()
    }

    def "test_rrToWire"() {
	AAAARecord ar = new AAAARecord(m_an, DClass.IN, m_ttl, m_addr)

	// canonical
	DNSOutput dout = new DNSOutput()
	when:
	ar.rrToWire(dout, null, true)
	then:
	m_addr_bytes == dout.toByteArray()

	// case sensitive
	DNSOutput dout2 = new DNSOutput()
	when:
	ar.rrToWire(dout2, null, false)
	then:
	m_addr_bytes == dout2.toByteArray()
    }
}
