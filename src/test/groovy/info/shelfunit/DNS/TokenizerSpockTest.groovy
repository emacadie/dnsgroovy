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

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileWriter
import java.io.IOException
import spock.lang.Specification

public class TokenizerSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    private Tokenizer m_t

    def setup() {
	m_t = null
    }
    
    public void test_get() throws IOException
    {
	m_t = new Tokenizer(new BufferedInputStream(new ByteArrayInputStream("AnIdentifier \"a quoted \\\" string\"\r\n this is \"my\"\t(comment)\nanotherIdentifier (\ramultilineIdentifier\n)".getBytes())))

	Tokenizer.Token tt = m_t.get(true, true)
	expect:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	tt.isString()
	!tt.isEOL()
	mgu.equals("AnIdentifier", tt.value)
	    
	when:
	tt = m_t.get(true, true)
	then:
        mgu.equals(Tokenizer.WHITESPACE, tt.type)
	!tt.isString()
	!tt.isEOL()
	mgu.equals(null, tt.value)
	
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.QUOTED_STRING, tt.type)
	tt.isString()
	!tt.isEOL()
	mgu.equals("a quoted \\\" string", tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.EOL, tt.type)
	!tt.isString()
	tt.isEOL()
	mgu.equals(null, tt.value)

	    /*
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.COMMENT, tt.type)
	!tt.isString()
	!tt.isEOL()
	mgu.equals(" this is \"my\"\t(comment)", tt.value)
	    */

	when:
	tt = m_t.get(true, true)
	then:
	    // mgu.equals(Tokenizer.EOL, tt.type)
	!tt.isString()
	    // tt.isEOL()
	mgu.equals(null, tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	tt.isString()
	!tt.isEOL()
	    // mgu.equals("anotherIdentifier", tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	tt.isString()
	!tt.isEOL()
	    // mgu.equals("amultilineIdentifier", tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	    /*	
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.EOF, tt.type)
	!tt.isString()
	tt.isEOL()
	mgu.equals(null, tt.value)
	
	// should be able to do this repeatedly
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.EOF, tt.type)
	!tt.isString()
	tt.isEOL()
	mgu.equals(null, tt.value)
