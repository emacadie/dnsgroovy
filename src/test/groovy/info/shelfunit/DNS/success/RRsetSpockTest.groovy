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

import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Date
import java.util.Iterator

import org.xbill.DNS.ARecord
import org.xbill.DNS.CNAMERecord
import org.xbill.DNS.DClass
import org.xbill.DNS.Name
import org.xbill.DNS.RRset
import org.xbill.DNS.Record
import org.xbill.DNS.RRSIGRecord
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type

import spock.lang.Specification

public class RRsetSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()
    private RRset m_rs
    Name m_name, m_name2
    long m_ttl
    ARecord m_a1, m_a2
    RRSIGRecord m_s1, m_s2

    def setup() throws TextParseException, UnknownHostException {
	m_rs = new RRset()
	m_name = Name.fromString("this.is.a.test.")
	m_name2 = Name.fromString("this.is.another.test.")
	m_ttl = 0xABCDL
	m_a1 = new ARecord(m_name, DClass.IN, m_ttl,
			   InetAddress.getByName("192.169.232.11"))
	m_a2 = new ARecord(m_name, DClass.IN, m_ttl+1,
			   InetAddress.getByName("192.169.232.12"))

	m_s1 = new RRSIGRecord(m_name, DClass.IN, m_ttl, Type.A, 0xF, 0xABCDEL,
			       new Date(), new Date(), 0xA, m_name,
			       new byte[ 0 ])
	m_s2 = new RRSIGRecord(m_name, DClass.IN, m_ttl, Type.A, 0xF, 0xABCDEL,
			       new Date(), new Date(), 0xA, m_name2,
			       new byte[ 0 ])
    }

    def "test_ctor_0arg"() {
	expect:
	mgu.equals(0, m_rs.size())

	when: m_rs.getDClass()
	then: thrown( IllegalStateException.class )

	when: m_rs.getType()
	then: thrown( IllegalStateException.class )

	when: m_rs.getTTL()
	then: thrown( IllegalStateException.class )

	when: m_rs.getName()
	then: thrown( IllegalStateException.class )

	when: m_rs.first()
	then: thrown( IllegalStateException.class )

	when: m_rs.toString()
	then: thrown( IllegalStateException.class )

	when:
	Iterator itr = m_rs.rrs()
	then:
	mga.notNull(itr)
	mga.that(!itr.hasNext())

	when:
	itr = m_rs.sigs()
	then:
	mga.notNull(itr)
	mga.that(!itr.hasNext())
    }

    /*
// begin monster method
    public void test_basics() throws TextParseException,
				     UnknownHostException
    {
	m_rs.addRR(m_a1)

	mgu.equals(1, m_rs.size())
	mgu.equals(DClass.IN, m_rs.getDClass())
	mgu.equals(m_a1, m_rs.first())
	mgu.equals(m_name, m_rs.getName())
	mgu.equals(m_ttl, m_rs.getTTL())
	mgu.equals(Type.A, m_rs.getType())

	// add it again, and make sure nothing changed
	m_rs.addRR(m_a1)

	mgu.equals(1, m_rs.size())
	mgu.equals(DClass.IN, m_rs.getDClass())
	mgu.equals(m_a1, m_rs.first())
	mgu.equals(m_name, m_rs.getName())
	mgu.equals(m_ttl, m_rs.getTTL())
	mgu.equals(Type.A, m_rs.getType())

	m_rs.addRR(m_a2)

	mgu.equals(2, m_rs.size())
	mgu.equals(DClass.IN, m_rs.getDClass())
	Record r = m_rs.first()
	mgu.equals(m_a1, r)
	mgu.equals(m_name, m_rs.getName())
	mgu.equals(m_ttl, m_rs.getTTL())
	mgu.equals(Type.A, m_rs.getType())

	Iterator itr = m_rs.rrs()
	mgu.equals(m_a1, itr.next())
	mgu.equals(m_a2, itr.next())

	// make sure that it rotates
	itr = m_rs.rrs()
	mgu.equals(m_a2, itr.next())
	mgu.equals(m_a1, itr.next())
	itr = m_rs.rrs()
	mgu.equals(m_a1, itr.next())
	mgu.equals(m_a2, itr.next())

	m_rs.deleteRR(m_a1)
	mgu.equals(1, m_rs.size())
	mgu.equals(DClass.IN, m_rs.getDClass())
	mgu.equals(m_a2, m_rs.first())
	mgu.equals(m_name, m_rs.getName())
	mgu.equals(m_ttl, m_rs.getTTL())
	mgu.equals(Type.A, m_rs.getType())

	// the signature records
	m_rs.addRR(m_s1)
	mgu.equals(1, m_rs.size())
	itr = m_rs.sigs()
	mgu.equals(m_s1, itr.next())
	assertFalse(itr.hasNext())

	m_rs.addRR(m_s1)
	itr = m_rs.sigs()
	mgu.equals(m_s1, itr.next())
	assertFalse(itr.hasNext())

	m_rs.addRR(m_s2)
	itr = m_rs.sigs()
	mgu.equals(m_s1, itr.next())
	mgu.equals(m_s2, itr.next())
	assertFalse(itr.hasNext())

	m_rs.deleteRR(m_s1)
	itr = m_rs.sigs()
	mgu.equals(m_s2, itr.next())
	assertFalse(itr.hasNext())

	
	// clear it all
	m_rs.clear()
	mgu.equals(0, m_rs.size())
	assertFalse(m_rs.rrs().hasNext())
	assertFalse(m_rs.sigs().hasNext())

    } // end monster method
    */
    
    def "test_ctor_1arg"() {
	m_rs.addRR(m_a1)
	m_rs.addRR(m_a2)
	m_rs.addRR(m_s1)
	m_rs.addRR(m_s2)

	RRset rs2 = new RRset( m_rs )
	expect:
	mgu.equals(2, rs2.size())
	mgu.equals(m_a1, rs2.first())
	
	when:
	Iterator itr = rs2.rrs()
	then:
	mgu.equals(m_a1, itr.next())
	mgu.equals(m_a2, itr.next())
	mga.that(!itr.hasNext())
	
	when:
	itr = rs2.sigs()
	then:
	mga.that(itr.hasNext())
	mgu.equals(m_s1, itr.next())
	mga.that(itr.hasNext())
	mgu.equals(m_s2, itr.next())
	mga.that(!itr.hasNext())
    }

    def "test_toString"() {
	m_rs.addRR(m_a1)
	m_rs.addRR(m_a2)
	m_rs.addRR(m_s1)
	m_rs.addRR(m_s2)

	String out = m_rs.toString()

	expect:
	mga.that(out.indexOf(m_name.toString()) != -1)
	mga.that(out.indexOf(" IN A ") != -1)
	mga.that(out.indexOf("[192.169.232.11]") != -1)
	mga.that(out.indexOf("[192.169.232.12]") != -1)
    }
    
    def "test_addRR_invalidType"() throws TextParseException {
	m_rs.addRR(m_a1)
	
	CNAMERecord c = new CNAMERecord(m_name, DClass.IN, m_ttl, Name.fromString("an.alias."))
	
	when: 
	    m_rs.addRR(c)
	then:
	    thrown( IllegalArgumentException.class )
    }
    
    def "test_addRR_invalidName"() throws TextParseException, UnknownHostException {
	m_rs.addRR(m_a1)
	
	m_a2 = new ARecord(m_name2, DClass.IN, m_ttl,
			   InetAddress.getByName("192.169.232.11"))
	
	when: 
	    m_rs.addRR(m_a2)
	then:
            thrown( IllegalArgumentException.class )
    }
    
    def "test_addRR_invalidDClass"() throws TextParseException, UnknownHostException {
	m_rs.addRR(m_a1)
	
	m_a2 = new ARecord(m_name, DClass.CHAOS, m_ttl,
			   InetAddress.getByName("192.169.232.11"))
	
	when: 
	    m_rs.addRR(m_a2)
	then:
	    thrown( IllegalArgumentException.class )
    }

    def "test_TTLcalculation"() {
	when:
	m_rs.addRR(m_a2)
	then: mgu.equals(m_a2.getTTL(), m_rs.getTTL())
	when:
	m_rs.addRR(m_a1)
	then: mgu.equals(m_a1.getTTL(), m_rs.getTTL())

	when:
	Iterator itr = m_rs.rrs()
	then:
	while( itr.hasNext() ){
	    Record r = (Record)itr.next()
	    mgu.equals( m_a1.getTTL(), r.getTTL())
	}
    }

    def "test_Record_placement"() {
	m_rs.addRR(m_a1)
	m_rs.addRR(m_s1)
	m_rs.addRR(m_a2)
	
	when:
	Iterator itr = m_rs.rrs()
	then:
	mga.that(itr.hasNext())
	mgu.equals(m_a1, itr.next())
	mga.that(itr.hasNext())
	mgu.equals(m_a2, itr.next())
	mga.that(!itr.hasNext())

	when:
	itr = m_rs.sigs()
	then:
	mga.that(itr.hasNext())
	mgu.equals(m_s1, itr.next())
	mga.that(!itr.hasNext())
    }

    def "test_noncycling_iterator"() {
	m_rs.addRR(m_a1)
	m_rs.addRR(m_a2)

	when:
	Iterator itr = m_rs.rrs(false)
	then:
	mga.that(itr.hasNext())
	mgu.equals(m_a1, itr.next())
	mga.that(itr.hasNext())
	mgu.equals(m_a2, itr.next())

	when:
	itr = m_rs.rrs(false)
	then:
	mga.that(itr.hasNext())
	mgu.equals(m_a1, itr.next())
	mga.that(itr.hasNext())
	mgu.equals(m_a2, itr.next())
    }

}
