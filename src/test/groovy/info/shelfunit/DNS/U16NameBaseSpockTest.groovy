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
// package info.shelfunit.DNS;
package org.xbill.DNS

import org.xbill.DNS.*

import java.io.IOException;
import java.util.Arrays;
import spock.lang.Specification
import org.spockframework.util.Assert
import info.shelfunit.DNS.MyGroovyUtil

public class U16NameBaseSpockTest extends Specification {
    def mgu = new MyGroovyUtil()

    /*
    private void assertEquals( byte[] exp, byte[] act )
    {
	assertTrue(java.util.Arrays.equals(exp, act));
    }
*/
    private static class TestSpockClass extends U16NameBase {
	public TestSpockClass(){}

	public TestSpockClass(Name name, int type, int dclass, long ttl)
	{
	    super(name, type, dclass, ttl);
	}
	
	public TestSpockClass(Name name, int type, int dclass, long ttl, int u16Field,
			 String u16Description, Name nameField, String nameDescription) {
	    super(name, type, dclass, ttl, u16Field, u16Description, nameField, nameDescription);
	}
	
	public int getU16Field() {
	    return super.getU16Field();
	}

	public Name getNameField() {
	    return super.getNameField();
	}

	public Record getObject() {
	    return null;
	}
    }

    def "test_ctor_0arg"() {
	when:
	TestSpockClass tc = new TestSpockClass();
	def getTTL = tc.getTTL()
	
	then:
	// tc.getName().equals(null);
	mgu.equals(tc.getName(), null) 
	mgu.equals(0, tc.getType());
	mgu.equals(0, tc.getDClass());
	// tc.getTTL returns java.lang.Long, 
	// 0 is a java.lang.Integer
	mgu.equals(tc.getTTL().intValue(), 0)
        mgu.equals(0, tc.getU16Field());
	mgu.equals(tc.getNameField(), null);
    }
    
    def "test_ctor_4arg"() throws TextParseException {
	when:
	Name n = Name.fromString("My.Name.");
	TestSpockClass tc = new TestSpockClass(n, Type.MX, DClass.IN, 0xBCDA);
	
	then:
	mgu.equals( n, tc.getName());
	mgu.equals( Type.MX,  tc.getType());
	mgu.equals( DClass.IN,  tc.getDClass());
	mgu.equals( tc.getTTL().intValue(), 0xBCDA)
	mgu.equals( 0,  tc.getU16Field());
	mgu.equals( tc.getNameField(), null);
    }
    
    def "test_ctor_8arg"() throws TextParseException {
	when:
	Name n = Name.fromString("My.Name.");
	Name m = Name.fromString("My.Other.Name.");
	
	TestSpockClass tc = new TestSpockClass(
	    n, Type.MX, DClass.IN, 0xB12FL,
	    0x1F2B, "u16 description",
	    m, "name description");
	then:
	mgu.equals( n, tc.getName());
	mgu.equals( Type.MX,  tc.getType());
	mgu.equals( DClass.IN,  tc.getDClass());
        mgu.equals( tc.getTTL(), 0xB12FL) 
	mgu.equals( 0x1F2B,  tc.getU16Field());
	mgu.equals( m,  tc.getNameField());

	// an invalid u16 value
	when: 
	  new TestSpockClass(n, Type.MX, DClass.IN, 0xB12FL,
		0x10000, "u16 description",
		m, "name description");
	then:
	  thrown( IllegalArgumentException.class )

	// a relative name
	when:
	Name rel = Name.fromString("My.relative.Name");
	new TestSpockClass(n, Type.MX, DClass.IN, 0xB12FL,
			  0x1F2B, "u16 description",
			  rel, "name description");
	then:
	    thrown( RelativeNameException.class )
    }
    
    def "test_rrFromWire"() throws IOException {
	when:
	def raw = [ (byte)0xBC, (byte)0x1F, 2, 'M', 'y', 6, 's', 'i', 'N', 'g', 'l', 'E', 4, 'n', 'A', 'm', 'E', 0 ].collect{ entry -> (byte) entry }
	DNSInput dnsin = new DNSInput(raw.toArray(new byte[raw.size] ) );
	
	TestSpockClass tc = new TestSpockClass();
	tc.rrFromWire(dnsin);

	Name exp = Name.fromString("My.single.name.");
	then:
	mgu.equals( 0xBC1FL.intValue(), tc.getU16Field());
	mgu.equals( exp, tc.getNameField());
    }
    
    def "test_rdataFromString"() throws IOException {
	when:
	Name exp = Name.fromString("My.Single.Name.");

	Tokenizer t = new Tokenizer(0x19A2 + " My.Single.Name.");
	TestSpockClass tc = new TestSpockClass();
	tc.rdataFromString(t, null);
	then:
	    mgu.equals( 0x19A2, tc.getU16Field());
	    mgu.equals( exp, tc.getNameField());

	when:
	t = new Tokenizer("10 My.Relative.Name");
	tc = new TestSpockClass();
	tc.rdataFromString(t, null);
	then:
	    thrown( RelativeNameException.class )
    }
    
    def "test_rrToString"() throws IOException, TextParseException {
	Name n = Name.fromString("My.Name.");
	Name m = Name.fromString("My.Other.Name.");
	
	TestSpockClass tc = new TestSpockClass(
	    n, Type.MX, DClass.IN, 0xB12FL,
	    0x1F2B, "u16 description",
	    m, "name description");

	String out = tc.rrToString();
	String exp = 0x1F2B + " My.Other.Name.";
	
	expect: mgu.equals( exp, out);
    }
    
    def "test_rrToWire"() throws IOException, TextParseException
    {
	Name n = Name.fromString("My.Name.");
	Name m = Name.fromString("M.O.n.");
	
	TestSpockClass tc = new TestSpockClass(n, Type.MX, DClass.IN, 0xB12FL,
				     0x1F2B, "u16 description",
				     m, "name description");

	// canonical
	when:
	DNSOutput dout = new DNSOutput();
	tc.rrToWire(dout, null, true);
	byte[] out = dout.toByteArray();
	def exp = [ 0x1F, 0x2B, 1, 'm', 1, 'o', 1, 'n', 0 ].collect{ entry -> (byte) entry }
	then:
	Arrays.equals(exp.toArray(new byte[exp.size]), out )

	// case sensitive
	when:
	dout = new DNSOutput();
	tc.rrToWire(dout, null, false);
	out = dout.toByteArray();
	exp = [ 0x1F, 0x2B, 1, 'M', 1, 'O', 1, 'n', 0 ].collect{ entry -> (byte) entry }
        then:
	Arrays.equals(exp.toArray(new byte[exp.size]), out )
    }

}
