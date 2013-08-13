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

import info.shelfunit.DNS.MyGroovyAssert
import info.shelfunit.DNS.MyGroovyUtil
// import org.xbill.DNS.DNSInput
// import org.xbill.DNS.DNSOutput
// import org.xbill.DNS.EmptyRecord
// import org.xbill.DNS.Record
// import org.xbill.DNS.Tokenizer

import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification

public class EmptyRecordSpockTest extends Specification {
	
    def "test_ctor"() throws UnknownHostException {
	EmptyRecord ar = new EmptyRecord()
	expect:
	null == ar.getName()
	0 == ar.getType()
	0 == ar.getDClass()
	0 == ar.getTTL().intValue()
    }

    def "test_getObject"() {
	EmptyRecord ar = new EmptyRecord()
	Record r = ar.getObject()
	expect: 
	    r instanceof EmptyRecord
    }

    def "test_rrFromWire"() throws IOException {
	def raw = [ 1, 2, 3, 4, 5 ].collect { entry -> (byte) entry }
	byte[] b_array = raw.toArray(new byte[raw.size()])
	DNSInput i = new DNSInput(b_array)  
	i.jump(3)

	EmptyRecord er = new EmptyRecord()
	er.rrFromWire(i)
	
	expect:
	3 == i.current()
	null == er.getName()
	0 == er.getType()
	0 == er.getDClass()
	0 == er.getTTL().intValue()
    }

    def "test_rdataFromString"() throws IOException {
	Tokenizer t = new Tokenizer("these are the tokens")
	EmptyRecord er = new EmptyRecord()
	er.rdataFromString(t, null)

	expect:
	null == er.getName()
	0 == er.getType()
	0 == er.getDClass()
	0 == er.getTTL().intValue()	
	"these" == t.getString()
    }

    def "test_rrToString"() {
	EmptyRecord er = new EmptyRecord()
	expect: 
	    "" == er.rrToString()
    }

    def "test_rrToWire"() {
	EmptyRecord er = new EmptyRecord()
	DNSOutput out = new DNSOutput()
	er.rrToWire(out, null, true)
	expect: 
	    0 == out.toByteArray().length
    }
}
