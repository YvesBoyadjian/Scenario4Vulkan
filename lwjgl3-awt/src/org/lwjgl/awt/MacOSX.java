package org.lwjgl.awt;

import org.lwjgl.system.JNI;
import org.lwjgl.system.macosx.ObjCRuntime;

/**
 * Utility class to provide MacOSX-only stuff.
 *
 * @author SWinxy
 */
public class MacOSX {

	/**
	 * ObjCRuntime does not expose objc_msgSend, so we have to get it ourselves
	 * Pointer to a method that sends a message to an instance of a class
	 * Apple spec: macOS 10.0 (OSX 10; 2001) or higher
	 */
	private static final long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");

	// No.
	private MacOSX() {}

	/**
	 * Flushes any extant implicit transaction.
	 * Equivalent of <code>[CATransaction flush];</code> in Objective-C.
	 * <p>
	 * From Apple's developer documentation:
	 *
	 * <blockquote>
	 * Delays the commit until any nested explicit transactions have completed.
	 * <p>
	 * Flush is typically called automatically at the end of the current runloop,
	 * regardless of the runloop mode. If your application does not have a runloop,
	 * you must call this method explicitly.
	 * <p>
	 * However, you should attempt to avoid calling flush explicitly.
	 * By allowing flush to execute during the runloop your application
	 * will achieve better performance, atomic screen updates will be preserved,
	 * and transactions and animations that work from transaction to transaction
	 * will continue to function.
	 * </blockquote>
	 */
	public static void caFlush() {
		// Pointer to the CATransaction class definition
		// Apple spec: macOS 10.5 (OSX Leopard; 2007) or higher
		long CATransaction = ObjCRuntime.objc_getClass("CATransaction");

		// Pointer to the flush method
		// Apple spec: macOS 10.5 (OSX Leopard; 2007) or higher
		long flush = ObjCRuntime.sel_getUid("flush");

		JNI.invokePPP(CATransaction, flush, objc_msgSend);
	}
}
