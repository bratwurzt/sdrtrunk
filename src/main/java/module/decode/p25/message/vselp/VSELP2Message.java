package module.decode.p25.message.vselp;

import alias.AliasList;
import bits.BinaryMessage;
import module.decode.p25.message.P25Message;
import module.decode.p25.reference.DataUnitID;

public class VSELP2Message extends P25Message
{
  /**
   * Motorola VSELP audio message 1.
   *
   * @param message
   * @param duid
   * @param aliasList
   */
  public VSELP2Message(BinaryMessage message, DataUnitID duid, AliasList aliasList)
  {
    super(message, duid, aliasList);
  }

  @Override
  public String getMessage()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getMessageStub());
    sb.append(" ");
    sb.append(mMessage.toString());

    return sb.toString();
  }

  public String getMessageStub()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("VSELP 2 VOICE FRAME");

    return sb.toString();
  }
}
