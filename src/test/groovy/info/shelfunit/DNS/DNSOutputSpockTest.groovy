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

import spock.lang.Specification

public class DNSOutputSpockTest extends Specification {
    private DNSOutput m_do
    def mga = new MyGroovyAssert()
    def mgu = new MyGroovyUtil()

    def void setup() {
	m_do = new DNSOutput( 1 )
    }
    /*
    private void assertEquals( byte[] exp, byte[] act )
    {
	assertTrue(java.util.Arrays.equals(exp, act))
    }
    */

    def "test_default_ctor"() {
	m_do = new DNSOutput()
	expect: mgu.equals( 0, m_do.current() )
    }
    
    def "test_initial_state"() {
	expect: mgu.equals( 0, m_do.current() )
        when:
	    m_do.restore()
	then:
	    thrown( IllegalStateException.class )
	
	when:
	    m_do.jump(1)
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_writeU8_basic"() {
	when:
	m_do.writeU8(1)
	then: mgu.equals( 1, m_do.current() )
	    
	when:
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( 1, curr.length )
	mgu.equals( 1, curr[0].intValue() )
    }
    
    def "test_writeU8_expand"() {
	// starts off at 1
	when:
	m_do.writeU8(1)
	m_do.writeU8(2)
	then:
	mgu.equals( 2, m_do.current() )
	    
	when:
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( 2, curr.length )
	mgu.equals( 1, curr[0].intValue() )
	mgu.equals( 2, curr[1].intValue() )
    }

    def "test_writeU8_max"() {
        when:
	m_do.writeU8(0xFF)
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( (byte)0xFF, (byte)curr[0] )
    }
    
    def "test_writeU8_toobig"() {
	when:
	    m_do.writeU8( 0x1FF )
	then:
	    thrown( IllegalArgumentException.class  )
    }
    
    def "test_writeU16_basic"() {
	when:
	m_do.writeU16(0x100)
	then:
	mgu.equals( 2, m_do.current() )

	when:
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( 2, curr.length )
	mgu.equals( 1, curr[0].intValue() )
	mgu.equals( 0, curr[1].intValue() )
    }

    def "test_writeU16_max"() {
	m_do.writeU16(0xFFFF)
	byte[] curr = m_do.toByteArray()
	expect:
	mgu.equals( (byte)0xFF, (byte)curr[0] )
	mgu.equals( (byte)0XFF, (byte)curr[1] )
    }
    
    def "test_writeU16_toobig"() {
	when:
	    m_do.writeU16( 0x1FFFF )
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_writeU32_basic"() {
	when:
	m_do.writeU32(0x11001011)
	then:
	mgu.equals( 4, m_do.current() )

	when:
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( 4, curr.length )
	mgu.equals( 0x11, curr[0].intValue() )
	mgu.equals( 0x00, curr[1].intValue() )
	mgu.equals( 0x10, curr[2].intValue() )
	mgu.equals( 0x11, curr[3].intValue() )
    }
    
    def "test_writeU32_max"() {
        when:
	m_do.writeU32(0xFFFFFFFFL)
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( (byte)0xFF, (byte)curr[0] )
	mgu.equals( (byte)0XFF, (byte)curr[1] )
	mgu.equals( (byte)0XFF, (byte)curr[2] )
	mgu.equals( (byte)0XFF, (byte)curr[3] )
    }
    
    def "test_writeU32_toobig"() {
	when:
	    m_do.writeU32( 0x1FFFFFFFFL )
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_jump_basic"() {
	when:
	m_do.writeU32(0x11223344L)
	then: 
	mgu.equals( 4, m_do.current() )
	when:
	m_do.jump( 2 )
	then:
	mgu.equals( 2, m_do.current() )
	
	when:
	m_do.writeU8( 0x99 )
	byte[] curr = m_do.toByteArray()
	then:
	mgu.equals( 3, curr.length )
	mgu.equals( 0x11, curr[0].intValue() )
	mgu.equals( 0x22, curr[1].intValue() )
	mgu.equals( (byte)0x99, (byte)curr[2] )
	
    }

    def "test_writeByteArray_1arg"() {
	def byte[] b_in = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x12, (byte)0x34 ]// .collect{ entry -> (byte) entry }
	m_do.writeByteArray( b_in )
	expect:
	mgu.equals( 5, m_do.current() )

	when:
	byte[] curr = m_do.toByteArray()
	then: 
	    mga.that(java.util.Arrays.equals(b_in, curr))
	    // mgu.equals( b_in, curr )
    }

    def "test_writeByteArray_3arg"() {
	def byte[] b_in = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x12, (byte)0x34 ] 
	m_do.writeByteArray( b_in, 2, 3 )
	expect: mgu.equals( 3, m_do.current() )

	when:
	def byte[] exp = [ b_in[2], b_in[3], b_in[4] ]
	byte[] curr = m_do.toByteArray()
	then:
	mga.that(java.util.Arrays.equals(exp, curr))
	    // mgu.equals( exp, curr )
    }
    
    def "test_writeCountedString_basic"() {
	def byte[] b_in = [ 'h', 'e', 'l', 'L', '0' ] 
	m_do.writeCountedString( b_in )
	expect: mgu.equals( b_in.length + 1, m_do.current() )

	when:
	byte[] curr = m_do.toByteArray()
	def byte[] exp = [ (byte)(b_in.length), b_in[0], b_in[1], b_in[2], b_in[3], b_in[4] ]
	then:
	mga.that(java.util.Arrays.equals(exp, curr))
	    // mgu.equals( exp, curr )
    }

    def "test_writeCountedString_empty"() {
	byte[] b_in = [] 
	m_do.writeCountedString( b_in )
	expect: mgu.equals( b_in.length + 1, m_do.current() )

	when:
	byte[] curr = m_do.toByteArray()
	def byte [] exp = [ (byte)(b_in.length) ]
	then:
	mga.that(java.util.Arrays.equals(exp, curr))
	    // mgu.equals( exp, curr )
    }

    def "test_writeCountedString_toobig"() {
	byte[] b_in = new byte [ 256 ]
        when:
	    m_do.writeCountedString(b_in)
	then:
	thrown( IllegalArgumentException.class )
	
    }
    
    def "test_save_restore"() {
	m_do.writeU32( 0x12345678L )
	expect: mgu.equals( 4, m_do.current() )

	when:
	m_do.save()
	m_do.writeU16( 0xABCD )
	then:
	mgu.equals( 6, m_do.current() )
	when:
	m_do.restore()
	then:
	mgu.equals( 4, m_do.current() )

	when:
	    m_do.restore()
	then:
	    thrown( IllegalStateException.class )
    }

}
