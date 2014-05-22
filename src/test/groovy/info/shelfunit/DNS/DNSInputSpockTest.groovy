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

import org.xbill.DNS.DNSInput
import org.xbill.DNS.WireParseException

import java.util.Arrays
import spock.lang.Specification

public class DNSInputSpockTest extends Specification {

    private byte[]	m_raw
    private DNSInput	m_di

    private void assertEquals( byte[] exp, byte[] act )
    {
	assertTrue(Arrays.equals(exp, act))
    }

    def setup() {
	def m_raw_orig = [ 0, 1, 2, 3, 4, 5, (byte)255, (byte)255, (byte)255, (byte)255 ].collect{ entry -> (byte) entry }
	m_raw = m_raw_orig.toArray(new byte[m_raw_orig.size] ) 
	    m_di = new DNSInput( m_raw) 
    }

    def "test_initial_state"() {
	expect:
	 0 == m_di.current() 
	 10 == m_di.remaining() 
    }

    def "test_jump1"() {
	m_di.jump( 1 )
	expect:
	 1 == m_di.current() 
	 9 == m_di.remaining() 
    }
    
    def "test_jump2"() {
	m_di.jump( 9 )
	expect:
	 9 == m_di.current() 
	 1 == m_di.remaining() 
    }

    def "test_jump_invalid"() {
	when:
	    m_di.jump( 10 )
	then:
	thrown( IllegalArgumentException.class )
    }

    def "test_setActive"() {
	m_di.setActive( 5 )
	expect:
	 0 == m_di.current() 
	 5 == m_di.remaining() 
    }
    
    def "test_setActive_boundary1"() {
	m_di.setActive( 10 )
	expect:
	 0 == m_di.current() 
	 10 == m_di.remaining() 
    }

    def "test_setActive_boundary2"() {
	m_di.setActive( 0 )
	expect:
	 0 == m_di.current() 
	 0 == m_di.remaining() 
    }
    
    def "test_setActive_invalid"() {
	when:
	    m_di.setActive( 11 )
	then:
	thrown( IllegalArgumentException.class )
    }

    def "test_clearActive"() {
	when:
	// first without setting active:
	m_di.clearActive()
	then:
	 0 == m_di.current() 
	 10 == m_di.remaining() 

	when:
	m_di.setActive( 5 )
	m_di.clearActive()
	then:
	 0 == m_di.current() 
	 10 == m_di.remaining() 
    }

    def "test_restore_invalid"() {
	when:
	    m_di.restore()
	then:
	thrown( IllegalStateException.class )
    }

    def "test_save_restore"() {
	when:
	m_di.jump( 4 )
	then:
	 4 == m_di.current() 
	 6 == m_di.remaining() 
	when:
	m_di.save()
	m_di.jump( 0 )
	then:
	 0 == m_di.current() 
	 10 == m_di.remaining() 
	when:
	m_di.restore()
	then:
	 4 == m_di.current() 
	 6 == m_di.remaining() 
    }

    def "test_readU8_basic"() throws WireParseException {
	int v1 = m_di.readU8()
	expect:
	 1 == m_di.current() 
	 9 == m_di.remaining() 
	 0 == v1 
    }

    def "test_readU8_maxval"() throws WireParseException {
	m_di.jump( 9 )
	int v1 = m_di.readU8()
	expect:
	 10 == m_di.current() 
	 0 == m_di.remaining() 
	 255 == v1 

	when:
	    v1 = m_di.readU8()
	then:
	thrown( WireParseException.class  )
    }
    
    def "test_readU16_basic"() throws WireParseException {
	int v1 = m_di.readU16()
	expect:
	 2 == m_di.current() 
	 8 == m_di.remaining() 
	 1 == v1 
	when:
	m_di.jump( 1 )
	v1 = m_di.readU16()
	then:  258 == v1 
    }

    def "test_readU16_maxval"() throws WireParseException  {
	m_di.jump(8)
	int v = m_di.readU16()
	expect:
	 10 == m_di.current() 
	 0 == m_di.remaining() 
	 0xFFFF == v 
	
	when:
	    m_di.jump( 9 )
	    m_di.readU16()
	then:
	thrown( WireParseException.class )
    }

    def "test_readU32_basic"() throws WireParseException {
	long v1 = m_di.readU32()
	expect:
	 4 == m_di.current() 
	 6 == m_di.remaining() 
	 66051 == v1.intValue() 
    }

    def "test_readU32_maxval"() throws WireParseException {
	m_di.jump(6)
	long v = m_di.readU32()
	expect:
	 10 == m_di.current() 
	 0 == m_di.remaining() 
	 0xFFFFFFFFL == v 
	
	when:
	    m_di.jump( 7 )
	    m_di.readU32()
	then:
	thrown( WireParseException.class )
    }
    
    def "test_readByteArray_0arg"() throws WireParseException {
	when:
	m_di.jump( 1 )
	byte[] out = m_di.readByteArray()
	then:
	 10 == m_di.current() 
	 0 == m_di.remaining() 
	 9 == out.length 
        for ( i in 0..8 ) {
	     m_raw[i+1] == out[i] 
	}
    }
    
    def "test_readByteArray_0arg_boundary"() throws WireParseException {
	m_di.jump(9)
	m_di.readU8()
	byte[] out = m_di.readByteArray()
	expect:
	 0 == out.length 
    }

    def "test_readByteArray_1arg"() throws WireParseException {
	byte[] out = m_di.readByteArray( 2 )
	expect:
	 2 == m_di.current() 
	 8 == m_di.remaining() 
	 2 == out.length 
	 0.byteValue() == out[0] 
	 1.byteValue() == out[1] 
    }

    def "test_readByteArray_1arg_boundary"() throws WireParseException {
	byte[] out = m_di.readByteArray( 10 )
	expect:
	 10 == m_di.current() 
	 0 == m_di.remaining() 
	//  m_raw == out 
	m_raw == out
    }

    def "test_readByteArray_1arg_invalid"() {
	when:
	    m_di.readByteArray( 11 )
	then:
	thrown( WireParseException.class )
    }
    
    def "test_readByteArray_3arg"() throws WireParseException
    {
	byte[] data = new byte [ 5 ]
	m_di.jump(4)
	
	m_di.readByteArray( data, 1, 4 )
	expect:
	 8 == m_di.current() 
	 0.byteValue() == data[0] 
	for ( i in 0..3 ) {
	     m_raw[i+4] == data[i+1] 
	}
    }
    
    def "test_readCountedSting"() throws WireParseException {
	m_di.jump( 1 )
	byte[] out = m_di.readCountedString()
	// println("out[0]: " + out[0].getClass().getName())
	// println("2: " + 2.getClass().getName())

	expect:
	 1 == out.length 
	 3 == m_di.current() 
	 out[0] == 2.byteValue() 
    }
}
