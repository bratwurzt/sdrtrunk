package module.decode.p25.message.tsbk.osp.control;

import alias.AliasList;
import bits.BinaryMessage;
import module.decode.p25.message.tsbk.TSBKMessage;
import module.decode.p25.reference.DataUnitID;
import module.decode.p25.reference.Opcode;

public class CallAlert extends TSBKMessage
{
  public static final int[] TARGET_ADDRESS = {96, 97, 98, 99, 100, 101, 102, 103,
      104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119};
  public static final int[] SOURCE_ID = {120, 121, 122, 123, 124, 125, 126,
      127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143};

  public CallAlert(BinaryMessage message,
                   DataUnitID duid,
                   AliasList aliasList)
  {
    super(message, duid, aliasList);
  }

  @Override
  public String getEventType()
  {
    return Opcode.CALL_ALERT.getDescription();
  }

  public String getMessage()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getMessageStub());

    sb.append(" SRC ID: " + getSourceID());
    sb.append(" TGT ADDR: " + getTargetAddress());

    return sb.toString();
  }

  public String getSourceID()
  {
    return mMessage.getHex(SOURCE_ID, 6);
  }

  @Override
  public String getFromID()
  {
    return getSourceID();
  }

  public String getTargetAddress()
  {
    return mMessage.getHex(TARGET_ADDRESS, 6);
  }

  @Override
  public String getToID()
  {
    return getTargetAddress();
  }
}
