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
import java.util.ArrayList
import java.util.List
import spock.lang.Specification
import org.xbill.DNS.APLRecord.Element

import org.xbill.DNS.*

public class APLRecordSpockTest extends Specification {

	InetAddress m_addr4
	InetAddress m_addr6
	Name m_an, m_rn
	long m_ttl
	ArrayList m_elements
	String m_addr4_string
	byte[] m_addr4_bytes
	String m_addr6_string
	byte[] m_addr6_bytes

	protected void setup_Element_init() throws TextParseException,
				      UnknownHostException
	{
	    m_addr4 = InetAddress.getByName("193.160.232.5")
	    m_addr6 = InetAddress.getByName("2001:db8:85a3:8d3:1319:8a2e:370:7334")
	}
	
	def "test_valid_IPv4_setup_Element_init"() {
	    setup_Element_init()
	    Element el = new Element(true, m_addr4, 16)
	    expect:
	    Address.IPv4 == el.family
	    true == el.negative
	    m_addr4 == el.address
	    16 == el.prefixLength
	}
	
	def "test_invalid_IPv4_setup_Element_init"() {
	    setup_Element_init()
	    when:
		new Element(true, m_addr4, 33)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
	def "test_valid_IPv6_setup_Element_init"() {
	    setup_Element_init()
	    Element el = new Element(false, m_addr6, 74)
	    expect:
	    Address.IPv6 == el.family
	    false == el.negative
	    m_addr6 == el.address
	    74 == el.prefixLength
	}
	
	def "test_invalid_IPv6_setup_Element_init"() {
	    setup_Element_init()
	    when:
		new Element(true, m_addr6, 129)
	    then:
	    thrown( IllegalArgumentException.class )
	}

	protected void setup_init() throws TextParseException,
				      UnknownHostException
	{
	    m_an = Name.fromString("My.Absolute.Name.")
	    m_rn = Name.fromString("My.Relative.Name")
	    m_ttl = 0x13579
	    m_addr4_string = "193.160.232.5"
	    m_addr4 = InetAddress.getByName(m_addr4_string)
	    m_addr4_bytes = m_addr4.getAddress()
	    
	    m_addr6_string = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	    m_addr6 = InetAddress.getByName(m_addr6_string)
	    m_addr6_bytes = m_addr6.getAddress()
	    
	    m_elements = new ArrayList(2)
	    Element e = new Element(true, m_addr4, 12)
	    m_elements.add(e)
	    
	    e = new Element(false, m_addr6, 64)
	    m_elements.add(e)
	}
	
	def "test_0arg_init"() throws UnknownHostException {
	    setup_init()
	    APLRecord ar = new APLRecord()
	    expect:
	    null == ar.getName()
	    0 == ar.getType()
	    0 == ar.getDClass()
	    0 == ar.getTTL().intValue()
	    null == ar.getElements()
	}

    def "test_getObject_init"() {
	    setup_init()
	    APLRecord ar = new APLRecord()
	    Record r = ar.getObject()
	    expect:
	    r instanceof APLRecord
    }

	def "test_4arg_basic_init"() {
	    setup_init()
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, m_elements)
	    expect:
	    m_an == ar.getName()
	    Type.APL == ar.getType()
	    DClass.IN == ar.getDClass()
	    m_ttl == ar.getTTL()
	    m_elements == ar.getElements()
	}
	
