package module.decode.p25.message.tsbk.osp.voice;

import alias.AliasList;
import bits.BinaryMessage;
import module.decode.p25.message.tsbk.GroupMultiChannelGrant;
import module.decode.p25.reference.DataUnitID;
import module.decode.p25.reference.Opcode;

public class GroupVoiceChannelGrantUpdate extends GroupMultiChannelGrant
{
  public GroupVoiceChannelGrantUpdate(BinaryMessage message,
                                      DataUnitID duid,
                                      AliasList aliasList)
  {
    super(message, duid, aliasList);
  }

  @Override
  public String getEventType()
  {
    return Opcode.GROUP_VOICE_CHANNEL_GRANT_UPDATE.getDescription();
  }
}
