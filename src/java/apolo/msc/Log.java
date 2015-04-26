package apolo.msc;

import java.util.Date;

public class Log {

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	private final static String LINE="--"+new Date()+": ";
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	/**
	 * Print message
	 * @param message
	 */
	public final static void print(String message)
	{
		System.out.print(message);
	}
	/**
	 * Print message and go to next line
	 * @param message
	 */
	public final static void println(String message)
	{
		System.out.println(LINE+message);
	}
	
}

