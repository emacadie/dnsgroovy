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

import info.shelfunit.DNS.*
import java.io.IOException
import java.net.UnknownHostException
import java.util.Arrays
import java.util.Random
import spock.lang.Specification

public class SOARecordSpockTest extends Specification  {

	private Name m_an, m_rn, m_host, m_admin, m_origin
	private long m_ttl, m_serial, m_refresh, m_retry, m_expire, m_minimum
    private final static Random m_random = new Random()

    private static long randomU16() {
	return m_random.nextLong() >>> 48
    }

    private static long randomU32() {
	return m_random.nextLong() >>> 32
    }

    protected void setup_init() throws TextParseException,
				      UnknownHostException {
	    m_an = Name.fromString("My.Absolute.Name.")
	    m_rn = Name.fromString("My.Relative.Name")
	    m_host = Name.fromString("My.Host.Name.")
	    m_admin = Name.fromString("My.Administrative.Name.")
	    m_ttl = randomU16()
	    m_serial = randomU32()
	    m_refresh = randomU32()
	    m_retry = randomU32()
	    m_expire = randomU32()
	    m_minimum = randomU32()
    }
	
    def "test_0arg_init"() throws UnknownHostException {
	    setup_init()
	    SOARecord ar = new SOARecord()
		expect:
	    null == ar.getName()
	    0 == ar.getType()
	    0 == ar.getDClass()
	    0 == ar.getTTL().intValue()
	    null == ar.getHost()
	    null == ar.getAdmin()
	    0 == ar.getSerial().intValue()
	    0 == ar.getRefresh().intValue()
	    0 == ar.getRetry().intValue()
	    0 == ar.getExpire().intValue()
	    0 == ar.getMinimum().intValue()
	}
    
    def "test_getObject_init"() {
	setup_init()
	    SOARecord ar = new SOARecord()
	    Record r = ar.getObject()
		expect:
		r instanceof SOARecord
    }
	
