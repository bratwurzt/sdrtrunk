package module.decode.p25.message.tsbk;

import alias.AliasList;
import bits.BinaryMessage;
import module.decode.p25.message.tsbk.motorola.*;
import module.decode.p25.message.tsbk.osp.control.*;
import module.decode.p25.message.tsbk.osp.data.*;
import module.decode.p25.message.tsbk.osp.voice.*;
import module.decode.p25.reference.DataUnitID;
import module.decode.p25.reference.Opcode;
import module.decode.p25.reference.Vendor;

public class TSBKMessageFactory
{
  public static TSBKMessage getMessage(BinaryMessage message,
                                       DataUnitID duid,
                                       AliasList aliasList)
  {
    Vendor vendor = Vendor.fromValue(
        message.getInt(TSBKMessage.VENDOR_ID));

    switch (vendor)
    {
      case STANDARD:
        Opcode opcode =
            Opcode.fromValue(message.getInt(TSBKMessage.OPCODE));

        switch (opcode)
        {
          case ACKNOWLEDGE_RESPONSE:
            return new AcknowledgeResponse(message, duid, aliasList);
          case ADJACENT_STATUS_BROADCAST:
            return new AdjacentStatusBroadcast(message, duid, aliasList);
          case AUTHENTICATION_COMMAND:
            return new AuthenticationCommand(message, duid, aliasList);
          case CALL_ALERT:
            return new CallAlert(message, duid, aliasList);
          case DENY_RESPONSE:
            return new DenyResponse(message, duid, aliasList);
          case EXTENDED_FUNCTION_COMMAND:
            return new ExtendedFunctionCommand(message, duid, aliasList);
          case GROUP_AFFILIATION_QUERY:
            return new GroupAffiliationQuery(message, duid, aliasList);
          case GROUP_AFFILIATION_RESPONSE:
            return new GroupAffiliationResponse(message, duid, aliasList);
          case GROUP_DATA_CHANNEL_ANNOUNCEMENT:
            return new GroupDataChannelAnnouncement(message, duid, aliasList);
          case GROUP_DATA_CHANNEL_ANNOUNCEMENT_EXPLICIT:
            return new GroupDataChannelAnnouncementExplicit(message, duid, aliasList);
          case GROUP_DATA_CHANNEL_GRANT:
            return new GroupDataChannelGrant(message, duid, aliasList);
          case GROUP_VOICE_CHANNEL_GRANT:
            return new GroupVoiceChannelGrant(message, duid, aliasList);
          case GROUP_VOICE_CHANNEL_GRANT_UPDATE:
            return new GroupVoiceChannelGrantUpdate(message, duid, aliasList);
          case GROUP_VOICE_CHANNEL_GRANT_UPDATE_EXPLICIT:
            return new GroupVoiceChannelGrantUpdateExplicit(message, duid, aliasList);
          case IDENTIFIER_UPDATE_NON_VUHF:
            return new IdentifierUpdateNonVUHF(message, duid, aliasList);
          case IDENTIFIER_UPDATE_TDMA:
            return new IdentifierUpdateTDMA(message, duid, aliasList);
          case IDENTIFIER_UPDATE_VHF_UHF_BANDS:
            return new IdentifierUpdateVUHF(message, duid, aliasList);
          case INDIVIDUAL_DATA_CHANNEL_GRANT:
            return new IndividualDataChannelGrant(message, duid, aliasList);
          case LOCATION_REGISTRATION_RESPONSE:
            return new LocationRegistrationResponse(message, duid, aliasList);
          case MESSAGE_UPDATE:
            return new MessageUpdate(message, duid, aliasList);
          case NETWORK_STATUS_BROADCAST:
            return new NetworkStatusBroadcast(message, duid, aliasList);
          case QUEUED_RESPONSE:
            return new QueuedResponse(message, duid, aliasList);
          case PROTECTION_PARAMETER_UPDATE:
            return new ProtectionParameterUpdate(message, duid, aliasList);
          case RADIO_UNIT_MONITOR_COMMAND:
            return new RadioUnitMonitorCommand(message, duid, aliasList);
          case RFSS_STATUS_BROADCAST:
            return new RFSSStatusBroadcast(message, duid, aliasList);
          case ROAMING_ADDRESS_COMMAND:
            return new RoamingAddressCommand(message, duid, aliasList);
          case SECONDARY_CONTROL_CHANNEL_BROADCAST:
            return new SecondaryControlChannelBroadcast(message, duid, aliasList);
          case SECONDARY_CONTROL_CHANNEL_BROADCAST_EXPLICIT:
            return new SecondaryControlChannelBroadcastExplicit(message, duid, aliasList);
          case SNDCP_DATA_CHANNEL_GRANT:
            return new SNDCPDataChannelGrant(message, duid, aliasList);
          case SNDCP_DATA_CHANNEL_ANNOUNCEMENT_EXPLICIT:
            return new SNDCPDataChannelAnnouncementExplicit(message, duid, aliasList);
          case SNDCP_DATA_PAGE_REQUEST:
            return new SNDCPDataPageRequest(message, duid, aliasList);
          case STATUS_QUERY:
            return new StatusQuery(message, duid, aliasList);
          case STATUS_UPDATE:
            return new StatusUpdate(message, duid, aliasList);
          case TDMA_SYNC_BROADCAST:
            return new SyncBroadcast(message, duid, aliasList);
          case SYSTEM_SERVICE_BROADCAST:
            return new SystemServiceBroadcast(message, duid, aliasList);
          case TELEPHONE_INTERCONNECT_ANSWER_REQUEST:
            return new TelephoneInterconnectAnswerRequest(message, duid, aliasList);
          case TELEPHONE_INTERCONNECT_VOICE_CHANNEL_GRANT:
            return new TelephoneInterconnectVoiceChannelGrant(message, duid, aliasList);
          case TIME_DATE_ANNOUNCEMENT:
            return new TimeAndDateAnnouncement(message, duid, aliasList);
          case UNIT_DEREGISTRATION_ACKNOWLEDGE:
            return new UnitDeregistrationAcknowledge(message, duid, aliasList);
          case UNIT_REGISTRATION_COMMAND:
            return new UnitRegistrationCommand(message, duid, aliasList);
          case UNIT_REGISTRATION_RESPONSE:
            return new UnitRegistrationResponse(message, duid, aliasList);
          case UNIT_TO_UNIT_ANSWER_REQUEST:
            return new UnitToUnitAnswerRequest(message, duid, aliasList);
          case UNIT_TO_UNIT_VOICE_CHANNEL_GRANT:
            return new UnitToUnitVoiceChannelGrant(message, duid, aliasList);
          case UNIT_TO_UNIT_VOICE_CHANNEL_GRANT_UPDATE:
            return new UnitToUnitVoiceChannelGrantUpdate(message, duid, aliasList);
          default:
            return new TSBKMessage(message, duid, aliasList);
        }

      case MOTOROLA:
        MotorolaOpcode motorolaOpcode = MotorolaOpcode.
            fromValue(message.getInt(TSBKMessage.OPCODE));

        switch (motorolaOpcode)
        {
          case CCH_PLANNED_SHUTDOWN:
            return new PlannedControlChannnelShutdown(message, duid, aliasList);
          case CONTROL_CHANNEL_ID:
            return new ControlChannelBaseStationIdentification(message, duid, aliasList);
          case PATCH_GROUP_ADD:
            return new PatchGroupAdd(message, duid, aliasList);
          case PATCH_GROUP_DELETE:
            return new PatchGroupDelete(message, duid, aliasList);
          case PATCH_GROUP_CHANNEL_GRANT:
            return new PatchGroupVoiceChannelGrant(message, duid, aliasList);
          case PATCH_GROUP_CHANNEL_GRANT_UPDATE:
            return new PatchGroupVoiceChannelGrantUpdate(message, duid, aliasList);
          case SYSTEM_LOAD:
            return new SystemLoading(message, duid, aliasList);
          case TRAFFIC_CHANNEL_ID:
            return new TrafficChannelBaseStationIdentification(message, duid, aliasList);
          default:
        }

        return new MotorolaTSBKMessage(message, duid, aliasList);
      default:
        return new TSBKMessage(message, duid, aliasList);
    }
  }
}