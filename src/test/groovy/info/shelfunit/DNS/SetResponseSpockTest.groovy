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
package	org.xbill.DNS;

import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import spock.lang.Specification
import info.shelfunit.DNS.*

public class SetResponseSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()
	
    def "test_ctor_1arg"() {
	final types = [ SetResponse.UNKNOWN,
					SetResponse.NXDOMAIN,
					SetResponse.NXRRSET,
					SetResponse.DELEGATION,
					SetResponse.CNAME,
					SetResponse.DNAME,
					SetResponse.SUCCESSFUL ] 
	expect:
	types.each() {
	    nextType -> 
	    SetResponse sr = new SetResponse(nextType)
	    mgu.equals(null, sr.getNS())
	    mgu.equals(nextType == SetResponse.UNKNOWN, sr.isUnknown())
	    mgu.equals(nextType == SetResponse.NXDOMAIN, sr.isNXDOMAIN())
	    mgu.equals(nextType == SetResponse.NXRRSET, sr.isNXRRSET())
	    mgu.equals(nextType == SetResponse.DELEGATION, sr.isDelegation())
	    mgu.equals(nextType == SetResponse.CNAME, sr.isCNAME())
	    mgu.equals(nextType == SetResponse.DNAME, sr.isDNAME())
	    mgu.equals(nextType == SetResponse.SUCCESSFUL, sr.isSuccessful())
	}

    }
	
    def "test_ctor_1arg_toosmall"() {
	when:
	    new SetResponse(-1)
	then:
	thrown(IllegalArgumentException.class )
    }

    def "test_ctor_1arg_toobig"() {
	when:
	    new SetResponse(7)
	then:
	thrown(IllegalArgumentException.class )
    }
	
    def "test_ctor_2arg"() {
	final types = [ SetResponse.UNKNOWN,
			SetResponse.NXDOMAIN,
			SetResponse.NXRRSET,
			SetResponse.DELEGATION,
			SetResponse.CNAME,
			SetResponse.DNAME,
			SetResponse.SUCCESSFUL ]

	expect:
	types.each() {
	    nextType -> 
	    RRset rs = new RRset()
	    SetResponse sr = new SetResponse(nextType, rs)
	    mgu.equals(rs, sr.getNS()) // was: assertSame
	    mgu.equals(nextType == SetResponse.UNKNOWN, sr.isUnknown())
	    mgu.equals(nextType == SetResponse.NXDOMAIN, sr.isNXDOMAIN())
	    mgu.equals(nextType == SetResponse.NXRRSET, sr.isNXRRSET())
	    mgu.equals(nextType == SetResponse.DELEGATION, sr.isDelegation())
	    mgu.equals(nextType == SetResponse.CNAME, sr.isCNAME())
	    mgu.equals(nextType == SetResponse.DNAME, sr.isDNAME())
	    mgu.equals(nextType == SetResponse.SUCCESSFUL, sr.isSuccessful())
	}
    }
	
    def "test_ctor_2arg_toosmall"() {
	when:
	    new SetResponse(-1, new RRset())
	then:
	thrown(IllegalArgumentException.class)
    }

    def "test_ctor_2arg_toobig"() {
	when:
	    new SetResponse(7, new RRset())
	then:
	thrown(IllegalArgumentException.class)
    }
	
    def "test_ofType_basic"() {
	final types = [ SetResponse.DELEGATION,
			SetResponse.CNAME,
			SetResponse.DNAME,
			SetResponse.SUCCESSFUL ] 
	expect:
	types.each() {
	    nextType -> 
	    SetResponse sr = SetResponse.ofType(nextType)
	    mgu.equals(null, sr.getNS())
	    mgu.equals(nextType == SetResponse.UNKNOWN, sr.isUnknown())
	    mgu.equals(nextType == SetResponse.NXDOMAIN, sr.isNXDOMAIN())
	    mgu.equals(nextType == SetResponse.NXRRSET, sr.isNXRRSET())
	    mgu.equals(nextType == SetResponse.DELEGATION, sr.isDelegation())
	    mgu.equals(nextType == SetResponse.CNAME, sr.isCNAME())
	    mgu.equals(nextType == SetResponse.DNAME, sr.isDNAME())
	    mgu.equals(nextType == SetResponse.SUCCESSFUL, sr.isSuccessful())

	    SetResponse sr2 = SetResponse.ofType(nextType)
	    !mgu.equals(sr, sr2) // was assertSame
	}
    }
	
    def "test_ofType_singleton"() {
	final types = [ SetResponse.UNKNOWN,
			SetResponse.NXDOMAIN,
			SetResponse.NXRRSET ] 
	expect:
	types.each() {
	    nextType ->
	    SetResponse sr = SetResponse.ofType(nextType)
	    mgu.equals(null, sr.getNS())
	    mgu.equals(nextType == SetResponse.UNKNOWN, sr.isUnknown())
	    mgu.equals(nextType == SetResponse.NXDOMAIN, sr.isNXDOMAIN())
	    mgu.equals(nextType == SetResponse.NXRRSET, sr.isNXRRSET())
	    mgu.equals(nextType == SetResponse.DELEGATION, sr.isDelegation())
	    mgu.equals(nextType == SetResponse.CNAME, sr.isCNAME())
	    mgu.equals(nextType == SetResponse.DNAME, sr.isDNAME())
	    mgu.equals(nextType == SetResponse.SUCCESSFUL, sr.isSuccessful())

	    SetResponse sr2 = SetResponse.ofType(nextType)
	    mgu.equals(sr, sr2) // was assertSame
	}
    }
	
    def "test_ofType_toosmall"() {
	when:
	    SetResponse.ofType(-1)
	then:
	thrown(IllegalArgumentException.class )
    }

    def "test_ofType_toobig"() {
	when:
	    SetResponse.ofType(7)
	then:
	thrown(IllegalArgumentException.class )
    }

    def "test_addRRset"() throws TextParseException, UnknownHostException {
	when:
	RRset rrs = new RRset()
	rrs.addRR(new ARecord(Name.fromString("The.Name."),
			      DClass.IN,
			      0xABCD,
			      InetAddress.getByName("192.168.0.1")))
	rrs.addRR(new ARecord(Name.fromString("The.Name."),
			      DClass.IN,
			      0xABCD,
			      InetAddress.getByName("192.168.0.2")))
	SetResponse sr = new SetResponse(SetResponse.SUCCESSFUL)
	sr.addRRset(rrs)

	// RRset[] exp = new RRset[] { rrs }
	def RRset[] exp = [ rrs ] // as RRset
	then:
	mga.that(Arrays.equals(exp, sr.answers()))
    }
	
    def "test_addRRset_multiple"() throws TextParseException, UnknownHostException {
	when:
	RRset rrs = new RRset()
	rrs.addRR(new ARecord(Name.fromString("The.Name."),
			      DClass.IN,
			      0xABCD,
			      InetAddress.getByName("192.168.0.1")))
	rrs.addRR(new ARecord(Name.fromString("The.Name."),
			      DClass.IN,
			      0xABCD,
			      InetAddress.getByName("192.168.0.2")))

	RRset rrs2 = new RRset()
	rrs2.addRR(new ARecord(Name.fromString("The.Other.Name."),
			      DClass.IN,
			      0xABCE,
			      InetAddress.getByName("192.168.1.1")))
	rrs2.addRR(new ARecord(Name.fromString("The.Other.Name."),
			      DClass.IN,
			      0xABCE,
			      InetAddress.getByName("192.168.1.2")))

	SetResponse sr = new SetResponse(SetResponse.SUCCESSFUL)
	sr.addRRset(rrs)
	sr.addRRset(rrs2)

	def RRset[] exp = [ rrs, rrs2 ] 
	then:
	mga.that(Arrays.equals(exp, sr.answers()))
    }
	
    def "test_answers_nonSUCCESSFUL"() {
	when:
	SetResponse sr = new SetResponse(SetResponse.UNKNOWN, new RRset())
	then:
	mgu.equals(null, sr.answers())
    }
    
    def "test_getCNAME"() throws TextParseException, UnknownHostException {
	when:
	RRset rrs = new RRset()
	CNAMERecord cr = new CNAMERecord(Name.fromString("The.Name."),
					 DClass.IN,
					 0xABCD,
					 Name.fromString("The.Alias."))
	rrs.addRR(cr)
	SetResponse sr = new SetResponse(SetResponse.CNAME, rrs)
	then:
	mgu.equals(cr, sr.getCNAME())
    }
    
    def "test_getDNAME"() throws TextParseException, UnknownHostException {
	when:
	RRset rrs = new RRset()
	DNAMERecord dr = new DNAMERecord(Name.fromString("The.Name."),
					 DClass.IN,
					 0xABCD,
					 Name.fromString("The.Alias."))
	rrs.addRR(dr)
	SetResponse sr = new SetResponse(SetResponse.DNAME, rrs)

	then:
	mgu.equals(dr, sr.getDNAME())
    }
	
    def "test_toString"() throws TextParseException, UnknownHostException
    {
	final types = [ SetResponse.UNKNOWN,
			SetResponse.NXDOMAIN,
			SetResponse.NXRRSET,
			SetResponse.DELEGATION,
			SetResponse.CNAME,
			SetResponse.DNAME,
			SetResponse.SUCCESSFUL ]
	    // println("here is types: " + types )
	    // println("types is a " + types.getClass().getName())

	RRset rrs = new RRset()
	rrs.addRR(new ARecord(Name.fromString("The.Name."),
			      DClass.IN,
			      0xABCD,
			      InetAddress.getByName("192.168.0.1")))
	def labels = [ "unknown",
		       "NXDOMAIN",
		       "NXRRSET",
		       "delegation: " + rrs,
		       "CNAME: " + rrs,
		       "DNAME: " + rrs,
		       "successful" ]
/*
This was commented out
	final String[] labels = [ "unknown",
					       "NXDOMAIN",
					       "NXRRSET",
					       "delegation: " + rrs,
					       "CNAME: " + rrs,
					       "DNAME: " + rrs,
					       "successful" ] as String
*/

	expect:
        types.eachWithIndex() {
	    nextType, i ->
	    SetResponse sr = new SetResponse(nextType, rrs)
	    // println("-- here is labels[ " + i + " ]: " + labels[i])
	    // println("-- here is sr.toString():   " + sr.toString())
	    mgu.equals(labels[i], sr.toString())
	}
    }

}
