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
    def mga = new MyGroovyAssert()
    private Tokenizer m_t

    def setup() {
	m_t = null
    }
    /*
    private void assertEquals( byte[] exp, byte[] act )
    {
	assertTrue(java.util.Arrays.equals(exp, act))
    }
    */
    /*
    public void test_get() throws IOException
    {
	m_t = new Tokenizer(new BufferedInputStream(new ByteArrayInputStream("AnIdentifier \"a quoted \\\" string\"\r\n this is \"my\"\t(comment)\nanotherIdentifier (\ramultilineIdentifier\n)".getBytes())))

	Tokenizer.Token tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	assertTrue(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals("AnIdentifier", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	assertFalse(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals(null, tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.QUOTED_STRING, tt.type)
	assertTrue(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals("a quoted \\\" string", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOL, tt.type)
	assertFalse(tt.isString())
	assertTrue(tt.isEOL())
	mgu.equals(null, tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.COMMENT, tt.type)
	assertFalse(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals(" this is \"my\"\t(comment)", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOL, tt.type)
	assertFalse(tt.isString())
	assertTrue(tt.isEOL())
	mgu.equals(null, tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	assertTrue(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals("anotherIdentifier", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	assertTrue(tt.isString())
	assertFalse(tt.isEOL())
	mgu.equals("amultilineIdentifier", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOF, tt.type)
	assertFalse(tt.isString())
	assertTrue(tt.isEOL())
	mgu.equals(null, tt.value)

	// should be able to do this repeatedly
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOF, tt.type)
	assertFalse(tt.isString())
	assertTrue(tt.isEOL())
	mgu.equals(null, tt.value)

	m_t = new Tokenizer("onlyOneIdentifier")
	tt = m_t.get()
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("onlyOneIdentifier", tt.value)

	m_t = new Tokenizer("identifier ")
	tt = m_t.get()
	mgu.equals("identifier", tt.value)
	tt = m_t.get()
	mgu.equals(Tokenizer.EOF, tt.type)


	// some ungets
	m_t = new Tokenizer("identifier \nidentifier2 junk comment")
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier", tt.value)

	m_t.unget()

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	m_t.unget()
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.WHITESPACE, tt.type)
	
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOL, tt.type)
	
	m_t.unget()
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOL, tt.type)
	
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals("identifier2", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.COMMENT, tt.type)
	mgu.equals(" junk comment", tt.value)

	m_t.unget()
	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.COMMENT, tt.type)
	mgu.equals(" junk comment", tt.value)

	tt = m_t.get(true, true)
	mgu.equals(Tokenizer.EOF, tt.type)
	
	m_t = new Tokenizer("identifier ( junk  comment\n )")
	tt = m_t.get()
	mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	mgu.equals(Tokenizer.IDENTIFIER, m_t.get().type)
	mgu.equals(Tokenizer.EOF, m_t.get().type)
    }
    // end monster method
*/
    /*
    public void test_get_invalid() throws IOException
    {
	m_t = new Tokenizer("(this ")
	m_t.get()
	try {m_t.get() fail("TextParseException not thrown")}
	catch( TextParseException e ){}

	m_t = new Tokenizer("\"bad")
	try {m_t.get() fail("TextParseException not thrown")}
	catch( TextParseException e ){}
	
	m_t = new Tokenizer(")")
	try {m_t.get() fail("TextParseException not thrown")}
	catch( TextParseException e ){}
	
	m_t = new Tokenizer("\\")
	try {m_t.get() fail("TextParseException not thrown")}
	catch( TextParseException e ){}

	m_t = new Tokenizer("\"\n")
	try {m_t.get() fail("TextParseException not thrown")}
	catch( TextParseException e ){}
    }

    public void test_File_input() throws IOException
    {
	File tmp = File.createTempFile("dnsjava", "tmp")
	try {
	    FileWriter fw = new FileWriter(tmp)
	    fw.write("file\ninput test")
	    fw.close()

	    m_t = new Tokenizer(tmp)

	    Tokenizer.Token tt = m_t.get()
	    mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	    mgu.equals("file", tt.value)

	    tt = m_t.get()
	    mgu.equals(Tokenizer.EOL, tt.type)

	    tt = m_t.get()
	    mgu.equals(Tokenizer.IDENTIFIER, tt.type)
	    mgu.equals("input", tt.value)

	    tt = m_t.get(false, true)
	    mgu.equals(Tokenizer.COMMENT, tt.type)
	    mgu.equals(" test", tt.value)

	    m_t.close()
	}
	finally {
	    tmp.delete()
	}
    }

    public void test_unwanted_comment() throws IOException
    {
	m_t = new Tokenizer("; this whole thing is a comment\n")
	Tokenizer.Token tt = m_t.get()

	mgu.equals(Tokenizer.EOL, tt.type)
    }

    public void test_unwanted_ungotten_whitespace() throws IOException
    {
	m_t = new Tokenizer(" ")
	Tokenizer.Token tt = m_t.get(true, true)
	m_t.unget()
	tt = m_t.get()
	mgu.equals(Tokenizer.EOF, tt.type)
    }

    public void test_unwanted_ungotten_comment() throws IOException
    {
	m_t = new Tokenizer("; this whole thing is a comment")
	Tokenizer.Token tt = m_t.get(true, true)
	m_t.unget()
	tt = m_t.get()
	mgu.equals(Tokenizer.EOF, tt.type)
    }

    public void test_empty_string() throws IOException
    {
	m_t = new Tokenizer("")
	Tokenizer.Token tt = m_t.get()
	mgu.equals(Tokenizer.EOF, tt.type)

	m_t = new Tokenizer(" ")
	tt = m_t.get()
	mgu.equals(Tokenizer.EOF, tt.type)
    }

    public void test_multiple_ungets() throws IOException
    {
	m_t = new Tokenizer("a simple one")
	Tokenizer.Token tt = m_t.get()

	m_t.unget()
	try {
	    m_t.unget()
	    fail("IllegalStateException not thrown")
	}
	catch( IllegalStateException e ){}
    }

    public void test_getString() throws IOException
    {
	m_t = new Tokenizer("just_an_identifier")
	String out = m_t.getString()
	mgu.equals("just_an_identifier", out)

	m_t = new Tokenizer("\"just a string\"")
	out = m_t.getString()
	mgu.equals("just a string", out)

	m_t = new Tokenizer("; just a comment")
	try {
	    out = m_t.getString();
	    fail("TextParseException not thrown")
	}
	catch( TextParseException e ){}
    }

    public void test_getIdentifier() throws IOException
    {
	m_t = new Tokenizer("just_an_identifier")
	String out = m_t.getIdentifier();
	mgu.equals("just_an_identifier", out)

	m_t = new Tokenizer("\"just a string\"")
	try {
	    m_t.getIdentifier();
	    fail("TextParseException not thrown")
	}
	catch( TextParseException e ){}
    }

    public void test_getLong() throws IOException
    {
	m_t = new Tokenizer((Integer.MAX_VALUE+1L) + "");
	long out = m_t.getLong();
	mgu.equals((Integer.MAX_VALUE+1L), out);

	m_t = new Tokenizer("-10");
	try {
	    m_t.getLong();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("19_identifier");
	try {
	    m_t.getLong();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getUInt32() throws IOException
    {
	m_t = new Tokenizer(0xABCDEF12L + "");
	long out = m_t.getUInt32();
	mgu.equals(0xABCDEF12L, out);

	m_t = new Tokenizer(0x100000000L + "");
	try {
	    m_t.getUInt32();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("-12345");
	try {
	    m_t.getUInt32();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getUInt16() throws IOException
    {
	m_t = new Tokenizer(0xABCDL + "");
	int out = m_t.getUInt16();
	mgu.equals(0xABCDL, out);

	m_t = new Tokenizer(0x10000 + "");
	try {
	    m_t.getUInt16();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("-125");
	try {
	    m_t.getUInt16();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getUInt8() throws IOException
    {
	m_t = new Tokenizer(0xCDL + "");
	int out = m_t.getUInt8();
	mgu.equals(0xCDL, out);

	m_t = new Tokenizer(0x100 + "");
	try {
	    m_t.getUInt8();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("-12");
	try {
	    m_t.getUInt8();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getTTL() throws IOException
    {
	m_t = new Tokenizer("59S");
	mgu.equals(59, m_t.getTTL());

	m_t = new Tokenizer(TTL.MAX_VALUE + "");
	mgu.equals(TTL.MAX_VALUE, m_t.getTTL());

	m_t = new Tokenizer((TTL.MAX_VALUE+1L) + "");
	mgu.equals(TTL.MAX_VALUE, m_t.getTTL());

	m_t = new Tokenizer("Junk");
	try {
	    m_t.getTTL();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getTTLLike() throws IOException
    {
	m_t = new Tokenizer("59S");
	mgu.equals(59, m_t.getTTLLike());

	m_t = new Tokenizer(TTL.MAX_VALUE + "");
	mgu.equals(TTL.MAX_VALUE, m_t.getTTLLike());

	m_t = new Tokenizer((TTL.MAX_VALUE+1L) + "");
	mgu.equals(TTL.MAX_VALUE+1L, m_t.getTTLLike());

	m_t = new Tokenizer("Junk");
	try {
	    m_t.getTTLLike();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getName() throws IOException, TextParseException
    {
	Name root = Name.fromString(".");
	m_t = new Tokenizer("junk");
	Name exp = Name.fromString("junk.");
	Name out = m_t.getName(root);
	mgu.equals(exp, out);

	Name rel = Name.fromString("you.dig");
	m_t = new Tokenizer("junk");
	try {
	    m_t.getName(rel);
	    fail("RelativeNameException not thrown");
	}
	catch( RelativeNameException e ){}

	m_t = new Tokenizer("");
	try {
	    m_t.getName(root);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getEOL() throws IOException
    {
	m_t = new Tokenizer("id");
	m_t.getIdentifier();
	try {
	    m_t.getEOL();
	}
	catch( TextParseException e ){
	    fail(e.getMessage());
	}

	m_t = new Tokenizer("\n");
	try {
	    m_t.getEOL();
	    m_t.getEOL();
	}
	catch( TextParseException e ){
	    fail(e.getMessage());
	}

	m_t = new Tokenizer("id");
	try {
	    m_t.getEOL();
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getBase64() throws IOException
    {
	def exp_raw = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ].collect{ entry -> (byte) entry }
	byte[] exp = exp_raw.toArray(new byte[exp_raw.size] )
	// basic
	m_t = new Tokenizer("AQIDBAUGBwgJ");
	byte[] out = m_t.getBase64();
	mgu.equals(exp, out);

	// with some whitespace
	m_t = new Tokenizer("AQIDB AUGB   wgJ");
	out = m_t.getBase64();
	mgu.equals(exp, out);

	// two base64s separated by newline
	m_t = new Tokenizer("AQIDBAUGBwgJ\nAB23DK");
	out = m_t.getBase64();
	mgu.equals(exp, out);

	// no remaining strings
	m_t = new Tokenizer("\n");
	mgu.equals(null,  m_t.getBase64() );

	m_t = new Tokenizer("\n");
	try {
	    m_t.getBase64(true);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	// invalid encoding
	m_t = new Tokenizer("not_base64");
	try {
	    m_t.getBase64(false);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("not_base64");
	try {
	    m_t.getBase64(true);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }

    public void test_getHex() throws IOException
    {
	def exp_raw = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ].collect{ entry -> (byte) entry }
	// basic
	byte [] exp = exp_raw.toArray(new byte[exp_raw.size])
	m_t = new Tokenizer("0102030405060708090A0B0C0D0E0F");
	byte[] out = m_t.getHex();
	mgu.equals(exp, out);

	// with some whitespace
	m_t = new Tokenizer("0102030 405 060708090A0B0C      0D0E0F");
	out = m_t.getHex();
	mgu.equals(exp, out);

	// two hexs separated by newline
	m_t = new Tokenizer("0102030405060708090A0B0C0D0E0F\n01AB3FE");
	out = m_t.getHex();
	mgu.equals(exp, out);

	// no remaining strings
	m_t = new Tokenizer("\n");
	mgu.equals(null,  m_t.getHex() );

	m_t = new Tokenizer("\n");
	try {
	    m_t.getHex(true);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	// invalid encoding
	m_t = new Tokenizer("not_hex");
	try {
	    m_t.getHex(false);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}

	m_t = new Tokenizer("not_hex");
	try {
	    m_t.getHex(true);
	    fail("TextParseException not thrown");
	}
	catch( TextParseException e ){}
    }
*/
}
