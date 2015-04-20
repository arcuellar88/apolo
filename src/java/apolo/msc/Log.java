package apolo.msc;

import java.util.Date;

public class Log {

	private final static String LINE="--"+new Date()+": ";
	
	public final static void print(String message)
	{
		System.out.print(message);
	}
	public final static void println(String message)
	{
		System.out.println(LINE+message);
	}
	
}