    def "test_10arg_init"() {
	setup_init()
	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)
	    expect:
	    m_an == ar.getName()
	    Type.SOA == ar.getType()
	    DClass.IN == ar.getDClass()
	    m_ttl == ar.getTTL()
	    m_host == ar.getHost()
	    m_admin == ar.getAdmin()
	    m_serial == ar.getSerial()
	    m_refresh == ar.getRefresh()
	    m_retry == ar.getRetry()
	    m_expire == ar.getExpire()
	    m_minimum == ar.getMinimum()
	}
    
    def "test_10arg_relative_name_init"() {
	setup_init()
	    when:
		new SOARecord(m_rn, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
	    then: thrown( RelativeNameException.class )
    }
	
    def "test_10arg_relative_host_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_rn, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
	    then:
	    thrown( RelativeNameException.class )
    }

    def "test_10arg_relative_admin_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_rn, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
	    then:
	    thrown( RelativeNameException.class )
    }

    def "test_10arg_negative_serial_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, -1, m_refresh,
			      m_retry, m_expire, m_minimum)
	    then:   
	    thrown( IllegalArgumentException.class )
    }
	
    def "test_10arg_toobig_serial"() {
	setup_init()
	when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, 0x100000000L, m_refresh,
			      m_retry, m_expire, m_minimum)
	then:
	thrown( IllegalArgumentException.class )
    }
    
    def "test_10arg_negative_refresh_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, -1,
			      m_retry, m_expire, m_minimum)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
    def "test_10arg_toobig_refresh_init"() {
	setup_init()
	when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, 0x100000000L,
			      m_retry, m_expire, m_minimum)
        then:
	thrown( IllegalArgumentException.class )
    }
	
    def "test_10arg_negative_retry_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      -1, m_expire, m_minimum)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
    def "test_10arg_toobig_retry_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      0x100000000L, m_expire, m_minimum)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
    def "test_10arg_negative_expire_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, -1, m_minimum)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
    def "test_10arg_toobig_expire_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, 0x100000000L, m_minimum)
	    then:	
	    thrown( IllegalArgumentException.class )
    }
	
    def "test_10arg_negative_minimun_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, -1)
	    then:
	    thrown( IllegalArgumentException.class )
	}
	
    def "test_10arg_toobig_minimum_init"() {
	setup_init()
	    when:
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, 0x100000000L)
	    then:
	    thrown( IllegalArgumentException.class )	
    }

	protected void setup_rrFromWire() throws TextParseException,
				      UnknownHostException {
	    m_host    = Name.fromString("M.h.N.")
	    m_admin   = Name.fromString("M.a.n.")
	    m_serial  = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry   = 0xEF123456L
	    m_expire  = 0x12345678L
	    m_minimum = 0x3456789AL
	}
	
	def "test_rrFromWire"() throws IOException {
	    setup_rrFromWire()
	    def raw = [
		1, 'm', 1, 'h', 1, 'n', 0, // host
		1, 'm', 1, 'a', 1, 'n', 0, // admin
		(byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x12,	   // serial
		(byte)0xCD, (byte)0xEF, (byte)0x12, (byte)0x34,	   // refresh
		(byte)0xEF, (byte)0x12, (byte)0x34, (byte)0x56,	   // retry
		(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,	   // expire
		(byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A ].collect { entry -> (byte) entry }  // minimum

	    DNSInput di = new DNSInput(raw.toArray(new byte[raw.size]))
	    SOARecord ar = new SOARecord()
	    
	    ar.rrFromWire(di)
		expect:
	    m_host == ar.getHost()
	    m_admin == ar.getAdmin()
	    m_serial == ar.getSerial()
	    m_refresh == ar.getRefresh()
	    m_retry == ar.getRetry()
	    m_expire == ar.getExpire()
	    m_minimum == ar.getMinimum()
	}
    
    protected void setup_rdataFromString() throws TextParseException,
				      UnknownHostException {
	    m_origin = Name.fromString("O.")
	    m_host = Name.fromString("M.h", m_origin)
	    m_admin = Name.fromString("M.a.n.")
	    m_serial = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry = 0xEF123456L
	    m_expire = 0x12345678L
	    m_minimum = 0x3456789AL
	}
	
    def "test_valid_setup_rdataFromString"() throws IOException {
	setup_rdataFromString()
	    Tokenizer t = new Tokenizer("M.h " + m_admin + " " +
					m_serial + " " +
					m_refresh + " " +
					m_retry + " " +
					m_expire + " " +
					m_minimum)
	    SOARecord ar = new SOARecord()
	    
	    ar.rdataFromString(t, m_origin)
	    
	    expect:
	    m_host == ar.getHost()
	    m_admin == ar.getAdmin()
	    m_serial == ar.getSerial()
	    m_refresh == ar.getRefresh()
	    m_retry == ar.getRetry()
	    m_expire == ar.getExpire()
	    m_minimum == ar.getMinimum()
	}

	def "test_relative_name_setup_rdataFromString"() throws IOException {
	    setup_rdataFromString()
	    Tokenizer t = new Tokenizer("M.h " + m_admin + " " +
					m_serial + " " +
					m_refresh + " " +
					m_retry + " " +
					m_expire + " " +
					m_minimum)
	    SOARecord ar = new SOARecord()
	    
	    when:
		ar.rdataFromString(t, null)	
	    then:
	    thrown(RelativeNameException.class )
       }


    protected void setup_rrToString() throws TextParseException {
	    m_an      = Name.fromString("My.absolute.name.")
	    m_ttl     = 0x13A8
	    m_host    = Name.fromString("M.h.N.")
	    m_admin   = Name.fromString("M.a.n.")
	    m_serial  = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry   = 0xEF123456L
	    m_expire  = 0x12345678L
	    m_minimum = 0x3456789AL
    }

    def "test_singleLine_rrToString"() 	{
	setup_rrToString()
	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)

	    String out = ar.rrToString()
	    // println( "Here is ar.rrToString(): " + out )

	    String exp = m_host.toString() + " " + m_admin.toString() + " " + m_serial + " " +
		m_refresh + " " + m_retry + " " + m_expire + " " + m_minimum
	    expect:
	    exp == out
	}

    def "test_multiLine_rrToString"() {
	setup_rrToString()
	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)
	    String re = "^.*\\(\\n" +
		"\\s*" + m_serial + "\\s*;\\s*serial\\n" + // serial
		"\\s*" + m_refresh + "\\s*;\\s*refresh\\n" + // refresh
		"\\s*" + m_retry + "\\s*;\\s*retry\\n" + // retry
		"\\s*" + m_expire + "\\s*;\\s*expire\\n" + // expire
		"\\s*" + m_minimum + "\\s*\\)\\s*;\\s*minimum"; // minimum there was a dollar sign at the end there

	    Options.set("multiline")
	    String out = ar.rrToString()
	    Options.unset("multiline")
	    expect:
	    out.matches(re)
	}

	protected void setup_rrToWire() throws TextParseException {
	    m_an = Name.fromString("My.Abs.Name.")
	    m_ttl = 0x13A8
	    m_host = Name.fromString("M.h.N.")
	    m_admin = Name.fromString("M.a.n.")
	    m_serial = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry = 0xEF123456L
	    m_expire = 0x12345678L
	    m_minimum = 0x3456789AL
	}

    def "test_canonical_rrToWire"() {
	setup_rrToWire()
	
	    def exp = [
		1, 'm', 1, 'h', 1, 'n', 0, // host
		1, 'm', 1, 'a', 1, 'n', 0, // admin
		(byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x12,	   // serial
		(byte)0xCD, (byte)0xEF, (byte)0x12, (byte)0x34,	   // refresh
		(byte)0xEF, (byte)0x12, (byte)0x34, (byte)0x56,	   // retry
		(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,	   // expire
		(byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A ].collect { entry -> (byte) entry }  // minimum

	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)
	    DNSOutput o = new DNSOutput()
	    ar.rrToWire(o, null, true)
	    expect:
	    exp.toArray(new byte[exp.size]) == o.toByteArray()
	}
    
    def "test_case_sensitive_rrToWire"() {
	setup_rrToWire()
	    def exp = [
		1, 'M', 1, 'h', 1, 'N', 0, // host
		1, 'M', 1, 'a', 1, 'n', 0, // admin
		(byte)0xAB, (byte)0xCD, (byte)0xEF, (byte)0x12,	   // serial
		(byte)0xCD, (byte)0xEF, (byte)0x12, (byte)0x34,	   // refresh
		(byte)0xEF, (byte)0x12, (byte)0x34, (byte)0x56,	   // retry
		(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,	   // expire
		(byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A ].collect { entry -> (byte) entry }  // minimum

	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)
	    DNSOutput o = new DNSOutput()
	    ar.rrToWire(o, null, false)
	    expect:
	    exp.toArray(new byte[exp.size]) == o.toByteArray()
	} 
}
