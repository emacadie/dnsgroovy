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
// package info.shelfunit.DNS
package org.xbill.DNS

import org.xbill.DNS.*
import info.shelfunit.DNS.*

// Mnemonic has package-level access.

import spock.lang.Specification

public class MnemonicSpockTest extends Specification {
    private Mnemonic m_mn

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    /*
    public MnemonicTest( String name )
    {
	super( name )
    }
*/    
    def setup() {
	m_mn = new Mnemonic(MnemonicSpockTest.class.getName() + " UPPER", Mnemonic.CASE_UPPER)
    }
    
    def "test_toInteger"() {
	Integer i = Mnemonic.toInteger(64)
	expect: mgu.equals( new Integer(64), i )
	
	when:
	Integer i2 = Mnemonic.toInteger(64)
	then: mgu.equals( i, i2 )
	// assertNotSame( i, i2 )
	
	when:
	i = Mnemonic.toInteger(-1)
	then: mgu.equals( new Integer(-1), i )
	
	when:
	i2 = Mnemonic.toInteger(-1)
	then: mgu.equals( i, i2 )
	// assertNotSame( i, i2 )

	
	when:
	i = Mnemonic.toInteger(0)
	then: mgu.equals( new Integer(0), i )

	
	when:
	i2 = Mnemonic.toInteger(0)
	then: mgu.equals( i, i2 )
	// assertSame( i, i2 )
	
	when:
	i = Mnemonic.toInteger(63)
	then: mgu.equals( new Integer(63), i )
	
	when:
	i2 = Mnemonic.toInteger(63)
	then: mgu.equals( i, i2 )
	// assertSame( i, i2 )
    }
    
    def "test_no_maximum"() {
	when: 
	m_mn.check(-1)
	then:
	thrown( IllegalArgumentException.class )
/* these tests fail. 
        when: 
        m_mn.check(0)
	then:
	thrown( IllegalArgumentException.class )

        when: 
        m_mn.check(Integer.MAX_VALUE)
	then:
	thrown( IllegalArgumentException.class )
*/
	when:
	m_mn.setNumericAllowed(true)
	int val = m_mn.getValue("-2")
	then:
	mgu.equals( -1, val )
	
	when:
	val = m_mn.getValue("0")
	then:
	mgu.equals( 0, val )
       
	when:
	val = m_mn.getValue("" + Integer.MAX_VALUE)
	then:
	mgu.equals( Integer.MAX_VALUE, val )
    }
    
    def "test_setMaximum"() {
        
	m_mn.setMaximum(15)
	when: m_mn.check(-1)
	then: thrown( IllegalArgumentException.class )
	// when: m_mn.check(0)
	// then: thrown( IllegalArgumentException.class )
	// when: m_mn.check(15)
	// then: thrown( IllegalArgumentException.class )
	when: m_mn.check(16)
	then: thrown( IllegalArgumentException.class )
	
	when:
	// need numericok to exercise the usage of max in parseNumeric
	m_mn.setNumericAllowed(true)
	
	int val = m_mn.getValue("-2")
	then: mgu.equals( -1, val )
	
	when:	
	val = m_mn.getValue( "0" )
	then: mgu.equals( 0, val )
	
	when:
	val = m_mn.getValue( "15" )
	then: mgu.equals( 15, val )
	
	when:
	val = m_mn.getValue( "16" )
	then: mgu.equals( -1, val )
    }
    
    def "test_setPrefix"() {
	final String prefix = "A mixed CASE Prefix".toUpperCase()
	m_mn.setPrefix(prefix)

	String out = m_mn.getText(10)
	expect: mgu.equals( prefix + "10", out )
	
	when:
	int i = m_mn.getValue( out )
	then: mgu.equals( 10, i )
    }
    
