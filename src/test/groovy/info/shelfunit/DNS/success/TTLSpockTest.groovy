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

public class TTLSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def private final long S = 1
    def private final long M = 60*S
    def private final long H = 60*M
    def private final long D = 24*H
    def private final long W = 7*D

    def "test_parseTTL"() {
	expect:
	mgu.equals(9876, TTL.parseTTL("9876").intValue())

	mgu.equals(0, TTL.parseTTL("0S").intValue())
	mgu.equals(0, TTL.parseTTL("0M").intValue())
	mgu.equals(0, TTL.parseTTL("0H").intValue())
	mgu.equals(0, TTL.parseTTL("0D").intValue())
	mgu.equals(0L, TTL.parseTTL("0W"))

	mgu.equals(S, TTL.parseTTL("1s"))
	mgu.equals(M, TTL.parseTTL("1m"))
	mgu.equals(H, TTL.parseTTL("1h"))
	mgu.equals(D, TTL.parseTTL("1d"))
	mgu.equals(W, TTL.parseTTL("1w"))

	mgu.equals(98*S, TTL.parseTTL("98S"))
	mgu.equals(76*M, TTL.parseTTL("76M"))
	mgu.equals(54*H, TTL.parseTTL("54H"))
	mgu.equals(32*D, TTL.parseTTL("32D"))
	mgu.equals(10*W, TTL.parseTTL("10W"))

	mgu.equals(98*S+11*M+1234*H+2*D+W, TTL.parseTTL("98S11M1234H2D01W"))
    }

    def "test_parseTTL_invalid"() {
	when:
	TTL.parseTTL(null)
	then:
	thrown( NumberFormatException.class)

	when:
        TTL.parseTTL("")
	then:
	thrown( NumberFormatException.class)

	when:
        TTL.parseTTL("S")
	then:
	thrown( NumberFormatException.class)

	when:
        TTL.parseTTL("10S4B")
	then:
	thrown( NumberFormatException.class)

	when:
        TTL.parseTTL("1S"+0xFFFFFFFFL+"S")
	then:
	thrown( NumberFormatException.class)

	when:
        TTL.parseTTL(""+0x100000000L)
	then:
	thrown( NumberFormatException.class)
    }

    def "test_format"() {
	expect:
	mgu.equals("0S", TTL.format(0))
	mgu.equals("1S", TTL.format(1))
	mgu.equals("59S", TTL.format(59))
	mgu.equals("1M", TTL.format(60))
	mgu.equals("59M", TTL.format(59*M))
	mgu.equals("1M33S", TTL.format(M+33))
	mgu.equals("59M59S", TTL.format(59*M+59*S))
	mgu.equals("1H", TTL.format(H))
	mgu.equals("10H1M21S", TTL.format(10*H+M+21))
	mgu.equals("23H59M59S", TTL.format(23*H+59*M+59))
	mgu.equals("1D", TTL.format(D))
	mgu.equals("4D18H45M30S", TTL.format(4*D+18*H+45*M+30))
	mgu.equals("6D23H59M59S", TTL.format(6*D+23*H+59*M+59))
	mgu.equals("1W", TTL.format(W))
	mgu.equals("10W4D1H21M29S", TTL.format(10*W+4*D+H+21*M+29))
	mgu.equals("3550W5D3H14M7S", TTL.format(0x7FFFFFFFL))
    }

    def "test_format_invalid"() {
	when:
	TTL.format(-1) 
	then: 
	thrown( InvalidTTLException.class )

	when: 
	TTL.format(0x100000000L) 
	then:
        thrown( InvalidTTLException.class )
    }
}
