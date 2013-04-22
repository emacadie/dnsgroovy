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

import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Arrays
import java.util.Date
import spock.lang.Specification

import info.shelfunit.DNS.*

public class RecordSpockTest extends Specification {

    def mgu = new MyGroovyUtil()
    def mga = new MyGroovyAssert()

    /*
    private static class SubRecord extends Record
    {
	public SubRecord(){}

	public SubRecord(Name name, int type, int dclass, long ttl)
	{
	    super(name, type, dclass, ttl)
	}

	public Record getObject()
	{
	    return null
	}

	public void rrFromWire(DNSInput dnsin) throws IOException {}

	public String rrToString()
	{
	    return "{SubRecord: rrToString}"
	}

	public void rdataFromString(Tokenizer t, Name origin) throws IOException {}

	public void rrToWire(DNSOutput out, Compression c, boolean canonical) {}

	// makes it callable by test code
	public static byte[] byteArrayFromString(String instring) throws TextParseException
	{
	    return Record.byteArrayFromString(instring)
	}

	// make it callable by test code
	public static String byteArrayToString(byte[] inbytes, boolean quote)
	{
	    return Record.byteArrayToString(inbytes, quote)
	}

	// make it callable by test code
	public static String unknownToString(byte[] inbytes)
	{
	    return Record.unknownToString(inbytes)
	}

	public Object clone() throws CloneNotSupportedException
	{
	    throw new CloneNotSupportedException()
	}
    }
*/
    def "test_ctor_0arg"() {
	SubRecordSpockTest sr = new SubRecordSpockTest()
	expect:
	mgu.equals( null, sr.getName())
	mgu.equals(0, sr.getType())
	mgu.equals(0, sr.getTTL().intValue())
	mgu.equals(0, sr.getDClass())
    }
    
    def "test_ctor_4arg"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	int t = Type.A
	int d = DClass.IN
	long ttl = 0xABCDEL

