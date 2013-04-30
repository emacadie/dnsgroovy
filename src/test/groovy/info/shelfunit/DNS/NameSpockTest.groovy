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
package org.xbill.DNS

import info.shelfunit.DNS.*

import java.io.IOException
import java.util.Arrays
import spock.lang.Specification
import org.xbill.DNS.Name
import org.xbill.DNS.TextParseException

public class NameSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

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
	    mga.that(!n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(0, n.labels())
	    mgu.equals(0.shortValue(), n.length())
	}

	def "test_ctor_at_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("@", m_abs_origin)
	    expect:
	    mgu.equals(m_abs_origin, n)
	}
	    
	def "test_ctor_at_rel_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("@", m_rel_origin)
	    expect:	    
	    mgu.equals(m_rel_origin, n)
	}

	def "test_ctor_dot_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(".")
	    mgu.equals(Name.root, n)
	    !mgu.equals(Name.root, n)
	    mgu.equals(1, n.labels())
	    mgu.equals(1, n.length())
	}

	def "test_ctor_wildcard_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("*")
	    expect:
	    mga.that(!n.isAbsolute())
	    mga.that(n.isWild())
	    mgu.equals(1, n.labels())
	    mgu.equals(2.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 1, '*' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("*", n.getLabelString(0))
	}

	def "test_ctor_abs_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs)
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(4, n.labels())
	    mgu.equals(17.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("WWW", n.getLabelString(0))
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    mga.that(Arrays.equals(b2, n.getLabel(1)))
	    mgu.equals("DnsJava", n.getLabelString(1))
	    when:
	    def byte[] b3 = [ 3, 'o', 'r', 'g' ]
	    then:
	    mga.that(Arrays.equals(b3, n.getLabel(2)))
	    mgu.equals("org", n.getLabelString(2))
	    when:
	    def byte[] b4 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b4, n.getLabel(3)))
	    mgu.equals("", n.getLabelString(3))
	}

	def "test_ctor_rel_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_rel)
	    expect:
	    mga.that(!n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(2, n.labels())
	    mgu.equals(12.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("WWW", n.getLabelString(0))
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    mga.that(Arrays.equals(b2, n.getLabel(1)))
	    mgu.equals("DnsJava", n.getLabelString(1))
	}

	def "test_ctor_7label_init"() throws TextParseException {
	setup_init()
	    // 7 is the number of label positions that are cached
	    Name n = new Name("a.b.c.d.e.f.")
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(7, n.labels())
	    mgu.equals(13.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("a", n.getLabelString(0))
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(1)))
	    mgu.equals("b", n.getLabelString(1))
	    when:
	    b1 = [ 1, 'c' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("c", n.getLabelString(2))
	    when:
	    b1 = [ 1, 'd' ]
	    then:
	    mga.that(Arrays.equals(b1,  n.getLabel(3)))
	    mgu.equals("d", n.getLabelString(3))
	    when:
	    b1 = [ 1, 'e' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(4)))
	    mgu.equals("e", n.getLabelString(4))
	    when:
	    b1 = [ 1, 'f' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(5)))
	    mgu.equals("f", n.getLabelString(5))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(6)))
	    mgu.equals("", n.getLabelString(6))
	}

	def "test_ctor_8label_init"() throws TextParseException {
	setup_init()
	    // 7 is the number of label positions that are cached
	    Name n = new Name("a.b.c.d.e.f.g.")
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(8, n.labels())
	    mgu.equals(15.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("a", n.getLabelString(0))
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(1)))
	    mgu.equals("b", n.getLabelString(1))
	    when:
	    b1 = [ 1, 'c' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("c", n.getLabelString(2))
	    when:
	    b1 = [ 1, 'd' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(3)))
	    mgu.equals("d", n.getLabelString(3))
	    when:
	    b1 = [ 1, 'e' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(4)))
	    mgu.equals("e", n.getLabelString(4))
	    when:
	    b1 = [ 1, 'f' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(5)))
	    mgu.equals("f", n.getLabelString(5))
	    when:
	    b1 = [ 1, 'g' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(6)))
	    mgu.equals("g", n.getLabelString(6))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(7)))
	    mgu.equals("", n.getLabelString(7))
	}

	def "test_ctor_abs_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs, m_abs_origin)
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(4, n.labels())
	    mgu.equals(17.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ] 
	    then:
     	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("WWW", n.getLabelString(0))
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    mga.that(Arrays.equals(b2, n.getLabel(1) ) )
	    mgu.equals("DnsJava", n.getLabelString(1))
	    when:
	    b1 = [ 3, 'o', 'r', 'g' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("org", n.getLabelString(2))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(3)))
	    mgu.equals("", n.getLabelString(3))
	}

	def "test_ctor_abs_rel_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs, m_rel_origin)
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(4, n.labels())
	    mgu.equals(17.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    mga.that(Arrays.equals( b1, n.getLabel(0)))
	    mgu.equals("WWW", n.getLabelString(0))
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    mga.that(Arrays.equals(b2, n.getLabel( 1 ) ) )
	    mgu.equals("DnsJava", n.getLabelString(1))
	    when:
	    b1 = [ 3, 'o', 'r', 'g' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("org", n.getLabelString(2))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals( b1, n.getLabel(3)))
	    mgu.equals("", n.getLabelString(3))
	}

	def "test_ctor_rel_abs_origin_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_rel, m_abs_origin)
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(4, n.labels())
	    mgu.equals(18.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 3, 'W', 'W', 'W' ]
	    then:
	    mga.that(Arrays.equals( b1, n.getLabel(0)))
	    mgu.equals("WWW", n.getLabelString(0))
	    when:
	    def byte[] b2 = [ 7, 'D', 'n', 's', 'J', 'a', 'v', 'a' ]
	    then:
	    mga.that(Arrays.equals(b2, n.getLabel( 1 ) ) )
	    mgu.equals("DnsJava", n.getLabelString(1))
	    when:
	    b1 = [ 4, 'O', 'r', 'i', 'g' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("Orig", n.getLabelString(2))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(3)))
	    mgu.equals("", n.getLabelString(3))
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
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(3, n.labels())
	    mgu.equals(67.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 63, 'a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a' ]
	    then:
            mga.that(Arrays.equals(b1, n.getLabel(0)))
	    mgu.equals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", n.getLabelString(0))
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(1)))
	    mgu.equals("b", n.getLabelString(1))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("", n.getLabelString(2))
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
	    mga.that(!n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(4, n.labels())
	    mgu.equals(255.shortValue(), n.length())
	}
	    
	def "test_ctor_max_length_abs_init"() throws TextParseException {
	setup_init()
	    // absolute name with three 63-char labels and a 61-char label
	    Name n = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.")
	    expect:
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(5, n.labels())
	    mgu.equals(255.shortValue(), n.length())
	}

	def "test_ctor_escaped_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("ab\\123cd")
	    expect:
	    mga.that(!n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(1, n.labels())
	    mgu.equals(6.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 5, 'a', 'b', (byte)123, 'c', 'd' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	}
	    
	def "test_ctor_escaped_end_init"() throws TextParseException {
	setup_init()
	    Name n = new Name("abcd\\123")
	    expect:
	    mga.that(!n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(1, n.labels())
	    mgu.equals(6.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 5, 'a', 'b', 'c', 'd', (byte)123 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
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
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(3, n.labels())
	    mgu.equals(67.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 63, 'a','a','a','a',(byte)100,'a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a',
			      'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a' ] 
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(0)))
	    when:
	    b1 = [ 1, 'b' ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(1)))
	    mgu.equals("b", n.getLabelString(1))
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(2)))
	    mgu.equals("", n.getLabelString(2))
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
	    mga.that(n.isAbsolute())
	    mga.that(!n.isWild())
	    mgu.equals(128, n.labels())
	    mgu.equals(255.shortValue(), n.length())
	    when:
	    def byte[] b1 = [ 1, 'a' ]
	    then:
	    for ( i in 0..126 ) {
		mga.that(Arrays.equals(b1, n.getLabel(i)))
		mgu.equals("a", n.getLabelString(i))
	    }
	    when:
	    b1 = [ 0 ]
	    then:
	    mga.that(Arrays.equals(b1, n.getLabel(127)))
	    mgu.equals("", n.getLabelString(127))
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
	    mgu.equals(n, n2)
	}

	def "test_fromString_at_init"() throws TextParseException {
	setup_init()
	    Name n = Name.fromString("@", m_rel_origin)
	    expect:
	    mgu.equals(m_rel_origin, n)
	}

	def "test_fromString_dot_init"() throws TextParseException {
	setup_init()
	    Name n = Name.fromString(".")
	    expect:
	    mgu.equals(Name.root, n)
	}

	def "test_fromConstantString_init"() throws TextParseException {
	setup_init()
	    Name n = new Name(m_abs)
	    Name n2 = Name.fromConstantString(m_abs)
	    expect:
	    mgu.equals(n, n2)
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
	    mgu.equals(e, n)
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
	    mgu.equals(Name.root, n)
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
	    mgu.equals(e, n)
	}

	def "test_max_name_DNSInput_init"() throws TextParseException, WireParseException {
	    // absolute name with three 63-char labels and a 61-char label
	    Name e = new Name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb.ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd.")
	    byte[] raw = [ 63, 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 63, 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 63, 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 61, 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 0 ]
	    
	    Name n = new Name(new DNSInput(raw))
	    expect:
	    mgu.equals(e, n)
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
	    mgu.equals(128, n.labels())
	    mgu.equals(e, n)
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
	    mgu.equals(e, n)
	}

	def "test_two_pointer_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, (byte)0xC0, 1, (byte)0xC0, 6 ]
	    Name e = Name.fromString("abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(8)
	    
	    Name n = new Name(dnsin)
	    expect:
	    mgu.equals(e, n)
	}

	def "test_two_part_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, 1, 'B', (byte)0xC0, 1 ]
	    Name e = Name.fromString("B.abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    
	    Name n = new Name(dnsin)
	    expect:
	    mgu.equals(e, n)
	}

	def "test_long_jump_compression_DNSInput_init"() throws TextParseException, WireParseException {
	    // pointer to name beginning at index 256
	    byte[] raw = [ 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 3, 'a', 'b', 'c', 0, (byte)0xC1, 0 ]
	    Name e = Name.fromString("abc.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(261)
	    Name n = new Name(dnsin)
	    expect:
	    mgu.equals(e, n)
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
	    then: mgu.equals(e, n)
	    when:
	    n = new Name(dnsin)
	    then: mgu.equals(e2, n)
	}

	def "test_two_part_compression_state_restore_DNSInput_init"() throws TextParseException, WireParseException {
	    byte[] raw = [ 10, 3, 'a', 'b', 'c', 0, 1, 'B', (byte)0xC0, 1, 3, 'd', 'e', 'f', 0 ]
	    Name e = Name.fromString("B.abc.")
	    Name e2 = Name.fromString("def.")
	    
	    DNSInput dnsin = new DNSInput(raw)
	    dnsin.jump(6)
	    when:
	    Name n = new Name(dnsin)
	    then: mgu.equals(e, n)

	    when:
	    n = new Name(dnsin)
	    then: mgu.equals(e2, n)
	}
    
    
    def "test_init_from_name"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name e = new Name("B.c.d.")
	Name o = new Name(n, 1)
	expect:
	mgu.equals(e, o)
    }

    def "test_init_from_name_root"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name o = new Name(n, 4)
	expect:
	mgu.equals(Name.root, o)
    }

    def "test_init_from_name_empty"() throws TextParseException {
	Name n = new Name("A.B.c.d.")
	Name n2 = new Name(n, 5)

	expect:
	mga.that(!n2.isAbsolute())
	mga.that(!n2.isWild())
	mgu.equals(0, n2.labels())
        mgu.equals(0.shortValue(), n2.length())
    }

    def "test_concatenate_basic"() throws NameTooLongException, TextParseException {
	Name p = Name.fromString("A.B")
	Name s = Name.fromString("c.d.")
	Name e = Name.fromString("A.B.c.d.")
	
	Name n = Name.concatenate(p, s)
	expect:
	mgu.equals(e, n)
    }

    def "test_concatenate_abs_prefix"() throws NameTooLongException, TextParseException {

	Name p = Name.fromString("A.B.")
	Name s = Name.fromString("c.d.")
	Name e = Name.fromString("A.B.")
	
	Name n = Name.concatenate(p, s)
	expect:
	mgu.equals(e, n)
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
	mgu.equals(exp, n)
    }

    def "test_relativize_null_origin"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name dom = null

	Name n = sub.relativize(dom)
	expect:
	mgu.equals(sub, n)
    }

    def "test_relativize_disjoint"() throws TextParseException
    {
	Name sub = Name.fromString("a.b.c.")
	Name dom = Name.fromString("e.f.")

	Name n = sub.relativize(dom)
	expect:
	mgu.equals(sub, n)
    }

    def "test_relativize_root"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name dom = Name.fromString(".")
	Name exp = Name.fromString("a.b.c")

	Name n = sub.relativize(dom)
	expect:
	mgu.equals(exp, n)
    }

    def "test_wild"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name exp = Name.fromString("*.b.c.")

	Name n = sub.wild(1)
	expect:
	mgu.equals(exp, n)
    }

    def "test_wild_abs"() throws TextParseException {
	Name sub = Name.fromString("a.b.c.")
	Name exp = Name.fromString("*.")

	Name n = sub.wild(3)
	expect:
	mgu.equals(exp, n)
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
	mgu.equals(exp, n)
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
	mgu.equals(null, sub.fromDNAME(dnr))
    }

    def "test_subdomain_abs"() throws TextParseException {
	Name dom = new Name("the.domain.")
	Name sub = new Name("sub.of.the.domain.")
	expect:
	mga.that(sub.subdomain(dom))
	mga.that(!dom.subdomain(sub))
    }

    def "test_subdomain_rel"() throws TextParseException {
	Name dom = new Name("the.domain")
	Name sub = new Name("sub.of.the.domain")
	expect:
	mga.that(sub.subdomain(dom))
	mga.that(!dom.subdomain(sub))
    }

    def "test_subdomain_equal"() throws TextParseException {
	Name dom = new Name("the.domain")
	Name sub = new Name("the.domain")
	expect:
	mga.that(sub.subdomain(dom))
	mga.that(dom.subdomain(sub))
    }

    def "test_toString_abs"() throws TextParseException {
	String stin = "This.Is.My.Absolute.Name."
	Name n = new Name(stin)
	expect:	
	mgu.equals(stin, n.toString())
    }

    def "test_toString_rel"() throws TextParseException {
	String stin = "This.Is.My.Relative.Name"
	Name n = new Name(stin)
	expect:	
	mgu.equals(stin, n.toString())
    }

    def "test_toString_at"() throws TextParseException {
	Name n = new Name("@", null)
	expect:
	mgu.equals("@", n.toString())
    }

    def "test_toString_root"() throws TextParseException {
	expect:
	mgu.equals(".", Name.root.toString())
    }

    def "test_toString_wild"() throws TextParseException {
	String stin = "*.A.b.c.e"
	Name n = new Name(stin)
	expect:
	mgu.equals(stin, n.toString())
    }

    def "test_toString_escaped"() throws TextParseException {
	String stin = "my.escaped.junk\\128.label."
	Name n = new Name(stin)
	expect:
	mgu.equals(stin, n.toString())
    }

    def "test_toString_special_char"() throws TextParseException, WireParseException {
	byte[] raw = [ 1, '"', 1, '(', 1, ')', 1, '.', 1, ';', 1, '\\', 1, '@', 1, '$', 0 ]
	String exp = "\\\".\\(.\\).\\..\\;.\\\\.\\@.\\\$.";
	Name n = new Name(new DNSInput(raw))
	expect:
	mgu.equals(exp, n.toString())
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
	    mga.that(Arrays.equals(raw, o.toByteArray()))
    }

	def "test_empty_Compression_toWire"() throws TextParseException {
	    byte[] raw = [ 1, 'A', 5, 'B', 'a', 's', 'i', 'c', 4, 'N', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c)
	    expect:
	    mga.that(Arrays.equals(raw, o.toByteArray()))
	    mgu.equals(0, c.get(n))
	}

	def "test_with_exact_Compression_toWire"() throws TextParseException {
	    Name n = new Name("A.Basic.Name.")
	    
	    Compression c = new Compression()
	    c.add(256, n)
	    byte[] exp = [ (byte)0xC1, 0x0 ]

	    DNSOutput o = new DNSOutput()
	    n.toWire(o, c)
	    expect:
	    mga.that(Arrays.equals(exp, o.toByteArray()))
	    mgu.equals(256, c.get(n))
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
	    mga.that(Arrays.equals(exp, o.toByteArray()))
	    mgu.equals(257, c.get(d))
	    mgu.equals(0, c.get(n))
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
	    mga.that(Arrays.equals(raw, out))
	}

	def "test_root_toWire"() {
	    byte[] out = Name.root.toWire()
	    def byte[] b1 = [ 0 ]
	    expect:
	    mga.that(Arrays.equals( b1, out))
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
	    mga.that(Arrays.equals(exp, o.toByteArray()))
	    mgu.equals(257, c.get(d))
	    mgu.equals(0, c.get(n))
	}
    
	def "test_basic_toWireCanonical"() throws TextParseException {
	    byte[] raw = [ 1, 'a', 5, 'b', 'a', 's', 'i', 'c', 4, 'n', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    DNSOutput o = new DNSOutput()
	    n.toWireCanonical(o)
	    expect:
	    mga.that(Arrays.equals(raw, o.toByteArray()))
	}

	def "test_0arg_toWireCanonical"() throws TextParseException {
	    byte[] raw = [ 1, 'a', 5, 'b', 'a', 's', 'i', 'c', 4, 'n', 'a', 'm', 'e', 0 ]
	    Name n = new Name("A.Basic.Name.")
	    
	    byte[] out = n.toWireCanonical()
	    expect:
	    mga.that(Arrays.equals(raw, out))
	}

	def "test_root_toWireCanonical"() {
	    byte[] out = Name.root.toWireCanonical()
	    // mga.that(Arrays.equals(new byte[] { 0 }, out))
	    def byte[] b1 = [ 0 ]
	    expect:
	    mga.that(Arrays.equals(b1 , out))
	}

	def "test_empty_toWireCanonical"() throws TextParseException {
	    Name n = new Name("@", null)
	    byte[] out = n.toWireCanonical()
	    
	    def byte[] b1 = [ ]
	    expect:
	    mga.that(Arrays.equals(b1, out))
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
	    mga.that(Arrays.equals(exp, o.toByteArray()))
	    mgu.equals(257, c.get(d))
	    mgu.equals(-1, c.get(n))
	}
    
	def "test_same_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    expect:
	    mga.that(n.equals(n))
	}

	def "test_null_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    !mgu.equals( n, null)
		// assertFalse(n.equals(null))
	}

	def "test_notName_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    expect:
	    !mgu.equals(n, new Object())
		// assertFalse(n.equals(new Object()))
	}

        def "test_abs_equals"() throws TextParseException {
	    Name n = new Name("A.Name.")
	    Name n2 = new Name("a.name.")
	    expect:
	    mga.that(n.equals(n2))
	    mga.that(n2.equals(n))
	}

	def "test_rel_equals"() throws TextParseException {
	    Name n1 = new Name("A.Relative.Name")
	    Name n2 = new Name("a.relative.name")
	    expect:
	    mga.that(n1.equals(n2))
	    mga.that(n2.equals(n1))
	}

	def "test_mixed_equals"() throws TextParseException {
	    Name n1 = new Name("A.Name")
	    Name n2 = new Name("a.name.")

	    expect:
		// assertFalse(n1.equals(n2))
		// assertFalse(n2.equals(n1))
	    !mgu.equals(n1 ,n2)
	    !mgu.equals(n2, n1)
	}

	def "test_weird_equals"() throws TextParseException {
	    Name n1 = new Name("ab.c")
	    Name n2 = new Name("abc.")

	    expect:
	    !mgu.equals(n1 ,n2)
	    !mgu.equals(n2, n1)
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
	    mgu.equals(0, n.compareTo(n))
	}

	def "test_equal_compareTo"() throws TextParseException {
	    Name n1 = new Name("A.Name.")
	    Name n2 = new Name("a.name.")
	    expect:
	    mgu.equals(0, n1.compareTo(n2))
	    mgu.equals(0, n2.compareTo(n1))
	}

	def "test_close_compareTo"() throws TextParseException {
	    Name n1 = new Name("a.name")
	    Name n2 = new Name("a.name.")
	    expect:
	    mga.that(n1.compareTo(n2) > 0)
	    mga.that(n2.compareTo(n1) < 0)
	}

	def "test_disjoint_compareTo"() throws TextParseException {
	    Name n1 = new Name("b")
	    Name n2 = new Name("c")
	    expect:
	    mga.that(n1.compareTo(n2) < 0)
	    mga.that(n2.compareTo(n1) > 0)
	}

	def "test_label_prefix_compareTo"() throws TextParseException {
	    Name n1 = new Name("thisIs.a.")
	    Name n2 = new Name("thisIsGreater.a.")
	    expect:
	    mga.that(n1.compareTo(n2) < 0)
	    mga.that(n2.compareTo(n1) > 0)
	}

	def "test_more_labels_compareTo"() throws TextParseException {
	    Name n1 = new Name("c.b.a.")
	    Name n2 = new Name("d.c.b.a.")
	    expect:
	    mga.that(n1.compareTo(n2) < 0)
	    mga.that(n2.compareTo(n1) > 0)
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
