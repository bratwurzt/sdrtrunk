package module.decode.p25.message.pdu.osp.voice;

import alias.AliasList;
import bits.BinaryMessage;
import module.decode.p25.message.IdentifierReceiver;
import module.decode.p25.message.pdu.UnitToUnitChannelGrantExtended;
import module.decode.p25.reference.DataUnitID;
import module.decode.p25.reference.Opcode;

public class UnitToUnitVoiceChannelGrantExtended
    extends UnitToUnitChannelGrantExtended
    implements IdentifierReceiver
{
  public UnitToUnitVoiceChannelGrantExtended(BinaryMessage message,
                                             DataUnitID duid, AliasList aliasList)
  {
    super(message, duid, aliasList);
  }

  @Override
  public String getEventType()
  {
    return Opcode.UNIT_TO_UNIT_VOICE_CHANNEL_GRANT.getDescription();
  }
}
