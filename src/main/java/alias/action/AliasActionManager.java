package alias.action;

import alias.Alias;
import controller.ThreadPoolManager;
import message.Message;
import sample.Listener;

import java.util.List;

/**
 * Manages all alias action events.  Each received message is interrogated for
 * any alias entries and then each alias is interrogated for any alias actions.
 * <p>
 * Each alias action is executed and provided a copy of the source message so
 * that the contents of the message can be used as part of the action.
 */
public class AliasActionManager implements Listener<Message>
{
  private ThreadPoolManager mThreadPoolManager;

  public AliasActionManager(ThreadPoolManager threadPoolManager)
  {
    mThreadPoolManager = threadPoolManager;
  }

  @Override
  public void receive(Message message)
  {
    if (message.isValid())
    {
      List<Alias> aliases = message.getAliases();

      if (aliases != null)
      {
        for (Alias alias : aliases)
        {
          if (alias.hasActions())
          {
            List<AliasAction> actions = alias.getAction();

            for (AliasAction action : actions)
            {
              /* Provide access to the thread pool manager in case the
							 * action needs to setup a timer, and provide the original
							 * message to be used as part of the action (e.g. sending
							 * the message as a text message to a cell phone */
              action.execute(mThreadPoolManager, alias, message);
            }
          }
        }
      }
    }
  }
}
