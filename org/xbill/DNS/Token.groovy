// Copyright (c) 2003-2004 Brian Wellington (bwelling@xbill.org)
//
// Copyright (C) 2003-2004 Nominum, Inc.
// 
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND NOMINUM DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL NOMINUM BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
// OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
//

package org.xbill.DNS;

import java.io.*;
import java.net.*;

import org.xbill.DNS.utils.*;

public static class Token {
	/** The type of token. */
	public int type;

	/** The value of the token, or null for tokens without values. */
	public String value;

	private Token() {
		type = -1;
		value = null;
	}

	private Token set(int type, StringBuffer value) {
		if (type < 0)
			throw new IllegalArgumentException();
		this.type = type;
		this.value = value == null ? null : value.toString();
		return this;
	}

	/**
	 * Converts the token to a string containing a representation useful
	 * for debugging.
	 */
	public String 	toString() {
		switch (type) {
		case EOF:
			return "<eof>";
		case EOL:
			return "<eol>";
		case WHITESPACE:
			return "<whitespace>";
		case IDENTIFIER:
			return "<identifier: " + value + ">";
		case QUOTED_STRING:
			return "<quoted_string: " + value + ">";
		case COMMENT:
			return "<comment: " + value + ">";
		default:
			return "<unknown>";
		}
	}

	/** Indicates whether this token contains a string. */
	public boolean 	isString() {
		return (type == IDENTIFIER || type == QUOTED_STRING);
	}

	/** Indicates whether this token contains an EOL or EOF. */
	public boolean 	isEOL() {
		return (type == EOL || type == EOF);
	}
}