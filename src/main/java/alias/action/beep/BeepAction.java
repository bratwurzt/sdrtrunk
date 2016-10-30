package alias.action.beep;

import alias.Alias;
import alias.action.AliasActionType;
import alias.action.RecurringAction;
import message.Message;

public class BeepAction extends RecurringAction
{

  @Override
  public AliasActionType getType()
  {
    return AliasActionType.BEEP;
  }

  @Override
  public void performAction(Alias alias, Message message)
  {
    System.out.println("\007");
  }

  @Override
  public String toString()
  {
    return "Beep";
  }
}
