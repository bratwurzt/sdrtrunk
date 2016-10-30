package module.decode.p25.message.tdu.lc;

import module.decode.p25.reference.LinkControlOpcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUpdate extends TDULinkControlMessage
{
  private final static Logger mLog =
      LoggerFactory.getLogger(MessageUpdate.class);
  public static final int[] SHORT_DATA_MESSAGE = {72, 73, 74, 75, 88, 89, 90, 91, 92,
      93, 94, 95, 96, 97, 98, 99};
  public static final int[] TARGET_ADDRESS = {112, 113, 114, 115, 116, 117, 118,
      119, 120, 121, 122, 123, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147};
  public static final int[] SOURCE_ADDRESS = {160, 161, 162, 163, 164, 165, 166,
      167, 168, 169, 170, 171, 184, 185, 186, 187, 188, 189, 190, 190, 192, 193, 194, 195};

  public MessageUpdate(TDULinkControlMessage source)
  {
    super(source);
  }

  @Override
  public String getEventType()
  {
    return LinkControlOpcode.MESSAGE_UPDATE.getDescription();
  }

  public String getMessage()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getMessageStub());

    sb.append(" MSG:" + getShortDataMessage());
    sb.append(" SRC ADDR: " + getSourceAddress());
    sb.append(" TGT ADDR: " + getTargetAddress());

    return sb.toString();
  }

  public String getShortDataMessage()
  {
    return mMessage.getHex(SHORT_DATA_MESSAGE, 4);
  }

  public String getTargetAddress()
  {
    return mMessage.getHex(TARGET_ADDRESS, 6);
  }

  public String getSourceAddress()
  {
    return mMessage.getHex(SOURCE_ADDRESS, 6);
  }
}