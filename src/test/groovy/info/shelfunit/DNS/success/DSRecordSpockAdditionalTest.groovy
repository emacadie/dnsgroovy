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

public class DSRecordSpockAdditionalTest extends Specification {
    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()
    
    def private Name m_n
    def private long m_ttl
    def private int	m_footprint
    def private int	m_algorithm
    def private int	m_digestid
    def private byte[] m_digest	

	def setup() throws TextParseException {
	    m_n = Name.fromString("The.Name.")
	    m_ttl = 0xABCDL
	    m_footprint = 0xEF01
	    m_algorithm = 0x23
	    m_digestid = 0x45
	    
	    // orig m_digest = byte[ (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF ] as byte
	    def tempArray = [ (byte)0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF ].collect { entry -> (byte) entry }
	    m_digest = tempArray.toArray(new byte[tempArray.size])
	}
	
	def "test_basic"() throws TextParseException {
	    when:
	    DSRecord dr = new DSRecord(m_n, DClass.IN, m_ttl,
				       m_footprint, m_algorithm, m_digestid, m_digest)
	    then:
	    mgu.equals(m_n, dr.getName())
	    mgu.equals(DClass.IN, dr.getDClass())
	    mgu.equals(Type.DS, dr.getType())
	    mgu.equals(m_ttl, dr.getTTL())
	    mgu.equals(m_footprint, dr.getFootprint())
	    mgu.equals(m_algorithm, dr.getAlgorithm())
	    mgu.equals(m_digestid, dr.getDigestID())
	    mga.that(Arrays.equals(m_digest, dr.getDigest()))
	}
    
	def "test_toosmall_footprint"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     -1, m_algorithm, m_digestid, m_digest)
		// fail("IllegalArgumentException not thrown")
	    then:
	    thrown(IllegalArgumentException.class )
	}

	def "test_toobig_footprint"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     0x10000, m_algorithm, m_digestid, m_digest)
		    // fail("IllegalArgumentException not thrown")
	    then:
	    thrown(IllegalArgumentException.class )
	}
    
	def "test_toosmall_algorithm"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, -1, m_digestid, m_digest)
		    // fail("IllegalArgumentException not thrown")
	    then:
	    thrown(IllegalArgumentException.class)
	}

	def "test_toobig_algorithm"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, 0x10000, m_digestid, m_digest)
	    then:
	    thrown(IllegalArgumentException.class)
	}

	def "test_toosmall_digestid"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, m_algorithm, -1, m_digest)
	    then:
	    thrown(IllegalArgumentException.class)
	}

	def "test_toobig_digestid"() throws TextParseException {
	    when:
		new DSRecord(m_n, DClass.IN, m_ttl,
			     m_footprint, m_algorithm, 0x10000, m_digest)
	    then:
	    thrown(IllegalArgumentException.class)
	}

	def "test_null_digest"() {
	    when:
	    DSRecord dr = new DSRecord(m_n, DClass.IN, m_ttl,
				       m_footprint, m_algorithm, m_digestid, null)
	    then:
	    mgu.equals(m_n, dr.getName())
	    mgu.equals(DClass.IN, dr.getDClass())
	    mgu.equals(Type.DS, dr.getType())
	    mgu.equals(m_ttl, dr.getTTL())
	    mgu.equals(m_footprint, dr.getFootprint())
	    mgu.equals(m_algorithm, dr.getAlgorithm())
	    mgu.equals(m_digestid, dr.getDigestID())
	    mgu.equals(null, dr.getDigest())
       }

}
