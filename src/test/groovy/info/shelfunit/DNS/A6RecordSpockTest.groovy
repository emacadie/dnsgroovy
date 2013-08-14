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

public class A6RecordSpockTest extends Specification {

    Name m_an, m_an2, m_rn
    InetAddress m_addr
    String m_addr_string, m_addr_string_canonical
    byte[] m_addr_bytes
    int m_prefix_bits
    long m_ttl

    protected void setup() throws TextParseException, UnknownHostException {
	m_an = Name.fromString("My.Absolute.Name.")
	m_an2 = Name.fromString("My.Second.Absolute.Name.")
	m_rn = Name.fromString("My.Relative.Name")
	m_addr_string = "2001:0db8:85a3:08d3:1319:8a2e:0370:7334"
	m_addr_string_canonical = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	m_addr = InetAddress.getByName(m_addr_string)
	m_addr_bytes = m_addr.getAddress()
	m_ttl = 0x13579
	m_prefix_bits = 9
    }

    def "test_ctor_0arg"() {
	when:
	A6Record ar = new A6Record()

	then:
	    ar.getName() == null
	    0 == ar.getType()
	    0 == ar.getDClass()
	    0 == ar.getTTL().intValue()
    }
    
    def "test_getObject"() {
	A6Record ar = new A6Record()
	Record r = ar.getObject()
	expect: r instanceof A6Record
    }

    def "test_ctor_6arg"() {
	when:
	A6Record ar = new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits, m_addr, null)
	then:
	    m_an == ar.getName()
	    Type.A6 == ar.getType()
	    DClass.IN ==  ar.getDClass()
	    m_ttl == ar.getTTL()
	    m_prefix_bits == ar.getPrefixBits()
	    m_addr == ar.getSuffix()
	    ar.getPrefix() == null

	// with the prefix name
	when:
	ar = new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits, m_addr, m_an2)
	then:
	    m_an == ar.getName()
	    Type.A6 == ar.getType()
	    DClass.IN == ar.getDClass()
	    m_ttl == ar.getTTL()
	    m_prefix_bits == ar.getPrefixBits()
	    m_addr == ar.getSuffix()
	    m_an2 == ar.getPrefix()

	// a relative name
	when: 
	    new A6Record(m_rn, DClass.IN, m_ttl, m_prefix_bits, m_addr, null)
	then:
	    thrown( RelativeNameException.class )

	// a relative prefix name
	when:
	    new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits, m_addr, m_rn)
	then:
	    thrown( RelativeNameException.class )

	// invalid prefix bits
	when:
	    new A6Record(m_rn, DClass.IN, m_ttl, 0x100, m_addr, null)
	then:
	    thrown( RelativeNameException.class )

	// an IPv4 address
	when:
	    new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits,
			InetAddress.getByName("192.168.0.1"), null)
	then:
	    thrown( IllegalArgumentException.class )
	    // thrown( UnknownHostException.class )
    }

    def "test_rrFromWire"() throws CloneNotSupportedException,
					 IOException,
					 UnknownHostException {
	// record with no prefix
	when:
	DNSOutput dout = new DNSOutput()
	dout.writeU8(0)
	dout.writeByteArray(m_addr_bytes)

	DNSInput din = new DNSInput(dout.toByteArray())
	A6Record ar = new A6Record()
	ar.rrFromWire(din)
	then:
	    0 == ar.getPrefixBits()
	    m_addr == ar.getSuffix()
	    ar.getPrefix() == null

	// record with 9 bit prefix (should result in 15 bytes of the address)
	when:
	dout = new DNSOutput()
	dout.writeU8(9)
	dout.writeByteArray(m_addr_bytes, 1, 15)
	dout.writeByteArray(m_an2.toWire())

	din = new DNSInput(dout.toByteArray())
	ar = new A6Record()
	ar.rrFromWire(din)
	then:
	    9 == ar.getPrefixBits()

	when:
	byte[] addr_bytes = (byte[])m_addr_bytes.clone()
	addr_bytes[0] = 0
	InetAddress exp = InetAddress.getByAddress(addr_bytes)
	then:
	    exp == ar.getSuffix()
	    m_an2 == ar.getPrefix()
    }
    
    def "test_rdataFromString"() throws CloneNotSupportedException,
					      IOException,
					      UnknownHostException {
	// record with no prefix
	when:
	Tokenizer t = new Tokenizer("0 " + m_addr_string)
	A6Record ar = new A6Record()
	ar.rdataFromString(t, null)
	then:
	0 ==  ar.getPrefixBits()
	m_addr ==  ar.getSuffix()
	ar.getPrefix() ==  null

	// record with 9 bit prefix.  In contrast to the rrFromWire method,
	// rdataFromString expects the entire 128 bits to be represented
	// in the string
	when:
	t = new Tokenizer("9 " + m_addr_string + " " + m_an2)
	ar = new A6Record()
	ar.rdataFromString(t, null)
	then:
	9 ==  ar.getPrefixBits()
	m_addr ==  ar.getSuffix()
	m_an2 ==  ar.getPrefix()

	// record with invalid prefixBits
	when:
	t = new Tokenizer("129")
	ar = new A6Record()
	ar.rdataFromString(t, null)
	then:
	thrown( TextParseException.class )

	// record with invalid ipv6 address
	when:
	t = new Tokenizer("0 " + m_addr_string.substring(4))
	ar = new A6Record()
	ar.rdataFromString(t, null)
	then:
	    thrown( TextParseException.class )
    }
    def "test_rrToString"() {
	A6Record ar = new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits, m_addr, m_an2)
	String exp = "" + m_prefix_bits + " " + m_addr_string_canonical + " " + m_an2
	String out = ar.rrToString()
	exp ==  out
    }
    
    def "test_rrToWire"() {
	// canonical form
	A6Record ar = new A6Record(m_an, DClass.IN, m_ttl, m_prefix_bits, m_addr, m_an2)
	DNSOutput dout = new DNSOutput()
	dout.writeU8(m_prefix_bits)
	dout.writeByteArray(m_addr_bytes, 1, 15)
	dout.writeByteArray(m_an2.toWireCanonical())

	byte[] exp = dout.toByteArray()
	
	dout = new DNSOutput()
	ar.rrToWire(dout, null, true)
	
	assertTrue(Arrays.equals(exp, dout.toByteArray()))

	// case sensitiveform
	dout = new DNSOutput()
	dout.writeU8(m_prefix_bits)
	dout.writeByteArray(m_addr_bytes, 1, 15)
	dout.writeByteArray(m_an2.toWire())

	exp = dout.toByteArray()
	
	dout = new DNSOutput()
	ar.rrToWire(dout, null, false)
	exp ==  dout.toByteArray()
    }

}
