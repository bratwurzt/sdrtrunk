package module.decode.p25.message.filter;

import filter.Filter;
import filter.FilterElement;
import message.Message;
import module.decode.p25.message.tdu.lc.TDULinkControlMessage;

import java.util.Collections;
import java.util.List;

public class TDULCMessageFilter extends Filter<Message>
{
  public TDULCMessageFilter()
  {
    super("TDU Terminator Data Unit with Link Control");
  }

  @Override
  public boolean passes(Message message)
  {
    return mEnabled && canProcess(message);
  }

  @Override
  public boolean canProcess(Message message)
  {
    return message instanceof TDULinkControlMessage;
  }

  @Override
  public List<FilterElement<?>> getFilterElements()
  {
    return Collections.EMPTY_LIST;
  }
}
