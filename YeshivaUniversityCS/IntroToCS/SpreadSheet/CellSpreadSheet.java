
public class  CellSpreadSheet implements CellProvider
{
	private int rows;
	private int columns;
	private Cell[][] table;

 /**
 * Initializes the spreadsheet to be a given height and width
 * @param rows
 * @param columns
 */
 public CellSpreadSheet(int columns, int rows)
 {
 	this.rows = rows;
 	this.columns = columns;
 	this.table = new Cell[rows][columns];
 }

 /**
 * @param showFormulas if true, show the raw cell formulas. If false, show the value generated
by calculating the formulas.
 * @return a String, comma separated values representation of the entire spreadsheet. Each line
ends with "\n"
 */
 public String getSpreadSheetAsCSV(boolean showFormulas)
 {
	char lastColumn = (char)(this.columns + 64);
	String print = "";
		
	for( char c = 'A'; c <= lastColumn; c++ )
	{
		if ( c == lastColumn)
		{
			print += c + "\n";
		} else 
		{
			print += c + ",";
		}	
	}
	if ( showFormulas ) 
	{
		for ( int rowSpot = 0; rowSpot < this.rows; rowSpot++ )
		{
			for( int colSpot = 0; colSpot < this.table[rowSpot].length; colSpot++ )
			{
				if ( colSpot == this.columns - 1 ) 
				{
					if ( this.table[rowSpot][colSpot] == null )
					{
						print += 0.0 + "\n";
					}else if ( this.table[rowSpot][colSpot] instanceof DoubleCell )
					{
						print += this.table[rowSpot][colSpot].getStringValue() + "\n";
					} else if ( this.table[rowSpot][colSpot] instanceof FormulaCell )
					{
						print += this.table[rowSpot][colSpot].getStringValue() + "\n";
					}
				} else
				{
					if ( this.table[rowSpot][colSpot] == null )
					{
						print += 0.0 + ",";
					}else if ( this.table[rowSpot][colSpot] instanceof DoubleCell )
					{
						print += this.table[rowSpot][colSpot].getStringValue() + ",";
					}else if ( this.table[rowSpot][colSpot] instanceof FormulaCell )
					{
						print += this.table[rowSpot][colSpot].getStringValue() + ",";
					}
				}
			}
		}
		return print;
	} else
	{
		for ( int rowSpot = 0; rowSpot < this.rows; rowSpot++ )
		{
			for( int colSpot = 0; colSpot < this.table[rowSpot].length; colSpot++ )
			{
				if ( colSpot == this.columns -1 )
				{
					if ( this.table[rowSpot][colSpot] == null )
					{
						print += 0.0 + "\n";
					}else if ( this.table[rowSpot][colSpot] instanceof FormulaCell )
					{
						print += evaluateFormula((char)(colSpot + 65), rowSpot + 1) + "\n";
					}else if ( this.table[rowSpot][colSpot] instanceof DoubleCell )
					{
						print += table[rowSpot][colSpot].getStringValue() + "\n";
					}
				} else
				{
					if ( this.table[rowSpot][colSpot] == null )
					{
						print += 0.0 + ",";
					}else if ( this.table[rowSpot][colSpot] instanceof FormulaCell )
					{
						print += evaluateFormula((char)(colSpot + 65), rowSpot + 1) + ",";
					}else if ( this.table[rowSpot][colSpot] instanceof DoubleCell )
					{
						print += table[rowSpot][colSpot].getStringValue() + ",";
					}
				}
			}
		}
		return print;
	}
 }

 /**
 *
 * Sets a cell to given value, where the value is either a double or a cell formula.
 * Formulas are written as two cell references separated by a basic math operation. There must
be a space
 * between each of the 3 elements, i.e. a space between the first cell reference and operation,
and then another
 * space between the operation and the second cell reference. Example formulas:
 * Addition: A1 + C3
 * Subtraction: D7 - F6
 * Multiplication: F15 * Z9
 * Division: B6 / C2
 *
 * If the column or row is beyond the current bounds of the 2D array storing the spreadsheet,
the size of the 2D array must be expanded as needed to store the value at column, row.
 *
 * @param column column whose value to set, between 'A' and 'Z'
 * @param row row whose value to set. An integer >= 1
 * @param value must be either a string representation of a double or a cell formula
 */
 public void setValue(char column, int row, String value)
 {
	int colIndex = (int) (column) - 65;
	int rowIndex = row - 1;
	
	if ( ((colIndex + 1) > this.columns) && (row > this.rows) )
	{
		expandColumnRange(column);
		expandRowRange(row);
		if ( isValidDouble(value) )
		{
			this.table[rowIndex][colIndex] = new DoubleCell(Double.parseDouble(value));
		}else
		{
			this.table[rowIndex][colIndex] = new FormulaCell(value, this);
		}
	}else if ( (colIndex + 1) > this.columns )
	{
		expandColumnRange(column);
		if ( isValidDouble(value) )
		{
			this.table[rowIndex][colIndex] = new DoubleCell(Double.parseDouble(value));
		}else
		{
			this.table[rowIndex][colIndex] = new FormulaCell(value, this);
		}
	}else if ( row > this.rows )
	{
		expandRowRange(row);
		if ( isValidDouble(value) )
		{
			this.table[rowIndex][colIndex] = new DoubleCell(Double.parseDouble(value));
		}else
		{
			this.table[rowIndex][colIndex] = new FormulaCell(value, this);
		}
	}else
	{
		if ( isValidDouble(value) )
		{
			this.table[rowIndex][colIndex] = new DoubleCell(Double.parseDouble(value));
		}else
		{
			this.table[rowIndex][colIndex] = new FormulaCell(value, this);
		}
	}
 }

