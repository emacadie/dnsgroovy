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
// package org.xbill.DNS
package info.shelfunit.DNS

import java.io.IOException
import java.util.Arrays
import spock.lang.Specification

import org.xbill.DNS.Compression
import org.xbill.DNS.DNAMERecord
import org.xbill.DNS.DClass
import org.xbill.DNS.DNSInput
import org.xbill.DNS.DNSOutput
import org.xbill.DNS.Name
import org.xbill.DNS.NameTooLongException
import org.xbill.DNS.Options
import org.xbill.DNS.TextParseException
import org.xbill.DNS.WireParseException

public class NameSpockTest extends Specification {

    private final String m_abs = "WWW.DnsJava.org."
    private Name         m_abs_origin
    private final String m_rel = "WWW.DnsJava"
    private Name         m_rel_origin

	protected void setup_init() throws TextParseException {
	    m_abs_origin = Name.fromString("Orig.")
	    m_rel_origin = Name.fromString("Orig")
	}

        def "test_ctor_empty_init"() {
	setup_init()

	    when:
		new Name("")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_at_null_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("@")
	    expect:
	    !n.isAbsolute()
	    !n.isWild()
	    0 == n.labels()
	    0.shortValue() == n.length()
	}

	def "test_ctor_at_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("@", m_abs_origin)
	    expect:
	    m_abs_origin == n
	}
	    
	def "test_ctor_at_rel_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("@", m_rel_origin)
	    expect:	    
	    m_rel_origin == n
	}

	def "test_ctor_dot_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(".")
	    Name.root == n
	    !Name.root == n
	    1 == n.labels()
	    1 == n.length()
	}

	def "test_ctor_wildcard_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("*")
	    expect:
	    !n.isAbsolute()
	    n.isWild()
	    1 == n.labels()
	    2.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 1, '*' ]
	    then:
	    b1 == n.getLabel(0)
	    "*" == n.getLabelString(0)
	}

	def "test_ctor_abs_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs)
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    4 == n.labels()
	    17.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    b1 == n.getLabel(0)
	    "WWW" == n.getLabelString(0)
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    b2 == n.getLabel(1)
	    "DnsJava" == n.getLabelString(1)
	    when:
	    def byte[] b3 = [ 3, 'o', 'r', 'g' ]
	    then:
	    b3 == n.getLabel(2)
	    "org" == n.getLabelString(2)
	    when:
	    def byte[] b4 = [ 0 ]
	    then:
	    b4 == n.getLabel(3)
	    "" == n.getLabelString(3)
	}

	def "test_ctor_rel_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_rel)
	    expect:
	    !n.isAbsolute()
	    !n.isWild()
	    2 == n.labels()
	    12.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    b1 == n.getLabel(0)
	    "WWW" == n.getLabelString(0)
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    b2 == n.getLabel(1)
	    "DnsJava" == n.getLabelString(1)
	}

	def "test_ctor_7label_init"() throws TextParseException {
	setup_init()
	    // 7 is the number of label positions that are cached
	    Name n = new Name("a.b.c.d.e.f.")
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    7 == n.labels()
	    13.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    b1 == n.getLabel(0)
	    "a" == n.getLabelString(0)
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    b1 == n.getLabel(1)
	    "b" == n.getLabelString(1)
	    when:
	    b1 = [ 1, 'c' ]
	    then:
	   b1 == n.getLabel(2)
	    "c" == n.getLabelString(2)
	    when:
	    b1 = [ 1, 'd' ]
	    then:
	    b1 ==  n.getLabel(3)
	    "d" == n.getLabelString(3)
	    when:
	    b1 = [ 1, 'e' ]
	    then:
	    b1 == n.getLabel(4)
	    "e" == n.getLabelString(4)
	    when:
	    b1 = [ 1, 'f' ]
	    then:
	    b1 == n.getLabel(5)
	    "f" == n.getLabelString(5)
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(6)
	    "" == n.getLabelString(6)
	}

	def "test_ctor_8label_init"() throws TextParseException {
	setup_init()
	    // 7 is the number of label positions that are cached
	    Name n = new Name("a.b.c.d.e.f.g.")
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    8 == n.labels()
	    15.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    b1 == n.getLabel(0)
	    "a" == n.getLabelString(0)
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    b1 == n.getLabel(1)
	    "b" == n.getLabelString(1)
	    when:
	    b1 = [ 1, 'c' ]
	    then:
	   b1 == n.getLabel(2)
	    "c" == n.getLabelString(2)
	    when:
	    b1 = [ 1, 'd' ]
	    then:
	    b1 == n.getLabel(3)
	    "d" == n.getLabelString(3)
	    when:
	    b1 = [ 1, 'e' ]
	    then:
	    b1 == n.getLabel(4)
	    "e" == n.getLabelString(4)
	    when:
	    b1 = [ 1, 'f' ]
	    then:
	    b1 == n.getLabel(5)
	    "f" == n.getLabelString(5)
	    when:
	    b1 = [ 1, 'g' ]
	    then:
	    b1 == n.getLabel(6)
	    "g" == n.getLabelString(6)
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(7)
	    "" == n.getLabelString(7)
	}

	def "test_ctor_abs_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs, m_abs_origin)
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    4 == n.labels()
	    17.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ] 
	    then:
     	    b1 == n.getLabel(0)
	    "WWW" == n.getLabelString(0)
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    b2 == n.getLabel(1) 
	    "DnsJava" == n.getLabelString(1)
	    when:
	    b1 = [ 3, 'o', 'r', 'g' ]
	    then:
	   b1 == n.getLabel(2)
	    "org" == n.getLabelString(2)
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(3)
	    "" == n.getLabelString(3)
	}

	def "test_ctor_abs_rel_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs, m_rel_origin)
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    4 == n.labels()
	    17.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    b1 == n.getLabel(0)
	    "WWW" == n.getLabelString(0)
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    b2 == n.getLabel( 1 ) 
	    "DnsJava" == n.getLabelString(1)
	    when:
	    b1 = [ 3, 'o', 'r', 'g' ]
	    then:
	   b1 == n.getLabel(2)
	    "org" == n.getLabelString(2)
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(3)
	    "" == n.getLabelString(3)
	}

	def "test_ctor_rel_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_rel, m_abs_origin)
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    4 == n.labels()
	    18.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    b1 == n.getLabel(0)
	    "WWW" == n.getLabelString(0)
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    b2 == n.getLabel( 1 ) 
	    "DnsJava" == n.getLabelString(1)
	    when:
	    b1 = [ 4, 'O', 'r', 'i', 'g' ]
	    then:
	   b1 == n.getLabel(2)
	    "Orig" == n.getLabelString(2)
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(3)
	    "" == n.getLabelString(3)
	}

	def "test_ctor_invalid_label"() {
	setup_init()
	    when:
		new Name("junk..junk.")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_max_label_init"() throws TextParseException {
	setup_init()
	    // name with a 63 char label
	    Name n = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.b.")
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    3 == n.labels()
	    67.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 63, 'a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a' ]
	    then:
            b1 == n.getLabel(0)
	    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" == n.getLabelString(0)
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    b1 == n.getLabel(1)
	    "b" == n.getLabelString(1)
	    when:
	    b1 = [ 0 ]
	    then:
	   b1 == n.getLabel(2)
	    "" == n.getLabelString(2)
	}

	def "test_ctor_toobig_label"() {
	setup_init()
	    // name with a 64 char label
	    when:
		new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.b.")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_max_length_rel_init"() throws TextParseException {
	setup_init()
	    // relative name with three 63-char labels and a 62-char label
	    Name n = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd")
	    expect:
	    !n.isAbsolute()
	    !n.isWild()
	    4 == n.labels()
	    255.shortValue() == n.length()
	}
	    
	def "test_ctor_max_length_abs_init"() throws TextParseException {
	setup_init()
	    // absolute name with three 63-char labels and a 61-char label
	    Name n = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.")
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    5 == n.labels()
	    255.shortValue() == n.length()
	}

	def "test_ctor_escaped_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("ab\\123cd")
	    expect:
	    !n.isAbsolute()
	    !n.isWild()
	    1 == n.labels()
	    6.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 5, 'a', 'b', (byte)123, 'c', 'd' ]
	    then:
	    b1 == n.getLabel(0)
	}
	    
	def "test_ctor_escaped_end_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("abcd\\123")
	    expect:
	    !n.isAbsolute()
	    !n.isWild()
	    1 == n.labels()
	    6.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 5, 'a', 'b', 'c', 'd', (byte)123 ]
	    then:
	    b1 == n.getLabel(0)
	}

	def "test_ctor_short_escaped_init"() throws TextParseException {
	setup_init()
	    when:
		new Name("ab\\12cd")
	    then: thrown(TextParseException.class)
	}
	    
	def "test_ctor_short_escaped_end_init"() throws TextParseException {
	setup_init()
	    when:
		new Name("ab\\12")
	    then: thrown(TextParseException.class)
	}
	    
	def "test_ctor_empty_escaped_end_init"() throws TextParseException {
	setup_init()
	    when:
		new Name("ab\\")
	    then: thrown(TextParseException.class)
	}
	    
	def "test_ctor_toobig_escaped_init"() throws TextParseException {
	setup_init()
	    when:
		new Name("ab\\256cd")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_toobig_escaped_end_init"() throws TextParseException {
	setup_init()
	    when:
		new Name("ab\\256")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_max_label_escaped_init"() throws TextParseException {
	setup_init()
	    // name with a 63 char label containing an escape
	    Name n = new Name("aaaa\\100aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.b.")
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    3 == n.labels()
	    67.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 63, 'a','a','a','a',(byte)100,'a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a' ] 
	    then:
	    b1 == n.getLabel(0)
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    b1 == n.getLabel(1)
	    "b" == n.getLabelString(1)
	    when:
	    b1 = [ 0 ]
	    then:
	   b1 == n.getLabel(2)
	    "" == n.getLabelString(2)
	}

	def "test_ctor_max_labels_init"() throws TextParseException {
	setup_init()
	    StringBuffer sb = new StringBuffer()
	    // for ( int i = 0; i < 127; ++i ) {
	    for ( i in 0..126 ) {
		sb.append("a.");
	    }
	    Name n = new Name(sb.toString())
	    expect:
	    n.isAbsolute()
	    !n.isWild()
	    128 == n.labels()
	    255.shortValue() == n.length()
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    for ( i in 0..126 ) {
		b1 == n.getLabel(i)
		"a" == n.getLabelString(i)
	    }
	    when:
	    b1 = [ 0 ]
	    then:
	    b1 == n.getLabel(127)
	    "" == n.getLabelString(127)
	}

	def "test_ctor_toobig_label_escaped_end_init"() throws TextParseException {
	setup_init()
	    when:
		// name with a 64 char label containing an escape at the end
		new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\\090.b.")
	    then: thrown(TextParseException.class)
	}

	def "test_ctor_toobig_label_escaped_init"() throws TextParseException {
	setup_init()
	    when:
		// name with a 64 char label containing an escape at the end
		new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaa\\001aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.b.")
	    then: thrown(TextParseException.class)
	}
	
	def "test_fromString_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_rel, m_abs_origin)
	    Name n2 = Name.fromString(m_rel, m_abs_origin)
	    expect:
	    n == n2
	}

	def "test_fromString_at_init"() throws TextParseException {
	setup_init()
	    Name n = Name.fromString("@", m_rel_origin)
	    expect:
	    m_rel_origin == n
	}

	def "test_fromString_dot_init"() throws TextParseException {
	setup_init()
	    Name n = Name.fromString(".")
	    expect:
	    Name.root == n
	}

	def "test_fromConstantString_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs)
	    Name n2 = Name.fromConstantString(m_abs)
	    expect:
	    n == n2
	}
	
	def "test_fromConstantString_invalid_init"() {
	setup_init()
	    when:
		Name.fromConstantString("junk..junk")
	    then:
	    thrown(IllegalArgumentException.class)
	}
	
	def "test_basic_DNSInput_init"() throws IOException, TextParseException, WireParseException {	    
	    final byte[] raw = [ 3, 'W', 'w', 'w', 7, 'D', 'n', 's', 'J', 'a', 'v', 'a', 3, 'o', 'r', 'g', 0 ]
	    Name e = Name.fromString("Www.DnsJava.org.")

	    Name n = new Name(raw)
	    expect:
	    e == n
	}

	def "test_incomplete_DNSInput_init"() throws IOException {
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    new Name( b1 ) 
      	
	    then: thrown(WireParseException.class )
	}
	
	def "test_root_DNSInput_init"() throws WireParseException {
	    final byte[] raw = [ 0 ]
	    Name n = new Name(new DNSInput(raw))
	    expect:
	    Name.root == n
	}

	def "test_invalid_length_DNSInput_init"() throws IOException
	{
	    when:
	    def byte[] b1 = [ 4, 'W', 'w', 'w' ]
	    new Name( b1 ) 
	    then: thrown(WireParseException.class)
	}

	def "test_max_label_length_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 63, 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 0 ]
	    Name e = Name.fromString("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.")
	    
	    Name n = new Name(new DNSInput(raw))
	    expect:
	    e == n
	}

	def "test_max_name_DNSInput_init"() throws TextParseException, WireParseException {
	    // absolute name with three 63-char labels and a 61-char label
	    Name e = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.")
	    byte[] raw = [ 63, 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 63, 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 63, 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 61, 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 0 ]
	    
	    Name n = new Name(new DNSInput(raw))
	    expect:
	    e == n
	}

	def "test_toolong_name_DNSInput_init"() throws TextParseException, WireParseException {
	    // absolute name with three 63-char labels and a 62-char label
	    byte[] raw = [ 63, 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 63, 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 63, 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 62, 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 0 ]
	    
	    when:
		new Name(new DNSInput(raw))
	    then: thrown(WireParseException.class )
	}

	def "test_max_labels_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 0 ]
	    Name e = Name.fromString("a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.")
	    Name n = new Name(new DNSInput(raw))
	    expect:
	    128 == n.labels()
	    e == n
	}

	def "test_toomany_labels_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 1, 'a', 0 ]
	    when:
		new Name(new DNSInput(raw))    
	    then: thrown(WireParseException.class )
	}

	def "test_basic_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, (byte)0xC0, 1 ]
	    Name e = Name.fromString("abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    
	    Options.set("verbosecompression")
	    Name n = new Name(dnsin)
	    Options.unset("verbosecompression")
	    expect:
	    e == n
	}

	def "test_two_pointer_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, (byte)0xC0, 1, (byte)0xC0, 6 ]
	    Name e = Name.fromString("abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(8)
	    
	    Name n = new Name(dnsin)
	    expect:
	    e == n
	}

	def "test_two_part_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, 1, 'B', (byte)0xC0, 1 ]
	    Name e = Name.fromString("B.abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    
	    Name n = new Name(dnsin)
	    expect:
	    e == n
	}

	def "test_long_jump_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    // pointer to name beginning at index 256
	    byte[] raw = [ 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 3, 'a', 'b', 'c', 0, (byte)0xC1, 0 ]
	    Name e = Name.fromString("abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(261)
	    Name n = new Name(dnsin)
	    expect:
	    e == n
	}

	def "test_bad_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ (byte)0xC0, 2, 0 ]
	    when: new Name(new DNSInput(raw))
	    then: thrown(WireParseException.class )
	}

	def "test_basic_compression_state_restore_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, (byte)0xC0, 1, 3, 'd', 'e', 'f', 0 ]
	    Name e = Name.fromString("abc.")
	    Name e2 = Name.fromString("def.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    when:
	    Name n = new Name(dnsin)
	    then: e == n
	    when:
	    n = new Name(dnsin)
	    then: e2 == n
	}

	def "test_two_part_compression_state_restore_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, 1, 'B', (byte)0xC0, 1, 3, 'd', 'e', 'f', 0 ]
	    Name e = Name.fromString("B.abc.")
	    Name e2 = Name.fromString("def.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    when:
	    Name n = new Name(dnsin)
	    then: e == n

	    when:
	    n = new Name(dnsin)
	    then: e2 == n
	}
    
    
    def "test_init_from_name"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name e = new Name("B.c.d.")
	Name o = new Name(n, 1)
	expect:
	e == o
    }

    def "test_init_from_name_root"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name o = new Name(n, 4)
	expect:
	Name.root == o
    }

    def "test_init_from_name_empty"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name n2 = new Name(n, 5)

	expect:
	!n2.isAbsolute()
	!n2.isWild()
	0 == n2.labels()
        0.shortValue() == n2.length()
    }

    def "test_concatenate_basic"() throws NameTooLongException, TextParseException {
	Name p = Name.fromString("A.B")
	Name s = Name.fromString("c.d.")
	Name e = Name.fromString("A.B.c.d.")
	
	Name n = Name.concatenate(p, s)
	expect:
	e == n
    }

    def "test_concatenate_abs_prefix"() throws NameTooLongException, TextParseException {

	Name p = Name.fromString("A.B.")
	Name s = Name.fromString("c.d.")
	Name e = Name.fromString("A.B.")
	
	Name n = Name.concatenate(p, s)
	expect:
	e == n
    }

    def "test_concatenate_too_long"() throws TextParseException {
	Name p = Name.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
	Name s = Name.fromString("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.")

	when:
	    Name.concatenate(p, s)
	then: thrown(NameTooLongException.class )
    }

    def "test_relativize"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name dom = Name.fromString("c.")
	Name exp = Name.fromString("a.b")

	Name n = sub.relativize(dom)
	expect:
	exp == n
    }

    def "test_relativize_null_origin"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name dom = null

	Name n = sub.relativize(dom)
	expect:
	sub == n
    }

    def "test_relativize_disjoint"() throws TextParseException
    {
	Name sub = Name.fromString("a.b.c.")
	Name dom = Name.fromString("e.f.")

	Name n = sub.relativize(dom)
	expect:
	sub == n
    }

    def "test_relativize_root"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name dom = Name.fromString(".")
	Name exp = Name.fromString("a.b.c")

	Name n = sub.relativize(dom)
	expect:
	exp == n
    }

    def "test_wild"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name exp = Name.fromString("*.b.c.")

	Name n = sub.wild(1)
	expect:
	exp == n
    }

    def "test_wild_abs"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name exp = Name.fromString("*.")

	Name n = sub.wild(3)
	expect:
	exp == n
    }

    def "test_wild_toobig"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	when:
	    sub.wild(4)
	then: thrown(IllegalArgumentException.class )
    }

    def "test_wild_toosmall"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	when:
	    sub.wild(0)
	then: thrown(IllegalArgumentException.class )
    }

    def "test_fromDNAME"() throws NameTooLongException, TextParseException {
	Name own = new Name("the.owner.")
	Name alias = new Name("the.alias.")
	DNAMERecord dnr = new DNAMERecord(own, DClass.IN, 0xABCD, alias)
	Name sub = new Name("sub.the.owner.")
	Name exp = new Name("sub.the.alias.")

	Name n = sub.fromDNAME(dnr)
	expect:
	exp == n
    }

    def "test_fromDNAME_toobig"() throws NameTooLongException, TextParseException {
	Name own = new Name("the.owner.")
	Name alias = new Name("the.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.")
	DNAMERecord dnr = new DNAMERecord(own, DClass.IN, 0xABCD, alias)
	Name sub = new Name("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.the.owner.")

	when:
	    sub.fromDNAME(dnr)
	then: thrown(NameTooLongException.class )
    }

    def "test_fromDNAME_disjoint"() throws NameTooLongException, TextParseException {
	Name own = new Name("the.owner.")
	Name alias = new Name("the.alias.")
	DNAMERecord dnr = new DNAMERecord(own, DClass.IN, 0xABCD, alias)
	
	Name sub = new Name("sub.the.other")
	expect:
	null == sub.fromDNAME(dnr)
    }

    def "test_subdomain_abs"() throws TextParseException {
	Name dom = new Name("the.domain.")
	Name sub = new Name("sub.of.the.domain.")
	expect:
	sub.subdomain(dom)
	!dom.subdomain(sub)
    }

    def "test_subdomain_rel"() throws TextParseException {
	Name dom = new Name("the.domain")
	Name sub = new Name("sub.of.the.domain")
	expect:
	sub.subdomain(dom)
	!dom.subdomain(sub)
    }

    def "test_subdomain_equal"() throws TextParseException {
	Name dom = new Name("the.domain")
	Name sub = new Name("the.domain")
	expect:
	sub.subdomain(dom)
	dom.subdomain(sub)
    }

    def "test_toString_abs"() throws TextParseException {
	String stin = "This.Is.My.Absolute.Name."
	Name n = new Name(stin)
	expect:	
	stin == n.toString()
    }

    def "test_toString_rel"() throws TextParseException {
	String stin = "This.Is.My.Relative.Name"
	Name n = new Name(stin)
	expect:	
	stin == n.toString()
    }

    def "test_toString_at"() throws TextParseException {
	Name n = new Name("@", null)
	expect:
	"@" == n.toString()
    }

    def "test_toString_root"() throws TextParseException {
	expect:
	"." == Name.root.toString()
    }

    def "test_toString_wild"() throws TextParseException {
	String stin = "*.A.b.c.e"
	Name n = new Name(stin)
	expect:
	stin == n.toString()
    }

    def "test_toString_escaped"() throws TextParseException {
	String stin = "my.escaped.junk\\128.label."
	Name n = new Name(stin)
	expect:
	stin == n.toString()
    }

    def "test_toString_special_char"() throws TextParseException, WireParseException {
	byte[] raw = [ 1, '"', 1, '(', 1, ')', 1, '.', 1, ';', 1, '\\', 1, '@', 1, '$', 0 ]
	String exp = "\\\".\\(.\\).\\..\\;.\\\\.\\@.\\\$.";
	Name n = new Name(new DNSInput(raw))
	expect:
	exp == n.toString()
    }
  
    def "test_rel_toWire"() throws TextParseException {
	Name n = new Name("A.Relative.Name")
	    when:
		n.toWire(new DNSOutput(), null)
	    then: thrown(IllegalArgumentException.class)
    }
    
    def "test_null_Compression_toWire"() throws TextParseException {
	byte[] raw = [ 1, 'A', 5, 'B', 'a', 's', 'i', 'c', 4, 'N', 'a', 'm', 'e', 0 ]
	Name n = new Name("A.Basic.Name.")
	    
	DNSOutput o = new DNSOutput()
	n.toWire(o, null)
	expect:
	    raw == o.toByteArray()
    }

	def "test_empty_Compression_toWire"() throws TextParseException {
	    byte[] raw = [ 1, 'A', 5, 'B', 'a', 's', 'i', 'c', 4, 'N', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c)
	    expect:
	    raw == o.toByteArray()
	    0 == c.get(n)
	}

	def "test_with_exact_Compression_toWire"() throws TextParseException {
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    c.add(256, n)
	    byte[] exp = [ (byte)0xC1, 0x0 ]

	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c)
	    expect:
	    exp == o.toByteArray()
	    256 == c.get(n)
	}

	def "test_with_partial_Compression_toWire"() throws TextParseException {
	    Name d = new Name("Basic.Name.")
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    c.add(257, d)
	    byte[] exp = [ 1, 'A', (byte)0xC1, 0x1 ]

	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c)
	    expect:
	    exp == o.toByteArray()
	    257 == c.get(d)
	    0 == c.get(n)
	}

	def "test_0arg_rel_toWire"() throws TextParseException {
	    Name n = new Name("A.Relative.Name")
	    when:
		n.toWire()
	    then: thrown(IllegalArgumentException.class)
	}

	def "test_0arg_toWire"() throws TextParseException {
	    byte[] raw = [ 1, 'A', 5, 'B', 'a', 's', 'i', 'c', 4, 'N', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    byte[] out = n.toWire()
	    expect:
	    raw == out
	}

	def "test_root_toWire"() {
	    byte[] out = Name.root.toWire()
	    def byte[] b1 = [ 0 ]
	    expect:
	    b1 == out
	}

	def "test_3arg_toWire"() throws TextParseException {
	    Name d = new Name("Basic.Name.")
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    c.add(257, d)
	    byte[] exp = [ 1, 'A', (byte)0xC1, 0x1 ]

	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c, false)

	    expect:
	    exp == o.toByteArray()
	    257 == c.get(d)
	    0 == c.get(n)
	}
    
	def "test_basic_toWireCanonical"() throws TextParseException {
	    byte[] raw = [ 1, 'a', 5, 'b', 'a', 's', 'i', 'c', 4, 'n', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    DNSOutput o = new DNSOutput()
	    n.toWireCanonical(o)
	    expect:
	    raw == o.toByteArray()
	}

	def "test_0arg_toWireCanonical"() throws TextParseException {
	    byte[] raw = [ 1, 'a', 5, 'b', 'a', 's', 'i', 'c', 4, 'n', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    byte[] out = n.toWireCanonical()
	    expect:
	    raw == out
	}

	def "test_root_toWireCanonical"() {
	    byte[] out = Name.root.toWireCanonical()
	    // mga.that(Arrays.equals(new byte[] { 0 }, out))
	    def byte[] b1 = [ 0 ]
	    expect:
	    b1 == out
	}

	def "test_empty_toWireCanonical"() throws TextParseException {
	    Name n = new Name("@", null)
	    byte[] out = n.toWireCanonical()
	    
	    def byte[] b1 = [ ]
	    expect:
	    b1 == out
	}

	def "test_3arg_toWireCanonical"() throws TextParseException {
	    Name d = new Name("Basic.Name.")
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    c.add(257, d)
	    byte[] exp = [ 1, 'a', 5, 'b', 'a', 's', 'i', 'c', 4, 'n', 'a', 'm', 'e', 0 ]

	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c, true)
	    expect:
	    exp == o.toByteArray()
	    257 == c.get(d)
	    -1 == c.get(n)
	}
    
	def "test_same_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    expect:
	    n.equals(n)
	}

	def "test_null_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    expect: 
	        n != null
		// assertFalse(n.equals(null))
	}

	def "test_notName_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    expect:
	    n != new Object()
		// assertFalse(n.equals(new Object()))
	}

        def "test_abs_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    Name n2 = new Name("a.name.")
	    expect:
	    n.equals(n2)
	    n2.equals(n)
	}

	def "test_rel_equals"() throws TextParseException {
	    Name n1 = new Name("A.Relative.Name")
	    Name n2 = new Name("a.relative.name")
	    expect:
	    n1.equals(n2)
	    n2.equals(n1)
	}

	def "test_mixed_equals"() throws TextParseException {
	    Name n1 = new Name("A.Name")
	    Name n2 = new Name("a.name.")

	    expect:
		// assertFalse(n1.equals(n2))
		// assertFalse(n2.equals(n1))
	    n1 != n2
	    n2 != n1
	}

	def "test_weird_equals"() throws TextParseException {
	    Name n1 = new Name("ab.c")
	    Name n2 = new Name("abc.")

	    expect:
	    n1 != n2
	    n2 != n1
		// assertFalse(n1.equals(n2))
		// assertFalse(n2.equals(n1))
	}
    
	def "test_notName_compareTo"() throws TextParseException {
	    Name n = new Name("A.Name")
	    when:
	    n.compareTo(new Object())
	    then:
	    thrown(ClassCastException.class )
	}

	def "test_same_compareTo"() throws TextParseException {
	    Name n = new Name("A.Name")
	    expect:
	    0 == n.compareTo(n)
	}

	def "test_equal_compareTo"() throws TextParseException {
	    Name n1 = new Name("A.Name.")
	    Name n2 = new Name("a.name.")
	    expect:
	    0 == n1.compareTo(n2)
	    0 == n2.compareTo(n1)
	}

	def "test_close_compareTo"() throws TextParseException {
	    Name n1 = new Name("a.name")
	    Name n2 = new Name("a.name.")
	    expect:
	    n1.compareTo(n2) > 0
	    n2.compareTo(n1) < 0
	}

	def "test_disjoint_compareTo"() throws TextParseException {
	    Name n1 = new Name("b")
	    Name n2 = new Name("c")
	    expect:
	    n1.compareTo(n2) < 0
	    n2.compareTo(n1) > 0
	}

	def "test_label_prefix_compareTo"() throws TextParseException {
	    Name n1 = new Name("thisIs.a.")
	    Name n2 = new Name("thisIsGreater.a.")
	    expect:
	    n1.compareTo(n2) < 0
	    n2.compareTo(n1) > 0
	}

	def "test_more_labels_compareTo"() throws TextParseException {
	    Name n1 = new Name("c.b.a.")
	    Name n2 = new Name("d.c.b.a.")
	    expect:
	    n1.compareTo(n2) < 0
	    n2.compareTo(n1) > 0
	}
    
/*
    public static Test suite()
    {
	TestSuite s = new TestSuite()
	s.addTestSuite(Test_String_init.class)
	s.addTestSuite(Test_DNSInput_init.class)
	s.addTestSuite(NameTest.class)
	s.addTestSuite(Test_toWire.class)
	s.addTestSuite(Test_toWireCanonical.class)
	s.addTestSuite(Test_equals.class)
	s.addTestSuite(Test_compareTo.class)
	return s
    }
    */
}
