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

import java.io.IOException
import java.util.Arrays
import spock.lang.Specification

public class DSRecordSpockTest extends Specification {
    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    def "test_ctor_0arg"() {
	when:
	DSRecord dr = new DSRecord()

	then:
	mgu.equals(null, dr.getName())
	mgu.equals(0, dr.getType())
	mgu.equals(0, dr.getDClass())
	mgu.equals(0, dr.getTTL().intValue())
	mgu.equals(0, dr.getAlgorithm())
	mgu.equals(0, dr.getDigestID())
	mgu.equals(null, dr.getDigest())
	mgu.equals(0, dr.getFootprint())
    }
    
    def "test_getObject"() {
	DSRecord dr = new DSRecord()
	Record r = dr.getObject()
	expect: mga.that(r instanceof DSRecord)
    }
    /*
    public static class Test_Ctor_7argSpock extends TestCase
    {
	private Name	m_n
	private long	m_ttl
	private int	m_footprint
	private int	m_algorithm
	private int	m_digestid
	private byte[]	m_digest	

	protected void setUp() throws TextParseException
	{
	    m_n = Name.fromString("The.Name.")
	    m_ttl = 0xABCDL
	    m_footprint = 0xEF01
	    m_algorithm = 0x23
	    m_digestid = 0x45
	    
	    // orig m_digest = byte[ (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF ] as byte
	    def tempArray = [ (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF ].collect { entry -> (byte) entry }
	    m_digest = tempArray.toArray(new byte[tempArray.size])
	}
	
	public void test_basic() throws TextParseException
	{
	    DSRecord dr = new DSRecord(m_n, DClass.IN, m_ttl,
				       m_footprint, m_algorithm, m_digestid, m_digest)
	    mgu.equals(m_n, dr.getName())
	    mgu.equals(DClass.IN, dr.getDClass())
	    mgu.equals(Type.DS, dr.getType())
	    mgu.equals(m_ttl, dr.getTTL().intValue())
	    mgu.equals(m_footprint, dr.getFootprint())
	    mgu.equals(m_algorithm, dr.getAlgorithm())
	    mgu.equals(m_digestid, dr.getDigestID())
	    assertTrue(Arrays.equals(m_digest, dr.getDigest()))
	}

	public void test_toosmall_footprint() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     -1, m_algorithm, m_digestid, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_toobig_footprint() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     0x10000, m_algorithm, m_digestid, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_toosmall_algorithm() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, -1, m_digestid, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_toobig_algorithm() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, 0x10000, m_digestid, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_toosmall_digestid() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, m_algorithm, -1, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_toobig_digestid() throws TextParseException
	{
	    try {
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, m_algorithm, 0x10000, m_digest)
		fail("IllegalArgumentException not thrown")
	    }
	    catch(IllegalArgumentException e){}
	}

	public void test_null_digest()
	{
	    DSRecord dr = new DSRecord(m_n, DClass.IN, m_ttl,
				       m_footprint, m_algorithm, m_digestid, null)
	    mgu.equals(m_n, dr.getName())
	    mgu.equals(DClass.IN, dr.getDClass())
	    mgu.equals(Type.DS, dr.getType())
	    mgu.equals(m_ttl, dr.getTTL().intValue())
	    mgu.equals(m_footprint, dr.getFootprint())
	    mgu.equals(m_algorithm, dr.getAlgorithm())
	    mgu.equals(m_digestid, dr.getDigestID())
	    mgu.equals(null, dr.getDigest())
	}
    }
*/

    def "test_rrFromWire"() throws IOException {
	when:
	byte[] raw = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, 
		    (byte)0x01, (byte)0x23, (byte)0x45,
		    (byte)0x67, (byte)0x89 ] // .collect { entry -> (byte) entry }
	    DNSInput dnsin = new DNSInput(raw) // .toArray(new byte[raw.size]))

	DSRecord dr = new DSRecord()
	dr.rrFromWire(dnsin)
	def answer = [ (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89 ].collect { entry -> (byte) entry }
	
	then:
	mgu.equals(0xABCD, dr.getFootprint())
	mgu.equals(0xEF, dr.getAlgorithm())
	mgu.equals(0x01, dr.getDigestID())

	mga.that(Arrays.equals( answer.toArray(new byte[answer.size]), dr.getDigest()))
    }

    def "test_rdataFromString"() throws IOException {
	when:
	def byte[] raw = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, 
			   (byte)0x01, (byte)0x23, (byte)0x45,
			   (byte)0x67, (byte)0x89 ] // .collect { entry -> (byte) entry }
	Tokenizer t = new Tokenizer(0xABCD + " " + 0xEF + " " + 0x01 + " 23456789AB")

	DSRecord dr = new DSRecord()
	dr.rdataFromString(t, null)
	def answer = [ (byte)0x23, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xAB ].collect { entry -> (byte) entry }

	then:
	mgu.equals(0xABCD, dr.getFootprint())
	mgu.equals(0xEF, dr.getAlgorithm())
	mgu.equals(0x01, dr.getDigestID())

	mga.that(Arrays.equals( answer.toArray(new byte[answer.size]), dr.getDigest()))
    }

    def "test_rrToString"() throws TextParseException {
	String exp = 0xABCD + " " + 0xEF + " " + 0x01 + " 23456789AB"
	def byteArray = [ (byte)0x23, (byte)0x45, (byte)0x67,
			  (byte)0x89, (byte)0xAB ].collect { entry -> (byte) entry }
	DSRecord dr = new DSRecord(Name.fromString("The.Name."), DClass.IN, 0x123,
				   0xABCD, 0xEF, 0x01,
				   byteArray.toArray(new byte[byteArray.size]) )
        expect: mgu.equals(exp, dr.rrToString())
    }
    
    def test_rrToWire() throws TextParseException {
	def byteArray = [ (byte)0x23, (byte)0x45, (byte)0x67,
			  (byte)0x89, (byte)0xAB ].collect { entry -> (byte) entry }
	DSRecord dr = new DSRecord(Name.fromString("The.Name."), DClass.IN, 0x123,
				   0xABCD, 0xEF, 0x01, byteArray.toArray(new byte[byteArray.size]) )
				   

	def byte[] exp = [ (byte)0xAB, (byte)0xCD, (byte)0xEF, 
			   (byte)0x01, (byte)0x23, (byte)0x45,
			   (byte)0x67, (byte)0x89, (byte)0xAB ] // .collect { entry -> (byte) entry }

	DNSOutput out = new DNSOutput()
	dr.rrToWire(out, null, true)

	// mga.that(Arrays.equals(exp.toArray(new byte[exp.size]), out.toByteArray()))
	expect: mga.that(Arrays.equals(exp, out.toByteArray()))
    }

}
