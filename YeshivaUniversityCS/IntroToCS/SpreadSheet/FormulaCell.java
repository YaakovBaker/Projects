public class FormulaCell implements Cell
{
	private String formula;
	private CellProvider cellSpreadSheet;
	public FormulaCell(String formula, CellProvider cellSpreadSheet)
	{
		this.formula = formula;
		this.cellSpreadSheet = cellSpreadSheet;
		
	}

	@Override
	public String getStringValue()
	{
		return this.formula;
	}

	@Override
	public double getNumericValue()
	{
		String operand1 = "";
		String operand2 = "";
	 	for ( int i = 0; i < formula.length(); i++)
	 	{
	 		if ( (formula.charAt(i) == '+') ||
 			 	 (formula.charAt(i) == '-') ||
 			 	 (formula.charAt(i) == '*') ||
 			 	 (formula.charAt(i) == '/') ||
 			 	 (formula.charAt(i) == '%') )
 			{
 				int operatorIndex = i;
 				operand1 = formula.substring(0, operatorIndex - 1);
 				operand2 = formula.substring(operatorIndex + 2, formula.length());
 			}
	 	}
 		char column1 = operand1.charAt(0);
 		int row1 = Integer.parseInt(operand1.substring(1, operand1.length()));
 		char column2 = operand2.charAt(0);
 		int row2 = Integer.parseInt(operand2.substring(1, operand2.length()));

		Cell cellObject1 = cellSpreadSheet.getCell(column1, row1);//this or CellProvider.getCell()
		Cell cellObject2 = cellSpreadSheet.getCell(column2, row2);
		double valueOfCellObject1 = evaluateCell(cellObject1);
		double valueOfCellObject2 = evaluateCell(cellObject2);
		return performOperation(this.formula, valueOfCellObject1, valueOfCellObject2);
	}

	private double evaluateCell( Cell cell)
	{
		if ( cell instanceof DoubleCell )
		{
			DoubleCell dubcell = (DoubleCell)cell;
			return dubcell.getNumericValue();

		} else if (cell instanceof FormulaCell)
		{
			FormulaCell formulaCell = (FormulaCell)cell;
			return formulaCell.getNumericValue();
		} else
		{
			return 0.0;
		}
	}

	private double performOperation(String formula, double value1, double value2)
	{
		double calculation = 0.0;
		for ( int i = 0; i < formula.length(); i++)
	 	{
	 		if ( formula.charAt(i) == '+')
	 		{
	 			calculation = value1 + value2;
	 			return calculation;
	 		}else if ( formula.charAt(i) == '-')
	 		{
	 			calculation = value1 - value2;
	 			return calculation;
	 		}else if ( formula.charAt(i) == '*')
	 		{
	 			calculation = value1 * value2;
	 			return calculation;
	 		}else if ( formula.charAt(i) == '/')
	 		{
	 			calculation = value1 / value2;
	 			return calculation;
	 		}else if ( formula.charAt(i) == '%')
	 		{
	 			calculation = value1 % value2;
	 			return calculation;
	 		}
	 	}
	 	return calculation;
	}

}