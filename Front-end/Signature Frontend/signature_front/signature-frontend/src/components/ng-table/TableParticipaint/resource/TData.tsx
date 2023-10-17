import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';
import {Localization} from '@/i18n/lan';
import i18next from 'i18next';

export function repairData_Participant(data: any) {
  const TableRows_participant: NGTableParticipantTypeCreateData[] = [];
  data?.length > 0 &&
    data.map((item: any) => {
      TableRows_participant.push({
        order: item.sortOrder,
        nom: item.firstName + ' ' + item.lastName,
        phone: item.phone,
        email: item.email,
        status: i18next.t(Localization('documentStatus', item.documentStatus)),
        invitation: i18next.t(
          Localization('invitationStatus', item.invitationStatus),
        ),
        role: i18next.t(Localization('table', item.role)),
        action: 1,
        comment: item.signatories.map(
          (item: {comment: string}) => item.comment,
        ),
      });
    });
  return TableRows_participant;
}