	def "test_4arg_empty_elements_init"() {
	    setup_init()
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, new ArrayList())
	    expect:
	    new ArrayList() == ar.getElements()
	}
    
        def "test_4arg_relative_name_init"() {
	    setup_init()
	    when:
		new APLRecord(m_rn, DClass.IN, m_ttl, m_elements)
	    then:
	    thrown( RelativeNameException.class )
	}
	
	def "test_4arg_invalid_elements_init"() {
	    setup_init()
	    m_elements = new ArrayList()
	    m_elements.add(new Object())
	    when:
		new APLRecord(m_an, DClass.IN, m_ttl, m_elements)
	
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
	protected void setup_rrFromWire() throws TextParseException,
				      UnknownHostException {
	    m_addr4 = InetAddress.getByName("193.160.232.5")
	    m_addr4_bytes = m_addr4.getAddress()
	    
	    m_addr6 = InetAddress.getByName("2001:db8:85a3:8d3:1319:8a2e:370:7334")
	    m_addr6_bytes = m_addr6.getAddress()
	}
	
	def "test_validIPv4_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [ 0, 1, 8, (byte)0x84, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3] ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(true, m_addr4, 8))
	    expect:
	    exp == ar.getElements()
	}
	
	def "test_validIPv4_short_address_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [ 0, 1, 20, (byte)0x83, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2] ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    InetAddress a = InetAddress.getByName("193.160.232.0")
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(true, a, 20))
	    expect:
	    exp == ar.getElements()
	}
	
	def "test_invalid_IPv4_prefix_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [ 0, 1, 33, (byte)0x84, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3] ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    when:
		ar.rrFromWire(di)
	    then:
	    thrown( WireParseException.class )
	}
	
	def "test_invalid_IPv4_length_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [ 0, 1, 8, (byte)0x85, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3], 10 ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    when:
		ar.rrFromWire(di)
	    then:
	    thrown( WireParseException.class )
	}
	
    def "test_multiple_validIPv4_rrFromWire"() throws IOException {
	setup_rrFromWire()
	    def raw = [ 0, 1, 8, (byte)0x84, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3],
			0, 1, 30, (byte)0x4,
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3] ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(true, m_addr4, 8))
	    exp.add(new Element(false, m_addr4, 30))
	    expect:
	    exp == ar.getElements()
	}
	
    def "test_validIPv6_rrFromWire"() throws IOException {
	setup_rrFromWire()
	    def raw = [ 0, 2, (byte)115, (byte)0x10, 
			m_addr6_bytes[0], m_addr6_bytes[1],
			m_addr6_bytes[2], m_addr6_bytes[3],
			m_addr6_bytes[4], m_addr6_bytes[5],
			m_addr6_bytes[6], m_addr6_bytes[7],
			m_addr6_bytes[8], m_addr6_bytes[9],
			m_addr6_bytes[10], m_addr6_bytes[11],
			m_addr6_bytes[12], m_addr6_bytes[13],
			m_addr6_bytes[14], m_addr6_bytes[15] ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(false, m_addr6, 115))
	    expect:
	    exp == ar.getElements()
	}

	def "test_valid_nonIP_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [ 0, 3, (byte)130, (byte)0x85, 1, 2, 3, 4, 5 ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    List l = ar.getElements()
	    expect:
	    1 == l.size()
	    
	    when:
	    Element el = (Element)l.get(0)
	    then:
	    3 == el.family
	    true == el.negative
	    130 == el.prefixLength

	    when:
	    def b_a = [ 1, 2, 3, 4, 5 ].collect { entry -> (byte) entry }
	    then:
	    b_a.toArray(new byte[b_a.size]) == (byte[])el.address
	}
    
	protected void setup_rdataFromString() throws TextParseException,
				      UnknownHostException {
	    m_addr4_string = "193.160.232.5"
	    m_addr4 = InetAddress.getByName(m_addr4_string)
	    m_addr4_bytes = m_addr4.getAddress()
	    
	    m_addr6_string = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	    m_addr6 = InetAddress.getByName(m_addr6_string)
	    m_addr6_bytes = m_addr6.getAddress()
	}

    def "test_validIPv4_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:" + m_addr4_string + "/11\n")
	    APLRecord ar = new APLRecord()
	    ar.rdataFromString(t, null)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(false, m_addr4, 11))

	    expect:
	    exp == ar.getElements()
	    
	    // make sure extra token is put back
	    Tokenizer.EOL == t.get().type
	}
	
    def "test_valid_multi_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:" + m_addr4_string + "/11 !2:" + m_addr6_string + "/100")
	    APLRecord ar = new APLRecord()
	    ar.rdataFromString(t, null)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(false, m_addr4, 11))
	    exp.add(new Element(true, m_addr6, 100))
	    expect:
	    exp == ar.getElements()
	}
	
    def "test_validIPv6_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("!2:" + m_addr6_string + "/36\n")
	    APLRecord ar = new APLRecord()
	    ar.rdataFromString(t, null)
	    
	    ArrayList exp = new ArrayList()
	    exp.add(new Element(true, m_addr6, 36))
	    expect:
	    exp == ar.getElements()
	    
	    // make sure extra token is put back
	    Tokenizer.EOL == t.get().type
	}
	
    def "test_no_colon_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("!1192.68.0.1/20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_colon_and_slash_swapped_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("!1/192.68.0.1:20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_no_slash_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("!1:192.68.0.1|20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_empty_family_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("!:192.68.0.1/20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
    def "test_malformed_family_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("family:192.68.0.1/20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_invalid_family_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("3:192.68.0.1/20")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
    def "test_empty_prefix_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:192.68.0.1/")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}

	def "test_malformed_prefix_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:192.68.0.1/prefix")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_invalid_prefix_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:192.68.0.1/33")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
	def "test_empty_address_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:/33")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
	
    def "test_malformed_address_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("1:A.B.C.D/33")
	    APLRecord ar = new APLRecord()
	    when:
		ar.rdataFromString(t, null)
	    then:
	    thrown( TextParseException.class )
	}
    

    def "test_rrToString"() throws TextParseException,
				      UnknownHostException {
	    m_an = Name.fromString("My.Absolute.Name.")
	    m_rn = Name.fromString("My.Relative.Name")
	    m_ttl = 0x13579
	    m_addr4_string = "193.160.232.5"
	    m_addr4 = InetAddress.getByName(m_addr4_string)
	    m_addr4_bytes = m_addr4.getAddress()
	    
	    m_addr6_string = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	    m_addr6 = InetAddress.getByName(m_addr6_string)
	    m_addr6_bytes = m_addr6.getAddress()
	    
	    m_elements = new ArrayList(2)
	    Element e = new Element(true, m_addr4, 12)
	    m_elements.add(e)
	    
	    e = new Element(false, m_addr6, 64)
	    m_elements.add(e)
	
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, m_elements)
	    expect:
	    "!1:" + m_addr4_string + "/12 2:" + m_addr6_string + "/64" == 			 ar.rrToString()
	}

	protected void setup_rrToWire() throws TextParseException, UnknownHostException {
	    m_an = Name.fromString("My.Absolute.Name.")
	    m_rn = Name.fromString("My.Relative.Name")
	    m_ttl = 0x13579
	    m_addr4_string = "193.160.232.5"
	    m_addr4 = InetAddress.getByName(m_addr4_string)
	    m_addr4_bytes = m_addr4.getAddress()
	    
	    m_addr6_string = "2001:db8:85a3:8d3:1319:8a2e:370:7334"
	    m_addr6 = InetAddress.getByName(m_addr6_string)
	    m_addr6_bytes = m_addr6.getAddress()
	    
	    m_elements = new ArrayList(2)
	    Element e = new Element(true, m_addr4, 12)
	    m_elements.add(e)
	    
	    e = new Element(false, m_addr6, 64)
	    m_elements.add(e)
	}
	
	def "test_empty_rrToWire"() {
	    setup_rrToWire()
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, new ArrayList())
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    new byte[0] == dout.toByteArray()
	}
	
	def "test_basic_rrToWire"() {
	    setup_rrToWire()
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, m_elements)
	    
	    def exp = [ 0, 1, 12, (byte)0x84, 
			m_addr4_bytes[0], m_addr4_bytes[1],
			m_addr4_bytes[2], m_addr4_bytes[3],
			0, 2, 64, 0x10,
			m_addr6_bytes[0], m_addr6_bytes[1],
			m_addr6_bytes[2], m_addr6_bytes[3],
			m_addr6_bytes[4], m_addr6_bytes[5],
			m_addr6_bytes[6], m_addr6_bytes[7],
			m_addr6_bytes[8], m_addr6_bytes[9],
			m_addr6_bytes[10], m_addr6_bytes[11],
			m_addr6_bytes[12], m_addr6_bytes[13],
			m_addr6_bytes[14], m_addr6_bytes[15] ].collect { entry -> (byte) entry }
	    
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == dout.toByteArray()
	}
	
	def "test_non_IP_rrToWire"() throws IOException {
	    setup_rrToWire()
	    def exp = [ 0, 3, (byte)130, (byte)0x85, 
			1, 2, 3, 4, 5 ].collect { entry -> (byte) entry }
	    
	    DNSInput di = new DNSInput(exp.toArray(new byte[exp.size]))
	    APLRecord ar = new APLRecord()
	    ar.rrFromWire(di)
	    
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == dout.toByteArray()
	}
	
	def "test_address_with_embedded_zero_rrToWire"() throws UnknownHostException {
	    setup_rrToWire()
	    InetAddress a = InetAddress.getByName("232.0.11.1")
	    ArrayList elements = new ArrayList()
	    elements.add(new Element(true, a, 31))
	    
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, elements)
	    
	    def exp = [ 0, 1, 31, (byte)0x84, (byte)232, 0, 11, 1 ].collect { entry -> (byte) entry }
	    
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == dout.toByteArray()
	}
	
	def "test_short_address_rrToWire"() throws UnknownHostException {
	    setup_rrToWire()
	    InetAddress a = InetAddress.getByName("232.0.11.0")
	    ArrayList elements = new ArrayList()
	    elements.add(new Element(true, a, 31))
	    
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, elements)
	    
	    def exp = [ 0, 1, 31, (byte)0x83, (byte)232, 0, 11 ].collect { entry -> (byte) entry }
	    
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == dout.toByteArray()
	}
	
	def "test_wildcard_address_rrToWire"() throws UnknownHostException {
	    setup_rrToWire()
	    InetAddress a = InetAddress.getByName("0.0.0.0")
	    ArrayList elements = new ArrayList()
	    elements.add(new Element(true, a, 31))
	    
	    APLRecord ar = new APLRecord(m_an, DClass.IN, m_ttl, elements)
	    
	    def exp = [ 0, 1, 31, (byte)0x80 ].collect { entry -> (byte) entry }
	    
	    DNSOutput dout = new DNSOutput()
	    
	    ar.rrToWire(dout, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == dout.toByteArray()
	}
}