*/
	when:
	m_t = new Tokenizer("onlyOneIdentifier")
	tt = m_t.get()
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("onlyOneIdentifier", tt.value)

	when:
	m_t = new Tokenizer("identifier ")
	tt = m_t.get()
	then:
	mgu.equals("identifier", tt.value)
	when:
	tt = m_t.get()
	then:
	mgu.equals(Tokenizer.EOF, tt.type)

	// some ungets
	when:
	m_t = new Tokenizer("identifier \nidentifier2 junk comment")
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier", tt.value)

	when:
	m_t.unget()
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier", tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	when:
	m_t.unget()
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.EOL, tt.type)
	
	when:
	m_t.unget()
	tt = m_t.get(true, true)
	then:
        mgu.equals(Tokenizer.EOL, tt.type)
	
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier2", tt.value)

	    /*
	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.COMMENT, tt.type)
	mgu.equals(" junk comment", tt.value)
	    
	when:
	m_t.unget()
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.COMMENT, tt.type)
	mgu.equals(" junk comment", tt.value)

	when:
	tt = m_t.get(true, true)
	then:
	mgu.equals(Tokenizer.EOF, tt.type)
	    */
	    
	when:
	m_t = new Tokenizer("identifier ( junk  comment\n )")
	tt = m_t.get()
	then:
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals(Tokenizer.IDENTIFIER, m_t.get().type)
	    // mgu.equals(Tokenizer.EOF, m_t.get().type)
	
    }
    // end monster method

    
    def "test_get_invalid"() throws IOException {
	/*	
	when:
	m_t = new Tokenizer("(this ")
	m_t.get()
	m_t.get()
	then: thrown( TextParseException.class )
*/
	when:
	m_t = new Tokenizer("\"bad")
	m_t.get()
	then: thrown( TextParseException.class )
	
	when:
	m_t = new Tokenizer(")")
        m_t.get()
	then: thrown( TextParseException.class )
	
	when: 
	m_t = new Tokenizer("\\")
	m_t.get()
	then: thrown( TextParseException.class )

	when: 
	m_t = new Tokenizer("\"\n")
        m_t.get()
	then: thrown( TextParseException.class )
    }
    
    def "test_File_input"() throws IOException {
	
	File tmp = File.createTempFile("dnsjava", "tmp")
	try {
	    FileWriter fw = new FileWriter(tmp)
	    fw.write("file\ninput test")
	    fw.close()

	    m_t = new Tokenizer(tmp)

	    Tokenizer.Token tt = m_t.get()
	    expect:
	    mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	    mgu.equals("file", tt.value)

	    when: tt = m_t.get()
	    then: mgu.equals(Tokenizer.EOL, tt.type)

	    when: tt = m_t.get()
	    then:
	    mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	    mgu.equals("input", tt.value)

	    when: tt = m_t.get(false, true)
	    then:
	    mgu.equals(Tokenizer.COMMENT, tt.type)
	    mgu.equals(" test", tt.value)

	    m_t.close()
	} finally {
	    tmp.delete()
	}
    }
    
    def "test_unwanted_comment"() throws IOException {
	m_t = new Tokenizer("; this whole thing is a comment\n")
	Tokenizer.Token tt = m_t.get()

	expect: mgu.equals(Tokenizer.EOL, tt.type)
    }
    
    def "test_unwanted_ungotten_whitespace"() throws IOException {
	when:
	m_t = new Tokenizer(" ")
	Tokenizer.Token tt = m_t.get(true, true)
	m_t.unget()
	tt = m_t.get()
	then: mgu.equals(Tokenizer.EOF, tt.type)
    }
    
    def "test_unwanted_ungotten_comment"() throws IOException {
	when:
	m_t = new Tokenizer("; this whole thing is a comment")
	Tokenizer.Token tt = m_t.get(true, true)
	m_t.unget()
	tt = m_t.get()
	then: mgu.equals(Tokenizer.EOF, tt.type)
    }
    
    def "test_empty_string"() throws IOException {
	m_t = new Tokenizer("")
	Tokenizer.Token tt = m_t.get()
	expect: mgu.equals(Tokenizer.EOF, tt.type)

	when:
	m_t = new Tokenizer(" ")
	tt = m_t.get()
        then: mgu.equals(Tokenizer.EOF, tt.type)
    }
    
    def "test_multiple_ungets"() throws IOException {
	m_t = new Tokenizer("a simple one")
	Tokenizer.Token tt = m_t.get()

	m_t.unget()
        when:
	    m_t.unget()
	then:
	    thrown( IllegalStateException.class )
    }
    
    def "test_getString"() throws IOException {
	m_t = new Tokenizer("just_an_identifier")
	String out = m_t.getString()
        expect: mgu.equals("just_an_identifier", out)
	    
        when:
	m_t = new Tokenizer("\"just a string\"")
	out = m_t.getString()
        then: mgu.equals("just a string", out)
	
	when:
	    m_t = new Tokenizer("; just a comment")
	    out = m_t.getString()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getIdentifier"() throws IOException {
	m_t = new Tokenizer("just_an_identifier")
	String out = m_t.getIdentifier()
	expect: mgu.equals("just_an_identifier", out)

	when:
	    m_t = new Tokenizer("\"just a string\"")
	    m_t.getIdentifier()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getLong"() throws IOException {
	m_t = new Tokenizer((Integer.MAX_VALUE+1L) + "")
	long out = m_t.getLong()
	expect: mgu.equals((Integer.MAX_VALUE+1L), out)
	
	when:
	    m_t = new Tokenizer("-10")
	    m_t.getLong()
	then:
	    thrown( TextParseException.class )

	when:
	    m_t = new Tokenizer("19_identifier")
	    m_t.getLong()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getUInt32"() throws IOException {
	m_t = new Tokenizer(0xABCDEF12L + "")
	long out = m_t.getUInt32()
	expect: mgu.equals(0xABCDEF12L, out)

	when:
	    m_t = new Tokenizer(0x100000000L + "")
	    m_t.getUInt32()
	then:
	    thrown( TextParseException.class )

	when:
	    m_t = new Tokenizer("-12345")
	    m_t.getUInt32()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getUInt16"() throws IOException {
	m_t = new Tokenizer(0xABCDL + "")
	int out = m_t.getUInt16()
	mgu.equals(0xABCDL, out)
	
	when:
	    m_t = new Tokenizer(0x10000 + "")
	    m_t.getUInt16()
	then:
	    thrown( TextParseException.class )

	when:
	    m_t = new Tokenizer("-125")
	    m_t.getUInt16()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getUInt8"() throws IOException {
	m_t = new Tokenizer(0xCDL + "")
	int out = m_t.getUInt8()
	mgu.equals(0xCDL, out)

	when:
	m_t = new Tokenizer(0x100 + "")
	m_t.getUInt8()
	then:
	thrown( TextParseException.class )
	
	when:
	    m_t = new Tokenizer("-12")
	    m_t.getUInt8()
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getTTL"() throws IOException {
	when:
	m_t = new Tokenizer("59S")
	then: mgu.equals(59, m_t.getTTL().intValue())

	when:
	m_t = new Tokenizer(TTL.MAX_VALUE + "")
	then: mgu.equals(TTL.MAX_VALUE, m_t.getTTL())

	when:
	m_t = new Tokenizer((TTL.MAX_VALUE+1L) + "")
	then: mgu.equals(TTL.MAX_VALUE, m_t.getTTL())

	when:
	m_t = new Tokenizer("Junk")
	m_t.getTTL()
	then:
	thrown( TextParseException.class )
    }
    
    def "test_getTTLLike"() throws IOException {
	when:
	m_t = new Tokenizer("59S")
	then: mgu.equals(59, m_t.getTTLLike().intValue())

	when:
	m_t = new Tokenizer(TTL.MAX_VALUE + "")
	then: mgu.equals(TTL.MAX_VALUE, m_t.getTTLLike())

	when:
	m_t = new Tokenizer((TTL.MAX_VALUE+1L) + "")
	then: mgu.equals(TTL.MAX_VALUE+1L, m_t.getTTLLike())
	
	when:
	m_t = new Tokenizer("Junk")
	m_t.getTTLLike()
	then:
	thrown( TextParseException.class )
    }
    
    def "test_getName"() throws IOException, TextParseException {
	Name root = Name.fromString(".")
	m_t = new Tokenizer("junk")
	Name exp = Name.fromString("junk.")
	Name out = m_t.getName(root)
	expect: mgu.equals(exp, out)

	Name rel = Name.fromString("you.dig")

	when:
    	    m_t = new Tokenizer("junk")
	    m_t.getName(rel)
	then:
	    thrown( RelativeNameException.class )
	
	when:
	    m_t = new Tokenizer("")
	    m_t.getName(root)
	then:
	    thrown( TextParseException.class )
    }
    
    def "test_getEOL"() throws IOException {
	/*
	when:
	m_t = new Tokenizer("id")
	m_t.getIdentifier()
	m_t.getEOL()
	then:
	thrown( TextParseException.class )
	*/
	/*	
	when:
	m_t = new Tokenizer("\n")
	m_t.getEOL()
	m_t.getEOL()
	then:
	thrown( TextParseException.class )
*/
	when:
	m_t = new Tokenizer("id")
	m_t.getEOL()
	then:
	thrown( TextParseException.class )
    }
    
    def "test_getBase64"() throws IOException {
	def exp_raw = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ].collect{ entry -> (byte) entry }
	byte[] exp = exp_raw.toArray(new byte[exp_raw.size] )
	// basic
	m_t = new Tokenizer("AQIDBAUGBwgJ")
	byte[] out = m_t.getBase64()
	expect: exp == out

	// with some whitespace
	when:
	m_t = new Tokenizer("AQIDB AUGB   wgJ")
	out = m_t.getBase64()
	then:
	exp == out

	// two base64s separated by newline
	when:
	m_t = new Tokenizer("AQIDBAUGBwgJ\nAB23DK")
	out = m_t.getBase64()
	then:
	exp == out
	

	// no remaining strings
	when:
	m_t = new Tokenizer("\n")
	then: 
	mgu.equals(null,  m_t.getBase64() )

	when:
	m_t = new Tokenizer("\n")
	m_t.getBase64(true)
	then:
	thrown( TextParseException.class )

	// invalid encoding
	when:
	m_t = new Tokenizer("not_base64")
	m_t.getBase64(false)
	then:
	thrown( TextParseException.class )

	when:
	m_t = new Tokenizer("not_base64")
	m_t.getBase64(true)
	then:
	thrown( TextParseException.class )
    }
    
    def "test_getHex"() throws IOException {
	def exp_raw = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ].collect{ entry -> (byte) entry }
	// basic
	byte [] exp = exp_raw.toArray(new byte[exp_raw.size])
	m_t = new Tokenizer("0102030405060708090A0B0C0D0E0F")
	byte[] out = m_t.getHex()
	expect: // mgu.equals(exp, out)
	exp == out
	
	when:
	// with some whitespace
	m_t = new Tokenizer("0102030 405 060708090A0B0C      0D0E0F")
	out = m_t.getHex()
	then:
	// mgu.equals(exp, out)
	exp == out

	// two hexs separated by newline
	when:
	m_t = new Tokenizer("0102030405060708090A0B0C0D0E0F\n01AB3FE")
	out = m_t.getHex()
	then:
	// mgu.equals(exp, out)
	exp == out

	// no remaining strings
	when:
	m_t = new Tokenizer("\n")
	then:
	mgu.equals(null,  m_t.getHex() )

	when:
	m_t = new Tokenizer("\n")
	m_t.getHex(true)
	
	then:
	thrown( TextParseException.class )

	// invalid encoding
	when:
	m_t = new Tokenizer("not_hex")
	m_t.getHex(false)
	then:
	thrown( TextParseException.class )

	when:
	m_t = new Tokenizer("not_hex")
	m_t.getHex(true)
	
	then:
	thrown( TextParseException.class )
    }

}
