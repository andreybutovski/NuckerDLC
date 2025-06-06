/*******************************************************************************
* Copyright (c) 2009-2011 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package im.nucker.scripts.interpreter;


/**
 * RuntimeException that is thrown and caught in response to a lua error. 
 * <p>
 * {@link LuaError} is used wherever a lua call to {@code error()} 
 * would be used within a script.
 * <p>
 * Since it is an unchecked exception inheriting from {@link RuntimeException},
 * Java method signatures do notdeclare this exception, althoug it can 
 * be thrown on almost any luaj Java operation.
 * This is analagous to the fact that any lua script can throw a lua error at any time.
 * <p> 
 * The LuaError may be constructed with a message object, in which case the message
 * is the string representation of that object.  getMessageObject will get the object
 * supplied at construct time, or a LuaString containing the message of an object 
 * was not supplied.
 */
public class LuaError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public int line = 0;

	public int level;

	public String fileline;


	public String traceback;

	public Throwable cause;

	public LuaValue object;
	
	/** Get the string message if it was supplied, or a string 
	 * representation of the message object if that was supplied.
	 */
	public String getMessage() {
		if (traceback != null)
			return traceback;
		String m = super.getMessage();
		if (m == null)
			return null;
		if (fileline != null)
			return fileline + " " + m;
		return m;
	}

	/** Get the LuaValue that was provided in the constructor, or 
	 * a LuaString containing the message if it was a string error argument.
	 * @return LuaValue which was used in the constructor, or a LuaString
	 * containing the message.
	 */
	public LuaValue getMessageObject() {
		if (object != null) return object;
		String m = getMessage();
		return m != null ? LuaValue.valueOf(m): null;
	}
	
	/** Construct LuaError when a program exception occurs. 
	 * <p> 
	 * All errors generated from lua code should throw LuaError(String) instead.
	 * @param cause the Throwable that caused the error, if known.  
	 */
	public LuaError(Throwable cause) {
		super( "vm error: "+cause );

		this.cause = cause;
		this.level = 1;
	}

	/**
	 * Construct a LuaError with a specific message.  
	 *  
	 * @param message message to supply
	 */
	public LuaError(String message, int line, boolean b) {
		super( message );
		this.line = line;
		this.level = 1;
	}

	public LuaError(String message) {
		super( message );
		this.level = 1;
	}

	/**
	 * Construct a LuaError with a message, and level to draw line number information from.
	 * @param message message to supply
	 * @param level where to supply line info from in call stack
	 */
	public LuaError(String message, int level) {
		super( message );
		this.level = level;
	}	

	/**
	 * Construct a LuaError with a LuaValue as the message object,
	 * and level to draw line number information from.
	 * @param message_object message string or object to supply
	 */
	public LuaError(LuaValue message_object) {
		super( message_object.tojstring() );
		this.object = message_object;
		this.level = 1;
	}	


	/** 
	 * Get the cause, if any.
	 */
	public Throwable getCause() {
		return cause;
	}


}
