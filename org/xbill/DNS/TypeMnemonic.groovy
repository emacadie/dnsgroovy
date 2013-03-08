package org.xbill.DNS;

import java.util.HashMap;

public class TypeMnemonic extends Mnemonic {
	private static HashMap objects;

	public 	TypeMnemonic() {
		super("Type", CASE_UPPER);
		setPrefix("TYPE");
		objects = new HashMap();
	}

	public static void add(int val, String str, Record proto) {
		super.add(val, str);
		objects.put(Mnemonic.toInteger(val), proto);
	}
	
	public static void check(int val) {
		Type.check(val);
	}

	public static Record getProto(int val) {
		check(val);
		return (Record) objects.get(toInteger(val));
	}
  public static void printLength() { 
    println( "objects.size: " + objects.size() )
  }
}