 /**
 * Returns a complete copy of the spreadsheet data. Since it is a COPY, any edits to the copy
will have no effect on the spreadsheet itself.
 * @return a complete copy of the spreadsheet data
 */
 public Cell[][] getCopyOfData()
 {
 	Cell[][] copyOfTable = new Cell[this.columns][this.rows];
 	for( int rowIndex = 0; rowIndex < this.table.length; rowIndex++ ) 
	{
		for( int colIndex = 0; colIndex < this.table[rowIndex].length; colIndex++ )
		{
			copyOfTable[colIndex][rowIndex] = this.table[rowIndex][colIndex];
		}
	}
 	return copyOfTable;
 }

 /**
 * Expand the spreadhseet to extend it to the given column.
 * @param column
 */
 public void expandColumnRange(char column)
{
 	int modifiedColSize = (int) (column) - 64;
 	this.columns = modifiedColSize;

 	Cell[][] oldTable = this.table;

 	this.table = new Cell[this.rows][this.columns];

 	for( int rowIndex = 0; rowIndex < oldTable.length; rowIndex++)
	{
		for( int colIndex = 0; colIndex < oldTable[rowIndex].length; colIndex++ )
		{
			if ( oldTable[rowIndex][colIndex] != null )
			{
 				this.table[rowIndex][colIndex] = oldTable[rowIndex][colIndex];
 			}
		}
	}
}

 /**
 * Expand the spreadhseet to extend it to the given row.
 * @param row
 */
private void expandRowRange(int row)
{
	this.rows = row;
 	Cell[][] oldTable = this.table;
 	this.table = new Cell[this.rows][this.columns];

 	for( int rowIndex = 0; rowIndex < oldTable.length; rowIndex++)
	{
		for( int colIndex = 0; colIndex < oldTable[rowIndex].length; colIndex++ )
		{
			if ( oldTable[rowIndex][colIndex] != null )
			{
 				this.table[rowIndex][colIndex] = oldTable[rowIndex][colIndex];
 			}
		}
	}
}

/**
* Returns a copy of a given column through a given row.
* If throughRow is larger than the current height of the column, all the values in the rows past
the current column height will be null.
* @param c the column to get, between 'A' and 'Z'
* @param throughRow the row to get a copy through.
* @return
*/
public Cell[] getCopyOfColumnThroughRow(char c, int throughRow)
{
	int colIndex = (int)c - 65;
	Cell[] copyOfColThroughRow = new Cell[throughRow];

	for( int rowIndex = 0; rowIndex < throughRow; rowIndex++)
	{
		if ( rowIndex >= this.rows )
		{
			copyOfColThroughRow[rowIndex] = null;
		}else if ( this.table[rowIndex][colIndex] == null )
		{
			copyOfColThroughRow[rowIndex] = new DoubleCell(0.0);
		}else
		{
			copyOfColThroughRow[rowIndex] = this.table[rowIndex][colIndex];
		}
	}
	return copyOfColThroughRow;
}

 /**
 *
 * @param column (first column is A)
 * @param row (first row is 1)
 * @return value stored in that cell. If it holds a double, return the double. If it holds a
formula, return the result of calculating the formula stored in the cell. If the cell is empty, it
returns 0.
 */
 public double getValue(char column,int row)
 {
 	Cell cell = this.getCell(column, row);
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

 /**
 *
 * @param cell the cell whose value should be returned. E.g. "A1" or "F9", etc.
 * @return
 */
 public double getValue(String cell)
 {
 	char column = cell.charAt(0);
 	int row = Integer.parseInt(cell.substring(1, cell.length()));
 	Cell theCell = this.getCell(column, row);
 	if ( theCell instanceof DoubleCell )
 	{
 		DoubleCell dubCell = (DoubleCell)theCell;
 		return dubCell.getNumericValue();
 	}else if ( theCell instanceof FormulaCell )
 	{
 		FormulaCell formulaCell = (FormulaCell)theCell;
 		return formulaCell.getNumericValue();
 	}else
 	{
 		return 0.0;
 	}
 }

 /**
 * Evaluate the formula held in the given cell
 * @param column 'A' through 'Z'
 * @param row row number, 1 or greater
 * @return
 */
 public double evaluateFormula(char column, int row)
 {
 	Cell cell = this.getCell(column, row);
	if (cell instanceof FormulaCell)
	{
		FormulaCell formulaCell = (FormulaCell)cell;
		return formulaCell.getNumericValue();
	} else if ( cell instanceof DoubleCell )
	{
		DoubleCell dubcell = (DoubleCell)cell;
		return dubcell.getNumericValue();
	} else
	{
		return 0.0;
	}
 }

 @Override
 public Cell getCell(char column, int row)
 {
 	int col = (int)(column) - 65;
 	int rowM1 = row - 1;
 	if ( (col >= this.columns) || (row > this.rows) )
 	{
 		return null;
 	}if ( this.table[rowM1][col] instanceof Cell )
 	{
 		return this.table[rowM1][col];
 	} else
 	{
 		return null;
 	}
 }

 /**
 * checks if the string represents a double
 * @param arg
 * @return true if it's a valid double, false if not
 */
 private boolean isValidDouble(String arg)
 {
 	try{
 		Double.parseDouble(arg);
 		return true;
 	}catch(NumberFormatException e){
 		return false;
 	}
 }
}