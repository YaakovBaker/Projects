public class DoubleCell implements Cell
{
	private double cellValue;
	public DoubleCell(double cellValue)
	{
		this.cellValue = cellValue;
	}

	@Override
	public double getNumericValue()
	{
		return this.cellValue;
	}

	@Override
	public String getStringValue()
	{
		return java.lang.Double.toString(this.cellValue);
	}
}
