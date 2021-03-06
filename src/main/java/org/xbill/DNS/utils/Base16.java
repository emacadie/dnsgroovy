// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS.utils;

import java.io.*;

/**
 * Routines for converting between Strings of hex-encoded data and arrays of
 * binary data.  This is not actually used by DNS.
 *
 * @author Brian Wellington
 */

public class Base16 {

    private static final String Base16String = "0123456789ABCDEF";
    
    private
    Base16() {}
    
    /**
     * Convert binary data to a hex-encoded String
     * @param b An array containing binary data
     * @return A String containing the encoded data
     */
    public static String
    toString(byte [] b) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
    
        for (int i = 0; i < b.length; i++) {
            short value = (short) (b[i] & 0xFF);
            byte high = (byte) (value >> 4);
            byte low = (byte) (value & 0xF);
            os.write(Base16String.charAt(high));
            os.write(Base16String.charAt(low));
        }
        return new String(os.toByteArray());
    }
    
    /**
     * Convert a hex-encoded String to binary data
     * @param str A String containing the encoded data
     * @return An array containing the binary data, or null if the string is invalid
     */
    public static byte []
    fromString(String str) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte [] raw = str.getBytes();
        for (int i = 0; i < raw.length; i++) {
            if (!Character.isWhitespace((char)raw[i]))
                bs.write(raw[i]);
        }
        byte [] b_in = bs.toByteArray();
        if (b_in.length % 2 != 0) {
            return null;
        }
    
        bs.reset();
        DataOutputStream ds = new DataOutputStream(bs);
    
        for (int i = 0; i < b_in.length; i += 2) {
            byte high = (byte) Base16String.indexOf(Character.toUpperCase((char)b_in[i]));
            byte low = (byte) Base16String.indexOf(Character.toUpperCase((char)b_in[i+1]));
            try {
                ds.writeByte((high << 4) + low);
            }
            catch (IOException e) {
            }
        }
        return bs.toByteArray();
    }
    
}

