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
import spock.lang.Specification
import org.xbill.DNS.DNSInput
import org.xbill.DNS.DNSOutput
import org.xbill.DNS.Header
import org.xbill.DNS.Opcode
import org.xbill.DNS.Rcode
import org.xbill.DNS.Flags

public class HeaderSpockTest extends Specification {
    private Header m_h

    def setup() {
	m_h = new Header(0xABCD) // 43981
    }
    
    def "test_fixture_state"() {
	expect:
	0xABCD == m_h.getID()

	when:
	boolean[] flags = m_h.getFlags()
	then:
	flags.each() { nextFlag -> !nextFlag }
	0 == m_h.getRcode()
	0 == m_h.getOpcode()
	0 == m_h.getCount(0)
	0 == m_h.getCount(1)
	0 == m_h.getCount(2)
	0 == m_h.getCount(3)
    }
    
    def "test_ctor_0arg"() {
	m_h = new Header()
	expect:
	(0 <= m_h.getID()) && (m_h.getID() < 0xFFFF)
	
	when:
	boolean[] flags = m_h.getFlags()
	then:
	flags.each() { nextFlag -> !nextFlag }
	0 == m_h.getRcode()
	0 == m_h.getOpcode()
	0 == m_h.getCount(0)
	0 == m_h.getCount(1)
	0 == m_h.getCount(2)
	0 == m_h.getCount(3)
    }
    
    def "test_ctor_DNSInput"() throws IOException {
	when:
	def raw = [ (byte)0x12, (byte)0xAB, // ID
		    (byte)0x8F, (byte)0xBD, // flags: 1 0001 1 1 1 1 011 1101
		    (byte)0x65, (byte)0x1C, // QDCOUNT
		    (byte)0x10, (byte)0xF0, // ANCOUNT
		    (byte)0x98, (byte)0xBA, // NSCOUNT
		    (byte)0x71, (byte)0x90 ].collect{ entry -> (byte) entry } // ARCOUNT

	m_h = new Header(new DNSInput(raw.toArray(new byte[raw.size]) ) )
	boolean[] flags = m_h.getFlags()

	then:
	0x12AB == m_h.getID()
	flags[0]
	1 == m_h.getOpcode()
	flags[5]
	flags[6]
	flags[7]
	flags[8]
	!flags[9]
	flags[10]
	flags[11]
	0xD == m_h.getRcode()
	0x651C == m_h.getCount(0)
	0x10F0 == m_h.getCount(1)
	0x98BA == m_h.getCount(2)
	0x7190 == m_h.getCount(3)
    }
    
    def "test_toWire"() throws IOException  {
	given:
	def raw = [ (byte)0x12, (byte)0xAB, // ID
		    (byte)0x8F, (byte)0xBD, // flags: 1 0001 1 1 1 1 011 1101
		    (byte)0x65, (byte)0x1C, // QDCOUNT
		    (byte)0x10, (byte)0xF0, // ANCOUNT
		    (byte)0x98, (byte)0xBA, // NSCOUNT
		    (byte)0x71, (byte)0x90 ].collect{ entry -> (byte) entry } // ARCOUNT
	
	m_h = new Header(raw.toArray(new byte[raw.size]))
	
	DNSOutput dout = new DNSOutput()
	m_h.toWire(dout)
	when:
	byte[] out = dout.toByteArray()
	then:
	12 == out.length
	out.eachWithIndex() {
	    nextOut, i ->
	    raw[i] == nextOut
	}
	
	when:
	m_h.setOpcode(0xA) // 1010
	then: 0xA == m_h.getOpcode()

	when:
	m_h.setRcode(0x7)  // 0111

	// flags is now: 1101 0111 1011 0111

	raw[2] = (byte)0xD7
	raw[3] = (byte)0xB7

	out = m_h.toWire()
        then:
	12 == out.length
	out.eachWithIndex() {
	    nextOut, i ->
	    raw[i] == nextOut
	}
    }

    def "test_flags"() {
	m_h.setFlag(0)
	m_h.setFlag(5)
	expect:
	m_h.getFlag(0)
	m_h.getFlags()[0]
	m_h.getFlag(5)
	m_h.getFlags()[5]
	 
	when:
	m_h.unsetFlag(0)
	then:
	!m_h.getFlag(0)
	!m_h.getFlags()[0]
	m_h.getFlag(5)
	m_h.getFlags()[5]

	when:
	m_h.unsetFlag(5)
	then:
	!m_h.getFlag(0)
	!m_h.getFlags()[0]
	!m_h.getFlag(5)
	!m_h.getFlags()[5]
	  
	when:
	boolean[] flags = m_h.getFlags()
        then:
	flags.eachWithIndex() {
	    nextFlag, i ->
	    if ( !( ( i > 0 && i < 5 ) || i > 11 ) ) {
		!nextFlag
	    }
	} 
    }
    
    def "test_flags_invalid"() {
	when: m_h.setFlag(-1)
	then: thrown( IllegalArgumentException.class )
	when: m_h.setFlag(1)
	then: thrown( IllegalArgumentException.class )
	when: m_h.setFlag(16)
	then: thrown( IllegalArgumentException.class )
	when: m_h.unsetFlag(-1)
	then: thrown( IllegalArgumentException.class )
	when: m_h.unsetFlag(13)
	then: thrown( IllegalArgumentException.class )
	when: m_h.unsetFlag(16)
	then: thrown( IllegalArgumentException.class )
	when: m_h.getFlag(-1)
	then: thrown( IllegalArgumentException.class )
	when: m_h.getFlag(4)
	then: thrown( IllegalArgumentException.class )
	when: m_h.getFlag(16)
	then: thrown( IllegalArgumentException.class )
    }
    
