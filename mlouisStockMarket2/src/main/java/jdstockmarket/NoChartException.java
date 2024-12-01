package jdstockmarket;

/*
 *   when an chart cannot be created, instantiate a
 *     new Exception object with an error message
 */
public class NoChartException  extends Exception
{
	public NoChartException()
	{
		super("Chart Cannot Be Created");
	}
}