	SubRecordSpockTest r = new SubRecordSpockTest(n, t, d, ttl)
	expect:
	mgu.equals(n, r.getName())
	mgu.equals(t, r.getType())
	mgu.equals(d, r.getDClass())
	mgu.equals(ttl, r.getTTL())
    }
    
    def "test_ctor_4arg_invalid"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	Name r = Name.fromString("my.relative.name")
	int t = Type.A
	int d = DClass.IN
	long ttl = 0xABCDEL

	when:
	    new SubRecordSpockTest(r, t, d, ttl)
	then: thrown( RelativeNameException.class ) 

	when:
	    new SubRecordSpockTest(n, -1, d, ttl)
	then: thrown( InvalidTypeException.class )

	when:
	    new SubRecordSpockTest(n, t, -1, ttl)
	then: thrown( InvalidDClassException.class )

	when:
	    new SubRecordSpockTest(n, t, d, -1)
	then: thrown( InvalidTTLException.class )
    }

    def "test_newRecord_3arg"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	Name r = Name.fromString("my.relative.name")
	int t = Type.A
	int d = DClass.IN

	Record rec = Record.newRecord(n, t, d)

	expect:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(0, rec.getTTL().intValue())

	when:
	    Record.newRecord(r, t, d)
	then: thrown( RelativeNameException.class )
    }

    def "test_newRecord_4arg"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	Name r = Name.fromString("my.relative.name")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8

	Record rec = Record.newRecord(n, t, d, ttl)

	expect:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())

	when:
	    Record.newRecord(r, t, d, ttl)
	then: thrown( RelativeNameException.class )
    }

    def "test_newRecord_5arg"() throws TextParseException, UnknownHostException {
	Name n = Name.fromString("my.name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ]
	InetAddress exp = InetAddress.getByName("123.232.0.255")

	Record rec = Record.newRecord(n, t, d, ttl, data)

	expect:
	mga.that(rec instanceof ARecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mgu.equals(exp, ((ARecord)rec).getAddress())
    }
    
    def "test_newRecord_6arg"() throws TextParseException,
					     UnknownHostException {
	Name n = Name.fromString("my.name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	def byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ]
	InetAddress exp = InetAddress.getByName("123.232.0.255")

	Record rec = Record.newRecord(n, t, d, ttl, 0, null)

	expect:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())

	when:
	rec = Record.newRecord(n, t, d, ttl, data.length, data)
	then:
	mga.that(rec instanceof ARecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mgu.equals(exp, ((ARecord)rec).getAddress())
	
	when:
	rec = Record.newRecord(n, Type.NIMLOC, d, ttl, data.length, data)
	then:
	mga.that(rec instanceof UNKRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(Type.NIMLOC, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mga.that(Arrays.equals(data, ((UNKRecord)rec).getData()))
    }
    
    def "test_newRecord_6arg_invalid"() throws TextParseException {
	Name n = Name.fromString("my.name.")
	Name r = Name.fromString("my.relative.name")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	def byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ] 

	expect:
	mgu.equals( null, Record.newRecord(n, t, d, ttl, 0, new byte[ 0 ]))
	mgu.equals( null, Record.newRecord(n, t, d, ttl, 1, new byte[ 0 ]))
	mgu.equals( null, Record.newRecord(n, t, d, ttl, data.length+1, data))

	when:
	def byte[] b1 = [ data[0], data[1], data[2], data[3], 0 ]
	then:
	mgu.equals( null, Record.newRecord(n, t, d, ttl, 5, b1 ))

	when:
	    Record.newRecord(r, t, d, ttl, 0, null)
	then: thrown( RelativeNameException.class )

    }

    def "test_fromWire"() throws IOException, TextParseException, UnknownHostException {
	Name n = Name.fromString("my.name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	def byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ]
	InetAddress exp = InetAddress.getByName("123.232.0.255")

	DNSOutput out = new DNSOutput()
	n.toWire(out, null)
	out.writeU16(t)
	out.writeU16(d)
	out.writeU32(ttl)
	out.writeU16(data.length)
	out.writeByteArray(data)

	DNSInput dnsin = new DNSInput(out.toByteArray())

	Record rec = Record.fromWire(dnsin, Section.ANSWER, false)

	expect:
	mga.that(rec instanceof ARecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mgu.equals(exp, ((ARecord)rec).getAddress())

	when:
	dnsin = new DNSInput(out.toByteArray())
	rec = Record.fromWire(dnsin, Section.QUESTION, false)
	then:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(0, rec.getTTL().intValue())

	when:
	dnsin = new DNSInput(out.toByteArray())
	rec = Record.fromWire(dnsin, Section.QUESTION)
	then:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(0, rec.getTTL().intValue())

	when:
	rec = Record.fromWire(out.toByteArray(), Section.QUESTION)
	then:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(0, rec.getTTL().intValue())

	when:
	out = new DNSOutput()
	n.toWire(out, null)
	out.writeU16(t)
	out.writeU16(d)
	out.writeU32(ttl)
	out.writeU16(0)

	dnsin = new DNSInput(out.toByteArray())

	rec = Record.fromWire(dnsin, Section.ANSWER, true)
	then:
	mga.that(rec instanceof EmptyRecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())

    }
    
    def "test_toWire"() throws IOException, TextParseException, UnknownHostException {
	Name n = Name.fromString("my.name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	def byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ]

	// a non-QUESTION
	DNSOutput out = new DNSOutput()
	n.toWire(out, null)
	out.writeU16(t)
	out.writeU16(d)
	out.writeU32(ttl)
	out.writeU16(data.length)
	out.writeByteArray(data)

	byte[] exp = out.toByteArray()

	Record rec = Record.newRecord(n, t, d, ttl, data.length, data)

	out = new DNSOutput()

	rec.toWire(out, Section.ANSWER, null)

	byte[] after = out.toByteArray()
	expect:    
	mga.that(Arrays.equals(exp, after))

	// an equivalent call
	when:
	after = rec.toWire(Section.ANSWER)
	then:    
	mga.that(Arrays.equals(exp, after))

	// a QUESTION entry
	when:
	out = new DNSOutput()
	n.toWire(out, null)
	out.writeU16(t)
	out.writeU16(d)

	exp = out.toByteArray()
	out = new DNSOutput()
	rec.toWire(out, Section.QUESTION, null)
	after = out.toByteArray()
	then:    
	mga.that(Arrays.equals(exp, after))

    }
    
    def "test_toWireCanonical"() throws IOException, TextParseException, UnknownHostException {
	Name n = Name.fromString("My.Name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xDBE8
	def byte[] data = [ (byte)123, (byte)232, (byte)0, (byte)255 ] 

	DNSOutput out = new DNSOutput()
	n.toWireCanonical(out)
	out.writeU16(t)
	out.writeU16(d)
	out.writeU32(ttl)
	out.writeU16(data.length)
	out.writeByteArray(data)

	byte[] exp = out.toByteArray()

	Record rec = Record.newRecord(n, t, d, ttl, data.length, data)

	byte[] after = rec.toWireCanonical()
	expect:
	mga.that(Arrays.equals(exp, after))
    }
    
    def "test_rdataToWireCanonical"() throws IOException, TextParseException, UnknownHostException {
	Name n = Name.fromString("My.Name.")
	Name n2 = Name.fromString("My.Second.Name.")
	int t = Type.NS
	int d = DClass.IN
	int ttl = 0xABE99
	DNSOutput out = new DNSOutput()
	n2.toWire(out, null)
	byte[] data = out.toByteArray()

	out = new DNSOutput()
	n2.toWireCanonical(out)
	byte[] exp = out.toByteArray()

	Record rec = Record.newRecord(n, t, d, ttl, data.length, data)
	expect: mga.that(rec instanceof NSRecord)

	when:
	byte[] after = rec.rdataToWireCanonical()
	then:
	mga.that(Arrays.equals(exp, after))
    }

    def "test_rdataToString"() throws IOException, TextParseException, UnknownHostException {
	Name n = Name.fromString("My.Name.")
	Name n2 = Name.fromString("My.Second.Name.")
	int t = Type.NS
	int d = DClass.IN
	int ttl = 0xABE99
	DNSOutput out = new DNSOutput()
	n2.toWire(out, null)
	byte[] data = out.toByteArray()

	Record rec = Record.newRecord(n, t, d, ttl, data.length, data)
	expect:
	mga.that(rec instanceof NSRecord)
	mgu.equals(rec.rrToString(), rec.rdataToString())
    }

    def "test_toString"() throws TextParseException {
	Name n = Name.fromString("My.N.")
	Name n2 = Name.fromString("My.Second.Name.")
	int t = Type.NS
	int d = DClass.IN
	int ttl = 0xABE99
	DNSOutput o = new DNSOutput()
	n2.toWire(o, null)
	byte[] data = o.toByteArray()

	Record rec = Record.newRecord(n, t, d, ttl, data.length, data)
	String out = rec.toString()
	    
	expect:
	mga.that(out.indexOf(n.toString()) != -1)
	mga.that(out.indexOf(n2.toString()) != -1)
	mga.that(out.indexOf("NS") != -1)
	mga.that(out.indexOf("IN") != -1)
	mga.that(out.indexOf(ttl+"") != -1)
	
	when:
	Options.set("BINDTTL")

	out = rec.toString()
	then:
	mga.that(out.indexOf(n.toString()) != -1)
	mga.that(out.indexOf(n2.toString()) != -1)
	mga.that(out.indexOf("NS") != -1)
	mga.that(out.indexOf("IN") != -1)
	mga.that(out.indexOf(TTL.format(ttl)) != -1)

	when:
	Options.set("noPrintIN")
	out = rec.toString()
	then:
	mga.that(out.indexOf(n.toString()) != -1)
	mga.that(out.indexOf(n2.toString()) != -1)
	mga.that(out.indexOf("NS") != -1)
	mga.that(out.indexOf("IN") == -1)
	mga.that(out.indexOf(TTL.format(ttl)) != -1)
    }
    
    def "test_byteArrayFromString"() throws TextParseException {
	String stringin = "the 98 \" \' quick 0xAB brown"
	byte[] out = SubRecordSpockTest.byteArrayFromString(stringin)
	expect:
	mga.that(Arrays.equals(stringin.getBytes(), out))

	when:
	stringin = " \\031Aa\\\\\"\\\\~\\127\\255"
	def byte[] exp = [ (byte)' ', 
			   0x1F, 
			   (byte)'A', 
			   (byte)'a', 
			   (byte)'\\', 
			   (byte)'"', 
			   (byte)'\\', 
			   0x7E, 
			   0x7F, 
			   (byte)0xFF ] 
	out = SubRecordSpockTest.byteArrayFromString(stringin)
        then:
	    mga.that(Arrays.equals(exp, out))
    }

    def "test_byteArrayFromString_invalid"() {
	StringBuffer b = new StringBuffer()
	for( int i = 0; i < 257; ++i){
	    b.append('A')
	}
	
	when:
	    SubRecordSpockTest.byteArrayFromString(b.toString())
	then: thrown( TextParseException.class )

	when:
	    SubRecordSpockTest.byteArrayFromString("\\256")
	then: thrown( TextParseException.class )

	when:
	    SubRecordSpockTest.byteArrayFromString("\\25a")
	then: thrown( TextParseException.class )

	when:
	    SubRecordSpockTest.byteArrayFromString("\\25")
	then: thrown( TextParseException.class )

	b.append("\\233")
	when:
	    SubRecordSpockTest.byteArrayFromString(b.toString())
	then: thrown( TextParseException.class )
	
    }

    def "test_byteArrayToString"() {
	def byte[] bytesin = [ ' ', 0x1F, 'A', 'a', '"', '\\', 0x7E, 0x7F, (byte)0xFF ] 
	String exp = "\" \\031Aa\\\"\\\\~\\127\\255\""
	expect:
	mgu.equals(exp, SubRecordSpockTest.byteArrayToString(bytesin, true))
    }

    def "test_unknownToString"() {
	def byte[] data = [ (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A,
			    (byte)0xBC, (byte)0xDE, (byte)0xFF ] 
	String out = SubRecordSpockTest.unknownToString(data)
	expect:
	mga.that(out.indexOf("" + data.length) != -1)
	mga.that(out.indexOf("123456789ABCDEFF") != -1)
    }
/*
    public void test_fromString() throws IOException, TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name n2 = Name.fromString("My.Second.Name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xABE99
	String sa = "191.234.43.10"
	InetAddress addr = InetAddress.getByName(sa)
	def byte[] b = [ (byte)191, (byte)234, (byte)43, (byte)10 ] 

	Tokenizer st = new Tokenizer(sa)
	Record rec = Record.fromString(n, t, d, ttl, st, n2)
	mga.that(rec instanceof ARecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mgu.equals(addr, ((ARecord)rec).getAddress())

	String unkData = SubRecordSpockTest.unknownToString(b)
	st = new Tokenizer(unkData)
	rec = Record.fromString(n, t, d, ttl, st, n2)
	mga.that(rec instanceof ARecord)
	mgu.equals(n, rec.getName())
	mgu.equals(t, rec.getType())
	mgu.equals(d, rec.getDClass())
	mgu.equals(ttl, rec.getTTL().intValue())
	mgu.equals(addr, ((ARecord)rec).getAddress())
    }

    public void test_fromString_invalid() throws IOException, TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name rel = Name.fromString("My.R")
	Name n2 = Name.fromString("My.Second.Name.")
	int t = Type.A
	int d = DClass.IN
	int ttl = 0xABE99
	InetAddress addr = InetAddress.getByName("191.234.43.10")

	Tokenizer st = new Tokenizer("191.234.43.10")

	when:
	    Record.fromString(rel, t, d, ttl, st, n2)
	then: thrown( RelativeNameException.class )

	st = new Tokenizer("191.234.43.10 another_token")
	when:
	    Record.fromString(n, t, d, ttl, st, n2)
	then: thrown( TextParseException.class )

	st = new Tokenizer("\\# 100 ABCDE")
	when:
	    Record.fromString(n, t, d, ttl, st, n2)
	then: thrown( TextParseException.class )

	when:
	    Record.fromString(n, t, d, ttl, "\\# 100", n2)
	then: thrown( TextParseException.class )
    }

    public void test_getRRsetType() throws TextParseException
    {
	Name n = Name.fromString("My.N.")

	Record r = Record.newRecord(n, Type.A, DClass.IN, 0)
	mgu.equals(Type.A, r.getRRsetType())

	r = new RRSIGRecord(n, DClass.IN, 0, Type.A, 1, 0, new Date(),
			    new Date(), 10, n, new byte[ 0 ])
	mgu.equals(Type.A, r.getRRsetType())
    }

    public void test_sameRRset() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name m = Name.fromString("My.M.")
	def byte[] b1 = [ 0 ]
	Record r1 = Record.newRecord(n, Type.A, DClass.IN, 0)
	Record r2 = new RRSIGRecord(n, DClass.IN, 0, Type.A, 1, 0, new Date(),
				    new Date(), 10, n, b1 )
	mga.that(r1.sameRRset(r2))
	mga.that(r2.sameRRset(r1))

	r1 = Record.newRecord(n, Type.A, DClass.HS, 0)
	r2 = new RRSIGRecord(n, DClass.IN, 0, Type.A, 1, 0, new Date(),
			     new Date(), 10, n, b1 )
	mga.that(r1.sameRRset(r2))
	mga.that(r2.sameRRset(r1))

	r1 = Record.newRecord(n, Type.A, DClass.IN, 0)
	r2 = new RRSIGRecord(m, DClass.IN, 0, Type.A, 1, 0, new Date(),
			     new Date(), 10, n, b1 )
	mga.that(r1.sameRRset(r2))
	mga.that(r2.sameRRset(r1))
    }

    public void test_equals() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name n2 = Name.fromString("my.n.")
	Name m = Name.fromString("My.M.")

	Record r1 = Record.newRecord(n, Type.A, DClass.IN, 0)

	mga.that(r1.equals(null))
	mga.that(r1.equals(new Object()))

	Record r2 = Record.newRecord(n, Type.A, DClass.IN, 0)
	mgu.equals(r1, r2)
	mgu.equals(r2, r1)

	r2 = Record.newRecord(n2, Type.A, DClass.IN, 0)
	mgu.equals(r1, r2)
	mgu.equals(r2, r1)

	r2 = Record.newRecord(n2, Type.A, DClass.IN, 0xABCDE)
	mgu.equals(r1, r2)
	mgu.equals(r2, r1)

	r2 = Record.newRecord(m, Type.A, DClass.IN, 0xABCDE)
	mga.that(r1.equals(r2))
	mga.that(r2.equals(r1))

	r2 = Record.newRecord(n2, Type.MX, DClass.IN, 0xABCDE)
	mga.that(r1.equals(r2))
	mga.that(r2.equals(r1))

	r2 = Record.newRecord(n2, Type.A, DClass.CHAOS, 0xABCDE)
	mga.that(r1.equals(r2))
	mga.that(r2.equals(r1))

	def byte[] d1 = [ 23, 12, 9, (byte)129 ]
	def byte[] d2 = [ (byte)220, 1, (byte)131, (byte)212 ]

	r1 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d1)
	r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d1)

	mgu.equals(r1, r2)
	mgu.equals(r2, r1)

	r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d2)

	mga.that(r1.equals(r2))
	mga.that(r2.equals(r1))
    }

    public void test_hashCode() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name n2 = Name.fromString("my.n.")
	Name m = Name.fromString("My.M.")
	def byte[] d1 = [ 23, 12, 9, (byte)129 ]
	def byte[] d2 = [ (byte)220, 1, (byte)131, (byte)212 ] 

	Record r1 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d1)

	// same record has same hash code
	Record r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d1)
	mgu.equals(r1.hashCode(), r2.hashCode())

	// case of names should not matter
	r2 = Record.newRecord(n2, Type.A, DClass.IN, 0xABCDE9, d1)
	mgu.equals(r1.hashCode(), r2.hashCode())

	// different names
	r2 = Record.newRecord(m, Type.A, DClass.IN, 0xABCDE9, d1)
	mga.that(r1.hashCode() != r2.hashCode())

	// different class
	r2 = Record.newRecord(n, Type.A, DClass.CHAOS, 0xABCDE9, d1)
	mga.that(r1.hashCode() != r2.hashCode())

	// different TTL does not matter
	r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE, d1)
	mgu.equals(r1.hashCode(), r2.hashCode())

	// different data
	r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d2)
	mga.that(r1.hashCode() != r2.hashCode())
    }

    public void test_cloneRecord() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	def byte[] d = [ 23, 12, 9, (byte)129 ]
	Record r = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)

	Record r2 = r.cloneRecord()

	assertNotSame(r, r2)
	mgu.equals(r, r2)

	r = new SubRecordSpockTest(n, Type.A, DClass.IN, 0xABCDE9)

	when:
	    r.cloneRecord()
	    fail("IllegalStateException not thrown")
	}
	catch( IllegalStateException e ){}
    }

    public void test_withName() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name m = Name.fromString("My.M.Name.")
	Name rel = Name.fromString("My.Relative.Name")
	def byte[] d = [ 23, 12, 9, (byte)129 ] 
	Record r = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)

	Record r1 = r.withName(m)
	
	mgu.equals(m, r1.getName())
	mgu.equals(Type.A, r1.getType())
	mgu.equals(DClass.IN, r1.getDClass())
	mgu.equals(0xABCDE9, r1.getTTL().intValue())
	mgu.equals(((ARecord)r).getAddress(), ((ARecord)r1).getAddress())

	when:
	    r.withName(rel)

	}
	then: thrown( RelativeNameException.class )
    }

    public void test_withDClass() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	def byte[] d = [ 23, 12, 9, (byte)129 ]
	Record r = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)

	Record r1 = r.withDClass(DClass.HESIOD, 0x9876)
	
	mgu.equals(n, r1.getName())
	mgu.equals(Type.A, r1.getType())
	mgu.equals(DClass.HESIOD, r1.getDClass())
	mgu.equals(0x9876, r1.getTTL().intValue())
	mgu.equals(((ARecord)r).getAddress(), ((ARecord)r1).getAddress())
    }

    public void test_setTTL() throws TextParseException,
				     UnknownHostException
    {
	Name n = Name.fromString("My.N.")
	def byte[] d = [ 23, 12, 9, (byte)129 ] 
	InetAddress exp = InetAddress.getByName("23.12.9.129")
	Record r = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)

	mgu.equals(0xABCDE9, r.getTTL().intValue())

	r.setTTL(0x9876)

	mgu.equals(n, r.getName())
	mgu.equals(Type.A, r.getType())
	mgu.equals(DClass.IN, r.getDClass())
	mgu.equals(0x9876, r.getTTL().intValue())
	mgu.equals(exp, ((ARecord)r).getAddress())
    }

    public void test_compareTo() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Name n2 = Name.fromString("my.n.")
	Name m = Name.fromString("My.M.")
	def byte[] d  = [ 23, 12, 9, (byte)129 ] 
	def byte[] d2 = [ 23, 12, 9, (byte)128 ] 
	Record r1 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)
	Record r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d)
	
	mgu.equals(0, r1.compareTo(r1))

	mgu.equals(0, r1.compareTo(r2))
	mgu.equals(0, r2.compareTo(r1))

	// name comparison should be canonical
	r2 = Record.newRecord(n2, Type.A, DClass.IN, 0xABCDE9, d)
	mgu.equals(0, r1.compareTo(r2))
	mgu.equals(0, r2.compareTo(r1))

	// different name
	r2 = Record.newRecord(m, Type.A, DClass.IN, 0xABCDE9, d)
	mgu.equals(n.compareTo(m), r1.compareTo(r2))
	mgu.equals(m.compareTo(n), r2.compareTo(r1))

	// different DClass
	r2 = Record.newRecord(n, Type.A, DClass.CHAOS, 0xABCDE9, d)
	mgu.equals(DClass.IN-DClass.CHAOS, r1.compareTo(r2))
	mgu.equals(DClass.CHAOS-DClass.IN, r2.compareTo(r1))

	// different Type
	r2 = Record.newRecord(n, Type.NS, DClass.IN, 0xABCDE9, m.toWire())
	mgu.equals(Type.A-Type.NS, r1.compareTo(r2))
	mgu.equals(Type.NS-Type.A, r2.compareTo(r1))

	// different data (same length)
	r2 = Record.newRecord(n, Type.A, DClass.IN, 0xABCDE9, d2)
	mgu.equals(1, r1.compareTo(r2))
	mgu.equals(-1, r2.compareTo(r1))

	// different data (one a prefix of the other)
	m = Name.fromString("My.N.L.")
	r1 = Record.newRecord(n, Type.NS, DClass.IN, 0xABCDE9, n.toWire())
	r2 = Record.newRecord(n, Type.NS, DClass.IN, 0xABCDE9, m.toWire())
	mgu.equals(-1, r1.compareTo(r2))
	mgu.equals(1, r2.compareTo(r1))
    }

    public void test_getAdditionalName() throws TextParseException
    {
	Name n = Name.fromString("My.N.")
	Record r = new SubRecordSpockTest(n, Type.A, DClass.IN, 0xABCDE9)

	mgu.equals( null, r.getAdditionalName())
    }

    public void test_checkU8()
    {
	when:Record.checkU8("field", -1) fail("IllegalArgumentException not thrown")}
	catch( IllegalArgumentException e ){}
	mgu.equals(0, Record.checkU8("field", 0))
	mgu.equals(0x9D, Record.checkU8("field", 0x9D))
	mgu.equals(0xFF, Record.checkU8("field", 0xFF))
	when:Record.checkU8("field", 0x100) fail("IllegalArgumentException not thrown")}
	catch( IllegalArgumentException e ){}
    }

    public void test_checkU16()
    {
	when:Record.checkU16("field", -1) fail("IllegalArgumentException not thrown")}
	catch( IllegalArgumentException e ){}
	mgu.equals(0, Record.checkU16("field", 0))
	mgu.equals(0x9DA1, Record.checkU16("field", 0x9DA1))
	mgu.equals(0xFFFF, Record.checkU16("field", 0xFFFF))
	when:Record.checkU16("field", 0x10000) fail("IllegalArgumentException not thrown")}
	catch( IllegalArgumentException e ){}
    }

    public void test_checkU32()
    {
	when:Record.checkU32("field", -1) fail("IllegalArgumentException not thrown")}
	catch( IllegalArgumentException e ){}
	mgu.equals(0, Record.checkU32("field", 0))
	mgu.equals(0x9DA1F02DL, Record.checkU32("field", 0x9DA1F02DL))
	mgu.equals(0xFFFFFFFFL, Record.checkU32("field", 0xFFFFFFFFL))
	when: Record.checkU32("field", 0x100000000L)
	then: thrown( IllegalArgumentException.class )
    }
    */
    def "test_checkName"() throws TextParseException {
	Name n = Name.fromString("My.N.")
	Name m = Name.fromString("My.m")

	expect: mgu.equals(n, Record.checkName("field", n))

	when:
	    Record.checkName("field", m)
	then: thrown( RelativeNameException.class )
    }
   
}