    def "test_ID"() {
	expect: 0xABCD == m_h.getID()

        when:
	m_h = new Header()
	int id = m_h.getID()
	then:
	id == m_h.getID()
	(id >= 0) && (id < 0xffff)

	when:
	m_h.setID(0xDCBA)
	then:
	0xDCBA == m_h.getID()
    }

    def "test_setID_invalid"() {
	when: 
	    m_h.setID(0x10000)
	then:
	    thrown( IllegalArgumentException.class )
	when: 
	    m_h.setID(-1)
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_Rcode"() {
	expect:
	0 == m_h.getRcode()

	when:
	m_h.setRcode(0xA) // 1010
	then:
	0xA == m_h.getRcode()
	for ( i in 0..12 ) {
	    if ( ( i > 0 && i < 5 ) || i > 11 ){
		continue
	    }
	    !m_h.getFlag(i)
	}
    }
    
    def "test_setRcode_invalid"() {
	when: 
	    m_h.setRcode(-1)
	then:
	    thrown( IllegalArgumentException.class )
	
	when: 
	    m_h.setRcode(0x100)
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_Opcode"() {
	expect:
	0 == m_h.getOpcode()

	when:
	m_h.setOpcode(0xE) // 1110
	then:
	0xE == m_h.getOpcode()
	!m_h.getFlag(0)
	for ( i in 5..11 ) {
	    !m_h.getFlag(i)
	}
	0 == m_h.getRcode()
    }
    
    def "test_setOpcode_invalid"() {
	when: 
	    m_h.setOpcode(-1)
	then:
	    thrown( IllegalArgumentException.class )
	when: 
	    m_h.setOpcode(0x100)
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_Count"() {
	when:
	m_h.setCount(2, 0x1E)
	then:
	0 == m_h.getCount(0)
	0 == m_h.getCount(1)
	0x1E == m_h.getCount(2)
	0 == m_h.getCount(3)

	when: m_h.incCount(0)
	then: 1 == m_h.getCount(0)

	when: m_h.decCount(2)
	then: 0x1E-1 == m_h.getCount(2)
    }
    
    def "test_setCount_invalid"() {
	when: m_h.setCount(-1, 0)
	then: thrown( ArrayIndexOutOfBoundsException.class )
	when: m_h.setCount(4, 0)
	then: thrown( ArrayIndexOutOfBoundsException.class )

	when: m_h.setCount(0, -1)
	then: thrown( IllegalArgumentException.class )
	when: m_h.setCount(3, 0x10000)
	then: thrown( IllegalArgumentException.class )
    }

    def "test_getCount_invalid"() {
	when: m_h.getCount(-1)
	then: thrown( ArrayIndexOutOfBoundsException.class )
	when: m_h.getCount(4)
	then: thrown( ArrayIndexOutOfBoundsException.class )
    }

    def "test_incCount_invalid"() {
	m_h.setCount(1, 0xFFFF)
	when: m_h.incCount(1)
	then: thrown( IllegalStateException.class )
    }

    def "test_decCount_invalid"() {
	m_h.setCount(2, 0)
	when: m_h.decCount(2)
        then: thrown( IllegalStateException.class )
    }

    def "test_toString"() {
	when:
	m_h.setOpcode(Opcode.value("STATUS"))
	m_h.setRcode(Rcode.value("NXDOMAIN"))
	m_h.setFlag(0) // qr
	m_h.setFlag(7) // rd
	m_h.setFlag(8) // ra
	m_h.setFlag(11) // cd
	m_h.setCount(1, 0xFF)
	m_h.setCount(2, 0x0A)
	
	String text = m_h.toString()
	    
	then:
	text.indexOf("id: 43981") != -1
	text.indexOf("opcode: STATUS") != -1
	text.indexOf("status: NXDOMAIN") != -1
	text.indexOf(" qr ") != -1
	text.indexOf(" rd ") != -1
	text.indexOf(" ra ") != -1
	text.indexOf(" cd ") != -1
	text.indexOf("qd: 0 ") != -1
	text.indexOf("an: 255 ") != -1
	text.indexOf("au: 10 ") != -1
	text.indexOf("ad: 0 ") != -1
	
    }
    
    def "test_clone"() {
	when:
	m_h.setOpcode(Opcode.value("IQUERY"))
	m_h.setRcode(Rcode.value("SERVFAIL"))
	m_h.setFlag(0) // qr
	m_h.setFlag(7) // rd
	m_h.setFlag(8) // ra
	m_h.setFlag(11) // cd
	m_h.setCount(1, 0xFF)
	m_h.setCount(2, 0x0A)

	Header h2 = (Header)m_h.clone()

	then:
	m_h != h2
	m_h.getID() == h2.getID()
	for ( i in 0..16 ) {
	    if ( ( i > 0 && i < 5 ) || i > 11 ) {
		continue
	    }
	    m_h.getFlag(i) == h2.getFlag(i)
	}
	for ( i in 0..3 ) {
	    m_h.getCount(i) == h2.getCount(i)
	}
    }

}
