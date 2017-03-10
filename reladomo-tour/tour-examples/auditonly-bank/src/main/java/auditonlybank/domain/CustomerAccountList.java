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
public class CustomerAccountList extends CustomerAccountListAbstract
{
	public CustomerAccountList()
	{
		super();
	}

	public CustomerAccountList(int initialSize)
	{
		super(initialSize);
	}

	public CustomerAccountList(Collection c)
	{
		super(c);
	}

	public CustomerAccountList(Operation operation)
	{
		super(operation);
	}
}
