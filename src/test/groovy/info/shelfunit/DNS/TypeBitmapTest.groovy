// -*- Java -*-
//
// Copyright (c) 2011, org.xbill.DNS
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

import junit.framework.TestCase;

public class TypeBitmapTest extends TestCase {
    public void test_empty() {
	def int [] a_i = [] // as int
      TypeBitmap typeBitmap = new TypeBitmap(a_i);
      assertEquals(typeBitmap.toString(), "");
    }
    
    public void test_typeA() {
	def int[] a_i = [ 1 ] 
      TypeBitmap typeBitmap = new TypeBitmap(a_i);
      assertEquals(typeBitmap.toString(), "A");
    }
    
    public void test_typeNSandSOA() {
	def int[] int_map = [ 2, 6 ]
      TypeBitmap typeBitmap = new TypeBitmap(int_map);
      assertEquals(typeBitmap.toString(), "NS SOA");
    }
}
