package auditonlybank.domain;
import com.gs.fw.finder.Operation;
import java.util.*;
/********************************************************************************
* File        : $Source:  $
* Version     : $Revision:  $
* Date        : $Date:  $
* Modified by : $Author:  $
*******************************************************************************
*/
public class CustomerList extends CustomerListAbstract
{
	public CustomerList()
	{
		super();
	}

	public CustomerList(int initialSize)
	{
		super(initialSize);
	}

	public CustomerList(Collection c)
	{
		super(c);
	}

	public CustomerList(Operation operation)
	{
		super(operation);
	}
}
