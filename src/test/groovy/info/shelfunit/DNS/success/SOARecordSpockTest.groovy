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

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

	private Name m_an, m_rn, m_host, m_admin
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
	
    def "test_0arg"() throws UnknownHostException {
	
	    setup_init()
	    SOARecord ar = new SOARecord()
		expect:
	    mgu.equals(null, ar.getName())
	    mgu.equals(0, ar.getType())
	    mgu.equals(0, ar.getDClass())
	    mgu.equals(0, ar.getTTL().intValue())
	    mgu.equals(null, ar.getHost())
	    mgu.equals(null, ar.getAdmin())
	    mgu.equals(0, ar.getSerial().intValue())
	    mgu.equals(0, ar.getRefresh().intValue())
	    mgu.equals(0, ar.getRetry().intValue())
	    mgu.equals(0, ar.getExpire().intValue())
	    mgu.equals(0, ar.getMinimum().intValue())
	}
    /*
	public void test_getObject()
	{
	    SOARecord ar = new SOARecord()
	    Record r = ar.getObject()
	    mga.that(r instanceof SOARecord)
	}
	
	public void test_10arg()
	{
	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)
	    mgu.equals(m_an, ar.getName())
	    mgu.equals(Type.SOA, ar.getType())
	    mgu.equals(DClass.IN, ar.getDClass())
	    mgu.equals(m_ttl, ar.getTTL())
	    mgu.equals(m_host, ar.getHost())
	    mgu.equals(m_admin, ar.getAdmin())
	    mgu.equals(m_serial, ar.getSerial())
	    mgu.equals(m_refresh, ar.getRefresh())
	    mgu.equals(m_retry, ar.getRetry())
	    mgu.equals(m_expire, ar.getExpire())
	    mgu.equals(m_minimum, ar.getMinimum())
	}
	
	public void test_10arg_relative_name()
	{
	    try {
		new SOARecord(m_rn, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
		fail("RelativeNameException not thrown")
	    }
	    catch( RelativeNameException e ){}
	}
	
	public void test_10arg_relative_host()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_rn, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
		fail("RelativeNameException not thrown")
	    }
	    catch( RelativeNameException e ){}
	}
	
	public void test_10arg_relative_admin()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_rn, m_serial, m_refresh,
			      m_retry, m_expire, m_minimum)
		fail("RelativeNameException not thrown")
	    }
	    catch( RelativeNameException e ){}
	}
	
	public void test_10arg_negative_serial()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, -1, m_refresh,
			      m_retry, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_toobig_serial()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, 0x100000000L, m_refresh,
			      m_retry, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_negative_refresh()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, -1,
			      m_retry, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_toobig_refresh()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, 0x100000000L,
			      m_retry, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_negative_retry()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      -1, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_toobig_retry()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      0x100000000L, m_expire, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_negative_expire()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, -1, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_toobig_expire()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, 0x100000000L, m_minimum)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_negative_minimun()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, -1)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
	
	public void test_10arg_toobig_minimum()
	{
	    try {
		new SOARecord(m_an, DClass.IN, m_ttl,
			      m_host, m_admin, m_serial, m_refresh,
			      m_retry, m_expire, 0x100000000L)
		fail("IllegalArgumentException not thrown")
	    }
	    catch( IllegalArgumentException e ){}
	}
    }

    public static class Test_rrFromWire extends TestCase
    {
	private Name m_host, m_admin
	private long m_serial, m_refresh, m_retry, m_expire, m_minimum

	protected void setUp() throws TextParseException,
				      UnknownHostException
	{
	    m_host    = Name.fromString("M.h.N.")
	    m_admin   = Name.fromString("M.a.n.")
	    m_serial  = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry   = 0xEF123456L
	    m_expire  = 0x12345678L
	    m_minimum = 0x3456789AL
	}
	
	public void test() throws IOException
	{
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
	    
	    mgu.equals(m_host, ar.getHost())
	    mgu.equals(m_admin, ar.getAdmin())
	    mgu.equals(m_serial, ar.getSerial())
	    mgu.equals(m_refresh, ar.getRefresh())
	    mgu.equals(m_retry, ar.getRetry())
	    mgu.equals(m_expire, ar.getExpire())
	    mgu.equals(m_minimum, ar.getMinimum())
	}
    }
    //////////////////////////////////////////////
    /*
    public static class Test_rdataFromString extends TestCase
    {
	private Name m_host, m_admin, m_origin
	private long m_serial, m_refresh, m_retry, m_expire, m_minimum

	protected void setUp() throws TextParseException,
				      UnknownHostException
	{
	    m_origin = Name.fromString("O.")
	    m_host = Name.fromString("M.h", m_origin)
	    m_admin = Name.fromString("M.a.n.")
	    m_serial = 0xABCDEF12L
	    m_refresh = 0xCDEF1234L
	    m_retry = 0xEF123456L
	    m_expire = 0x12345678L
	    m_minimum = 0x3456789AL
	}
	
	public void test_valid() throws IOException
	{
	    Tokenizer t = new Tokenizer("M.h " + m_admin + " " +
					m_serial + " " +
					m_refresh + " " +
					m_retry + " " +
					m_expire + " " +
					m_minimum)
	    SOARecord ar = new SOARecord()
	    
	    ar.rdataFromString(t, m_origin)
	    
	    mgu.equals(m_host, ar.getHost())
	    mgu.equals(m_admin, ar.getAdmin())
	    mgu.equals(m_serial, ar.getSerial())
	    mgu.equals(m_refresh, ar.getRefresh())
	    mgu.equals(m_retry, ar.getRetry())
	    mgu.equals(m_expire, ar.getExpire())
	    mgu.equals(m_minimum, ar.getMinimum())
	}

	public void test_relative_name() throws IOException
	{
	    Tokenizer t = new Tokenizer("M.h " + m_admin + " " +
					m_serial + " " +
					m_refresh + " " +
					m_retry + " " +
					m_expire + " " +
					m_minimum)
	    SOARecord ar = new SOARecord()
	    
	    try {
		ar.rdataFromString(t, null)
		fail("RelativeNameException not thrown")
	    }
	    catch(RelativeNameException e){}
	}
    }
    */
    //////////////////////////////////////////////
    /*
    public static class Test_rrToString extends TestCase
    {
	private Name m_an, m_host, m_admin
	private long m_ttl, m_serial, m_refresh, m_retry, m_expire, m_minimum

	protected void setUp() throws TextParseException
	{
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

	public void test_singleLine()
	{
	    SOARecord ar = new SOARecord(m_an, DClass.IN, m_ttl,
					 m_host, m_admin, m_serial, m_refresh,
					 m_retry, m_expire, m_minimum)

	    String out = ar.rrToString()
	    // println( "Here is ar.rrToString(): " + out )

	    String exp = m_host.toString() + " " + m_admin.toString() + " " + m_serial + " " +
		m_refresh + " " + m_retry + " " + m_expire + " " + m_minimum
    
	    mgu.equals(exp, out)
	}

	public void test_multiLine()
	{
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

	    mga.that(out.matches(re))
	}
    }
    */
    //////////////////////////////////////////////
    /*
    public static class Test_rrToWire extends TestCase
    {
	private Name m_an, m_host, m_admin
	private long m_ttl, m_serial, m_refresh, m_retry, m_expire, m_minimum

	protected void setUp() throws TextParseException
	{
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

	public void test_canonical()
	{
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

	    mga.that(Arrays.equals(exp.toArray(new byte[exp.size]), o.toByteArray()))
	}

	public void test_case_sensitive()
	{
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

	    mga.that(Arrays.equals(exp.toArray(new byte[exp.size]), o.toByteArray()))
	}
    }
    */
    /*
    public static Test suite()
    {
	TestSuite s = new TestSuite()
	s.addTestSuite(Test_init.class)
	s.addTestSuite(Test_rrFromWire.class)
	s.addTestSuite(Test_rdataFromString.class)
	s.addTestSuite(Test_rrToString.class)
	s.addTestSuite(Test_rrToWire.class)
	return s
    }
    */
}
