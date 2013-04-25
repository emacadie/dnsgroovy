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

import info.shelfunit.DNS.*
import org.xbill.DNS.utils.*

import spock.lang.Specification

public class Base64SpockTest extends Specification {
    def mgu = new MyGroovyUtil()
    /*
    public Base64Test( String name )
    {
	super(name)
    }
    */
    def "test_toString_empty"() {
	byte[] data = new byte [ 0 ]
	String out = Base64.toString( data )
	expect:
	mgu.equals( "", out )
    }

    def "test_toString_basic1"() {
	byte[] data = [ 0 ]
	String out = Base64.toString( data )
	expect:
	mgu.equals( "AA==", out )
    }

    def "test_toString_basic2"() {
	def byte[] data = [ 0, 0 ] 
	String out = Base64.toString( data )
	expect:
	mgu.equals( "AAA=", out )
    }

    def "test_toString_basic3"() {
	def byte[] data = [ 0, 0, 1 ] 
	String out = Base64.toString( data )
	expect:
	mgu.equals( "AAAB", out )
    }

    def "test_toString_basic4"() {
	def byte[] data = [ (byte)0xFC, 0, 0 ] 
	String out = Base64.toString( data )
	expect:
	mgu.equals( "/AAA", out )
    }

    def "test_toString_basic5"() {
	def byte[] data = [ (byte)0xFF, (byte)0xFF, (byte)0xFF ] 
	String out = Base64.toString( data )
	expect:
	mgu.equals( "////", out )
    }

    def "test_toString_basic6"() {
	def byte[] data = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ] 
	String out = Base64.toString( data )
	expect:
	mgu.equals( "AQIDBAUGBwgJ", out )
    }

    def "test_formatString_empty1"() {
	String out = Base64.formatString( new byte [ 0 ], 5, "", false )
	expect:
	mgu.equals( "", out )
    }

    def "test_formatString_shorter"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 13, "", false )
	expect:
	mgu.equals( "AQIDBAUGBwgJ", out )
    }

    def "test_formatString_sameLength"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 12, "", false )
	expect:
	mgu.equals( "AQIDBAUGBwgJ", out )
    }

    def "test_formatString_oneBreak"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 10, "", false )
	expect:
	mgu.equals( "AQIDBAUGBw\ngJ", out )
    }

    def "test_formatString_twoBreaks1"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 5, "", false )
	expect:
	mgu.equals( "AQIDB\nAUGBw\ngJ", out )
    }

    def "test_formatString_twoBreaks2"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 4, "", false )
	expect:
	mgu.equals( "AQID\nBAUG\nBwgJ", out )
    }

    def "test_formatString_shorterWithPrefix"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 13, "!_", false )
	expect:
	mgu.equals( "!_AQIDBAUGBwgJ", out )
    }

    def "test_formatString_sameLengthWithPrefix"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 12, "!_", false )
	expect:
	mgu.equals( "!_AQIDBAUGBwgJ", out )
    }

    def "test_formatString_oneBreakWithPrefix"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 10, "!_", false )
	expect:
	mgu.equals( "!_AQIDBAUGBw\n!_gJ", out )
    }

    def "test_formatString_twoBreaks1WithPrefix"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 5, "!_", false )
	expect:
	mgu.equals( "!_AQIDB\n!_AUGBw\n!_gJ", out )
    }

    def "test_formatString_twoBreaks2WithPrefix"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 4, "!_", false )
	expect:
	mgu.equals( "!_AQID\n!_BAUG\n!_BwgJ", out )
    }

    def "test_formatString_shorterWithPrefixAndClose"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 13, "!_", true )
	expect:
	mgu.equals( "!_AQIDBAUGBwgJ )", out )
    }

    def "test_formatString_sameLengthWithPrefixAndClose"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 12, "!_", true )
	expect:
	mgu.equals( "!_AQIDBAUGBwgJ )", out )
    }

    def "test_formatString_oneBreakWithPrefixAndClose"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 10, "!_", true )
	expect:
	mgu.equals( "!_AQIDBAUGBw\n!_gJ )", out )
    }

    def "test_formatString_twoBreaks1WithPrefixAndClose"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 5, "!_", true )
	expect:
	mgu.equals( "!_AQIDB\n!_AUGBw\n!_gJ )", out )
    }

    def "test_formatString_twoBreaks2WithPrefixAndClose"() {
	def byte[] b_in = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]  // "AQIDBAUGBwgJ" (12 chars)
	String out = Base64.formatString( b_in, 4, "!_", true )
	expect:
	mgu.equals( "!_AQID\n!_BAUG\n!_BwgJ )", out )
    }

   private boolean array_equals( byte[] exp, byte[] act ) {
       // 	expect:
        boolean result = true
	if ( !mgu.equals( exp.length, act.length ) ) {
	    return false
	}
	for ( int i = 0; i < exp.length; ++i ) {
	    if ( !mgu.equals( exp[i], act[i] ) ) {
		result = false 
	    }
	}
	return result
    }

    def "test_fromString_empty1"() {
	def byte[] data = new byte [ 0 ]
	byte [] out = Base64.fromString( "" )
	expect:
        array_equals( new byte[ 0 ], out ) == true
    }

    def "test_fromString_basic1"() {
	def byte[] exp = [ 0 ] 
	byte [] out = Base64.fromString( "AA==" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_basic2"() {
	def byte[] exp = [ 0, 0 ] 
	byte[] out = Base64.fromString( "AAA=" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_basic3"() {
	def byte[] exp = [ 0, 0, 1 ] 
	byte[] out = Base64.fromString( "AAAB" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_basic4"() {
	def byte[] exp = [ (byte)0xFC, 0, 0 ] 
	byte[] out = Base64.fromString( "/AAA" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_basic5"() {
	def byte[] exp = [ (byte)0xFF, (byte)0xFF, (byte)0xFF ] 
	byte[] out = Base64.fromString( "////" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_basic6"() {
	def byte[] exp = [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ] 
	byte[] out = Base64.fromString( "AQIDBAUGBwgJ" )
	expect:
        array_equals( exp, out ) == true
    }

    def "test_fromString_invalid1"() {
	byte[] out = Base64.fromString( "AAA" )
	expect:
	mgu.equals( null, out )
    }

    def "test_fromString_invalid2"() {
	byte[] out = Base64.fromString( "AA" )
	expect:
	mgu.equals( null, out )
    }

    def "test_fromString_invalid3"() {
	byte[] out = Base64.fromString( "A" )
	expect:
	mgu.equals( null, out )
    }

    def "test_fromString_invalid4"() {
	byte[] out = Base64.fromString( "BB==" )
	expect:
	mgu.equals( null, out )
    }

    def "test_fromString_invalid5"() {
	byte[] out = Base64.fromString( "BBB=" )
	expect:
	mgu.equals( null, out )
    }

}
