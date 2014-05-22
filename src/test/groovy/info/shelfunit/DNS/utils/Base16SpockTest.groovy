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
package info.shelfunit.DNS.utils

import org.xbill.DNS.utils.Base16
import spock.lang.Specification

public class Base16SpockTest extends Specification {

    /*
    public Base16Test( String name )
    {
	super(name)
    }
*/
    def "test_toString_emptyArray"() {
	String out = Base16.toString( new byte[ 0 ] )
	expect:
	"" == out
    }

    def "test_toString_singleByte1"() {
	byte[] data = [ (byte)1 ]
	String out = Base16.toString( data )
	expect:
	"01" == out
    }

    def "test_toString_singleByte2"() {
	byte[] data = [ (byte)16 ]
	String out = Base16.toString( data )
	expect:
	"10" == out
    }

    def "test_toString_singleByte3"() {
	byte[] data = [ (byte)255 ]
	String out = Base16.toString( data )
	expect:
	"FF" == out
    }

    def "test_toString_array1"() {
	def byte[] data = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ] 
	String out = Base16.toString( data )
	expect:
	"0102030405060708090A0B0C0D0E0F" == out
    }

    def "test_fromString_emptyString"() {
	String data = ""
	byte[] out = Base16.fromString( data )
	expect:
	0 == out.length
    }

    def "test_fromString_invalidStringLength"() {
	String data = "1"
	byte[] out = Base16.fromString( data )
	expect:
	null == out
    }

    def "test_fromString_nonHexChars"() {
	String data = "GG"
	byte[] out = Base16.fromString( data )
	/*
	 * the output is basically encoded as (-1<<4) + -1, not sure
	 * we want an assertion for this.
	 */
    }

    def "test_fromString_normal"() {
	when:
	String data = "0102030405060708090A0B0C0D0E0F"
	byte[] out = Base16.fromString( data )
	def byte[] exp = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ] 
	then:
	exp.length == out.length
	for ( int i = 0; i < exp.length; ++i ) {
	    expect: exp[i] == out[i]
	}
    }
}