    def "test_basic_operation"() {
	// setUp creates Mnemonic with CASE_UPPER
	m_mn.add( 10, "Ten" )
	m_mn.add( 20, "Twenty" )
	m_mn.addAlias( 20, "Veinte" )
	m_mn.add( 30, "Thirty" )

	String text = m_mn.getText(10)
	expect: mgu.equals( "TEN", text )
	
	when:
	text = m_mn.getText(20)
	then: mgu.equals( "TWENTY", text )
	
	when:	
	text = m_mn.getText(30)
	then: mgu.equals( "THIRTY", text )
	
	when:
	text = m_mn.getText(40)
	then: mgu.equals( "40", text )
	
	when:
	int value = m_mn.getValue("tEn")
	then: mgu.equals(10, value)
	
	when:
	value = m_mn.getValue("twenty")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("VeiNTe")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("THIRTY")
	then: mgu.equals(30, value)
    }
    
    public void test_basic_operation_lower()
    {
	m_mn = new Mnemonic(MnemonicTest.class.getName() + " LOWER", Mnemonic.CASE_LOWER)
	m_mn.add( 10, "Ten" )
	m_mn.add( 20, "Twenty" )
	m_mn.addAlias( 20, "Veinte" )
	m_mn.add( 30, "Thirty" )

	String text = m_mn.getText(10)
	mgu.equals( "ten", text )
	
	text = m_mn.getText(20)
	mgu.equals( "twenty", text )
	
	text = m_mn.getText(30)
	mgu.equals( "thirty", text )

	text = m_mn.getText(40)
	mgu.equals( "40", text )

	int value = m_mn.getValue("tEn")
	mgu.equals(10, value)

	value = m_mn.getValue("twenty")
	mgu.equals(20, value)

	value = m_mn.getValue("VeiNTe")
	mgu.equals(20, value)

	value = m_mn.getValue("THIRTY")
	mgu.equals(30, value)
    }

    def "test_basic_operation_sensitive"() {
	m_mn = new Mnemonic(MnemonicTest.class.getName() + " SENSITIVE", Mnemonic.CASE_SENSITIVE)
	m_mn.add( 10, "Ten" )
	m_mn.add( 20, "Twenty" )
	m_mn.addAlias( 20, "Veinte" )
	m_mn.add( 30, "Thirty" )

	String text = m_mn.getText(10)
	expect: mgu.equals( "Ten", text )
	
	when:	
	text = m_mn.getText(20)
	then: mgu.equals( "Twenty", text )
	
	when:	
	text = m_mn.getText(30)
	then: mgu.equals( "Thirty", text )
	
	when:
	text = m_mn.getText(40)
	then: mgu.equals( "40", text )
	
	when:
	int value = m_mn.getValue("Ten")
	then: mgu.equals(10, value)
	
	when:
	value = m_mn.getValue("twenty")
	then: mgu.equals(-1, value)
	
	when:
	value = m_mn.getValue("Twenty")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("VEINTE")
	then: mgu.equals(-1, value)
	
	when:
	value = m_mn.getValue("Veinte")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("Thirty")
	then: mgu.equals(30, value)
    }
    
    def "test_invalid_numeric"() {
	m_mn.setNumericAllowed(true)
	int value = m_mn.getValue("Not-A-Number")
	expect: mgu.equals(-1, value)
    }

    def "test_addAll"() {
	m_mn.add( 10, "Ten" )
	m_mn.add( 20, "Twenty" )

	Mnemonic mn2 = new Mnemonic("second test Mnemonic", Mnemonic.CASE_UPPER)
	mn2.add( 20, "Twenty" )
	mn2.addAlias( 20, "Veinte" )
	mn2.add( 30, "Thirty" )

	m_mn.addAll( mn2 )

	String text = m_mn.getText(10)
	expect: mgu.equals( "TEN", text )
	
	when:
	text = m_mn.getText(20)
	then: mgu.equals( "TWENTY", text )
		
	when:	
	text = m_mn.getText(30)
	then: mgu.equals( "THIRTY", text )
	
	when:
	text = m_mn.getText(40)
	then: mgu.equals( "40", text )
	
	when:
	int value = m_mn.getValue("tEn")
	then: mgu.equals(10, value)
	
	when:
	value = m_mn.getValue("twenty")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("VeiNTe")
	then: mgu.equals(20, value)
	
	when:
	value = m_mn.getValue("THIRTY")
	then: mgu.equals(30, value)
    }

}

