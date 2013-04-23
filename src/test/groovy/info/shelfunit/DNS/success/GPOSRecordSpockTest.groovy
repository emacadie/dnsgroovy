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

public class GPOSRecordSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    private Name	m_n
    private long	m_ttl
    private double	m_lat, m_long, m_alt

    def "test_ctor_0arg"() {
	GPOSRecord gr = new GPOSRecord()
	expect:
	mgu.equals(null, gr.getName())
	mgu.equals(0, gr.getType())
	mgu.equals(0, gr.getDClass())
	mgu.equals(0, gr.getTTL().intValue())
    }
        
    def "test_getObject"() {
	GPOSRecord gr = new GPOSRecord()
	Record r = gr.getObject()
	expect:
	mga.that(r instanceof GPOSRecord)
    }
    //////////////////////////////////////


    protected void setUpTest_Ctor_6arg_doubles() throws TextParseException {
	m_n = Name.fromString("The.Name.")
	    m_ttl = 0xABCDL
	    m_lat = -10.43
	    m_long = 76.12
	    m_alt = 100.101
    }
	
	def "test_basic_Ctor_6arg_doubles"() throws TextParseException {
	    setUpTest_Ctor_6arg_doubles()
	    GPOSRecord gr = new GPOSRecord(m_n, DClass.IN, m_ttl,
					   m_long, m_lat, m_alt)
	    expect:
	    mgu.equals(m_n, gr.getName())
	    mgu.equals(DClass.IN, gr.getDClass())
	    mgu.equals(Type.GPOS, gr.getType())
	    mgu.equals(m_ttl, gr.getTTL())
	    mgu.equals(new Double(m_long), new Double(gr.getLongitude()))
	    mgu.equals(new Double(m_lat), new Double(gr.getLatitude()))
	    mgu.equals(new Double(m_alt), new Double(gr.getAltitude()))
	    mgu.equals(new Double(m_long).toString(), gr.getLongitudeString())
	    mgu.equals(new Double(m_lat).toString(), gr.getLatitudeString())
	    mgu.equals(new Double(m_alt).toString(), gr.getAltitudeString())
	}
    
    def "test_toosmall_longitude_Ctor_6arg_doubles"() throws TextParseException {
	setUpTest_Ctor_6arg_doubles()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl, -90.001, m_lat, m_alt)
	    then:
	    thrown(IllegalArgumentException.class )
    }

    def "test_toobig_longitude_Ctor_6arg_doubles"() throws TextParseException {
	setUpTest_Ctor_6arg_doubles()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl, 90.001, m_lat, m_alt)
	    then:
	    thrown(IllegalArgumentException.class )
	}

	def "test_toosmall_latitude_Ctor_6arg_doubles"() throws TextParseException {
	    setUpTest_Ctor_6arg_doubles()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl, m_long, -180.001, m_alt)
	    then: 
	    thrown(IllegalArgumentException.class)
	}
    
    def "test_toobig_latitudesetUpTest_Ctor_6arg_doubles"() throws TextParseException {
	setUpTest_Ctor_6arg_doubles()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl, m_long, 180.001, m_alt)
	    then:
	        thrown(IllegalArgumentException.class)
	}

    def "test_invalid_stringsetUpTest_Ctor_6arg_doubles"() {
	setUpTest_Ctor_6arg_doubles()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl, new Double(m_long).toString(),
			       "120.\\00ABC", new Double(m_alt).toString())
	    then: thrown(IllegalArgumentException.class)
     }	


    
    
    
    
    protected void setUp_Ctor_6arg_Strings() throws TextParseException
	{
	    m_n = Name.fromString("The.Name.")
	    m_ttl = 0xABCDL
	    m_lat = -10.43
	    m_long = 76.12
	    m_alt = 100.101
	}
	
    def "test_basic_setUp_Ctor_6arg_Strings"() throws TextParseException {
	setUp_Ctor_6arg_Strings()
	    GPOSRecord gr = new GPOSRecord(m_n, DClass.IN, m_ttl,
					   new Double(m_long).toString(),
					   new Double(m_lat).toString(),
					   new Double(m_alt).toString())
	    expect:
	    mgu.equals(m_n, gr.getName())
	    mgu.equals(DClass.IN, gr.getDClass())
	    mgu.equals(Type.GPOS, gr.getType())
	    mgu.equals(m_ttl, gr.getTTL())
	    mgu.equals(new Double(m_long), new Double(gr.getLongitude()))
	    mgu.equals(new Double(m_lat), new Double(gr.getLatitude()))
	    mgu.equals(new Double(m_alt), new Double(gr.getAltitude()))
	    mgu.equals(new Double(m_long).toString(), gr.getLongitudeString())
	    mgu.equals(new Double(m_lat).toString(), gr.getLatitudeString())
	    mgu.equals(new Double(m_alt).toString(), gr.getAltitudeString())
	}
    
    def "test_toosmall_longitude_setUp_Ctor_6arg_Strings"() throws TextParseException {
	setUp_Ctor_6arg_Strings()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl,
			       "-90.001", new Double(m_lat).toString(),
			       new Double(m_alt).toString())
	    then:
	    thrown(IllegalArgumentException.class )
	}

    def "test_toobig_longitude_setUp_Ctor_6arg_Strings"() throws TextParseException {
	    setUp_Ctor_6arg_Strings()
	    when:
		new GPOSRecord(m_n, DClass.IN, m_ttl,
			       "90.001", new Double(m_lat).toString(),
			       new Double(m_alt).toString())
	    then:
	    thrown(IllegalArgumentException.class )
    }

    def "test_toosmall_latitude_setUp_Ctor_6arg_Strings"() throws TextParseException {
	setUp_Ctor_6arg_Strings()
	when:
		new GPOSRecord(m_n, DClass.IN, m_ttl,
			       new Double(m_long).toString(), "-180.001",
			       new Double(m_alt).toString())
	then:
	  thrown(IllegalArgumentException.class )
    }

    def "test_toobig_latitude_setUp_Ctor_6arg_Strings"() throws TextParseException {
	setUp_Ctor_6arg_Strings()
	when:
	new GPOSRecord(m_n, DClass.IN, m_ttl,
			       new Double(m_long).toString(), "180.001", new Double(m_alt).toString())
        then:
	thrown(IllegalArgumentException.class)
	
    }
   
    def "test_basic_Test_rrFromWire"() throws IOException {
	    def raw = [ 5, '-', '8', '.', '1', '2',
		      6, '1', '2', '3', '.', '0', '7',
		      3, '0', '.', '0' ].collect { entry -> (byte) entry }
	    DNSInput dnsInput = new DNSInput(raw.toArray(new byte[raw.size()]))
	    
	    GPOSRecord gr = new GPOSRecord()
	    gr.rrFromWire(dnsInput)
	    expect:
	    mgu.equals(new Double(-8.12), new Double(gr.getLongitude()))
	    mgu.equals(new Double(123.07), new Double(gr.getLatitude()))
	    mgu.equals(new Double(0.0), new Double(gr.getAltitude()))
    }
    
    def "test_longitude_toosmall_Test_rrFromWire"() throws IOException {
	    def raw = [ 5, '-', '9', '5', '.', '0',
				      6, '1', '2', '3', '.', '0', '7',
				      3, '0', '.', '0' ].collect { entry -> (byte) entry }
	    DNSInput dnsInput = new DNSInput(raw.toArray(new byte[raw.size()]))
	    
	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rrFromWire(dnsInput)
	    then: thrown(WireParseException.class )
    }

    def "test_longitude_toobig_Test_rrFromWire"() throws IOException {
	    def raw = [ 5, '1', '8', '5', '.', '0',
			6, '1', '2', '3', '.', '0', '7',
			3, '0', '.', '0' ].collect { entry -> (byte) entry }
	    DNSInput dnsInput = new DNSInput(raw.toArray(new byte[raw.size()]))

	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rrFromWire(dnsInput)
		fail("WireParseException not thrown")
	    then:
	    thrown(WireParseException.class )
	}

    def "test_latitude_toosmall_Test_rrFromWire"() throws IOException {
	    def raw = [ 5, '-', '8', '5', '.', '0',
			6, '-', '1', '9', '0', '.', '0',
			3, '0', '.', '0' ].collect { entry -> (byte) entry }
	    DNSInput dnsInput = new DNSInput(raw.toArray(new byte[raw.size()]))

	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rrFromWire(dnsInput)
	    then: thrown(WireParseException.class )
    }
    
    def "test_latitude_toobig_Test_rrFromWire"() throws IOException {
	    def raw = [ 5, '-', '8', '5', '.', '0',
			6, '2', '1', '9', '0', '.', '0',
			3, '0', '.', '0' ].collect { entry -> (byte) entry }
	    DNSInput dnsInput = new DNSInput(raw.toArray(new byte[raw.size()]))

	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rrFromWire(dnsInput)
	    then: thrown(WireParseException.class )
    }
    
    
    def "test_basic_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("10.45 171.121212 1010787")
	    
	    GPOSRecord gr = new GPOSRecord()
	    gr.rdataFromString(t, null)
	    expect:
	    mgu.equals(new Double(10.45), new Double(gr.getLongitude()))
	    mgu.equals(new Double(171.121212), new Double(gr.getLatitude()))
	    mgu.equals(new Double(1010787), new Double(gr.getAltitude()))
	}

    def "test_longitude_toosmall_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("-100.390 171.121212 1010787")
	    
	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rdataFromString(t, null)
	    then: thrown(IOException.class )
    }

    def "test_longitude_toobig_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("90.00001 171.121212 1010787")
	    
	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rdataFromString(t, null)
	    then: thrown(IOException.class)
    }

    def "test_latitude_toosmall_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("0.0 -180.01 1010787")
	    
	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rdataFromString(t, null)
	    then: thrown(IOException.class)
    }

    def "test_latitude_toobig_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("0.0 180.01 1010787")
	    
	    GPOSRecord gr = new GPOSRecord()
	    when:
		gr.rdataFromString(t, null)
	    then: thrown(IOException.class )
    }

    def "test_invalid_string_rdataFromString"() throws IOException {
	    Tokenizer t = new Tokenizer("1.0 2.0 \\435")
	    when:
		GPOSRecord gr = new GPOSRecord()
		gr.rdataFromString(t, null)
	    then:
	    thrown(TextParseException.class)
    }

    def "test_rrToString"() throws TextParseException {
	String exp = "\"10.45\" \"171.121212\" \"1010787.0\""
	    
	GPOSRecord gr = new GPOSRecord(Name.fromString("The.Name."), DClass.IN, 0x123,
				       10.45, 171.121212, 1010787)
	expect:
	mgu.equals(exp, gr.rrToString())
    }

    def "test_rrToWire"() throws TextParseException {
	GPOSRecord gr = new GPOSRecord(Name.fromString("The.Name."), DClass.IN, 0x123, -10.45, 120.0, 111.0)

	def exp = [ 6, '-', '1', '0', '.', '4', '5',
		    5, '1', '2', '0', '.', '0',
		    5, '1', '1', '1', '.', '0' ].collect { entry -> (byte) entry }
	
	DNSOutput out = new DNSOutput()
	gr.rrToWire(out, null, true)

	byte[] bar = out.toByteArray()

	expect:    
	mgu.equals(exp.size, bar.length)
	for( int i = 0; i < exp.size; ++i ) {
	    mgu.equals(exp[i], bar[i])
	}
    }
    /*
    public static Test suite()
    {
	TestSuite s = new TestSuite()
	s.addTestSuite(Test_Ctor_6arg_doubles.class)
	s.addTestSuite(Test_Ctor_6arg_Strings.class)
	s.addTestSuite(Test_rrFromWire.class)
	s.addTestSuite(Test_rdataFromString.class)
	s.addTestSuite(GPOSRecordTest.class)
	return s
    }
    */
}
