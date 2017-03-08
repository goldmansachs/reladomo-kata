package auditonlybank.domain;
import java.sql.Timestamp;
/********************************************************************************
* File        : $Source:  $
* Version     : $Revision:  $
* Date        : $Date:  $
* Modified by : $Author:  $
*******************************************************************************
*/
public class Customer extends CustomerAbstract
{
	public Customer(Timestamp processingDate)
	{
		super(processingDate);
		// You must not modify this constructor. Mithra calls this internally.
		// You can call this constructor. You can also add new constructors.
	}

	public Customer()
	{
		this(auditonlybank.util.TimestampProvider.getInfinityDate());
	}
}
