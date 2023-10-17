import {
  currentProjectIdKey,
  InvitationStatus,
  Participant,
  STEP,
  TypeSTep,
} from '@/constant/NGContant';
import {$count, $ok} from '@/utils/request/common/type';
import {Signatory} from '@/utils/request/interface/Project.interface';
import {TypeTemplate} from '@/utils/request/services/MyService';
import {ICountry} from '@components/ng-phone/type';
import {IRecipient} from '@pages/form/process-upload/type';
import {parsePhoneNumber} from 'react-phone-number-input';

// Common of Creation Signature Project Step 2

export const getCountryPhones = (
  approvals?: IRecipient[],
  signatories?: IRecipient[],
  recipients?: IRecipient[],
  viewers?: IRecipient[],
): ICountry[] => {
  if (
    signatories!.length +
      approvals!.length +
      recipients!.length +
      viewers!.length >
    0
  ) {
    return [
      ...signatories!.map(item => {
        return {
          callingCode:
            parsePhoneNumber(item.phone as string)?.countryCallingCode ?? '33',
          name: 'France',
          code: parsePhoneNumber(item.phone as string)?.country ?? 'FR',
        } as ICountry;
      }),
      ...approvals!.map(item => {
        return {
          callingCode:
            parsePhoneNumber(item.phone as string)?.countryCallingCode ?? '33',
          name: 'France',
          code: parsePhoneNumber(item.phone as string)?.country ?? 'FR',
        } as ICountry;
      }),
      ...viewers!.map(item => {
        return {
          callingCode:
            parsePhoneNumber(item.phone as string)?.countryCallingCode ?? '33',
          name: 'France',
          code: parsePhoneNumber(item.phone as string)?.country ?? 'FR',
        } as ICountry;
      }),
      ...recipients!.map(item => {
        return {
          callingCode:
            parsePhoneNumber(item.phone as string)?.countryCallingCode ?? '33',
          name: 'France',
          code: parsePhoneNumber(item.phone as string)?.country ?? 'FR',
        } as ICountry;
      }),
    ];
  } else {
    return [
      {
        callingCode: '33',
        name: 'France',
        code: 'FR',
      },
    ];
  }
};
export const handleSignatureStep = (participants: IRecipient[]): boolean => {
  const part = participants.find(participant => {
    return participant.role === Participant.Signatory;
  });
  return $ok(part);
};

/**
 * It used to validate base on a template (create project by a template )
 * */

export function validateProjectTemplate(
  template: TypeTemplate,
  input: IRecipient[],
  step: TypeSTep,
): boolean {
  if (step === STEP.STEP2) {
    const signatory = input.filter(item => item.role == Participant.Signatory);
    const approval = input.filter(item => item.role == Participant.Approval);

    return (
      signatory.length === template.signature &&
      approval.length === template.approval
    );
  }
  return true;
}

export const getSignatureProgress = (participants: Signatory[]): number => {
  let count = 0; // count signed actor
  const removeReceipt = participants.filter(
    item => ![Participant.Viewer].includes(item.role),
  );
  removeReceipt.forEach(p => {
    if (
      [
        InvitationStatus.SIGNED,
        InvitationStatus.APPROVED,
        InvitationStatus.RECEIVED,
      ].includes(p.documentStatus!)
    ) {
      count++;
    }
  });
  if (count == 0) {
    return 0;
  }
  return Number(parseFloat(String(count / $count(removeReceipt)!)).toFixed(2));
};

export const getTemporaryParticipants = (
  participants: (IRecipient & {fillForm?: boolean})[],
) => {
  const tempSignatories: (IRecipient & {fillForm?: boolean})[] = [];
  const tempApprovals: (IRecipient & {fillForm?: boolean})[] = [];
  const tempRecipients: (IRecipient & {fillForm?: boolean})[] = [];
  const tempViewers: (IRecipient & {fillForm?: boolean})[] = [];

  participants.forEach(
    ({
      role,
      phone,
      lastName,
      email,
      firstName,
      id,
      invitationStatus,
      sortOrder,
      fillForm,
    }) => {
      if (role === Participant.Approval) {
        tempApprovals.push({
          id,
          role,
          lastName: lastName?.trim(),
          firstName: firstName?.trim(),
          email: email?.trim(),
          projectId: localStorage.getItem(currentProjectIdKey)!,
          invitationStatus,
          sortOrder,
          phone,
          fillForm,
        });
      } else if (role === Participant.Viewer) {
        tempViewers.push({
          id,
          role,
          lastName: lastName?.trim(),
          firstName: firstName?.trim(),
          email: email?.trim(),
          projectId: localStorage.getItem(currentProjectIdKey)!,
          invitationStatus,
          sortOrder,
          phone,
          fillForm,
        });
      } else if (role === Participant.Signatory) {
        tempSignatories.push({
          id,
          role,
          lastName: lastName?.trim(),
          firstName: firstName?.trim(),
          email: email?.trim(),
          projectId: localStorage.getItem(currentProjectIdKey)!,
          invitationStatus,
          sortOrder,
          phone,
          fillForm,
        });
      } else if (role === Participant.Receipt) {
        tempRecipients.push({
          id,
          role,
          lastName: lastName?.trim(),
          firstName: firstName?.trim(),
          email: email?.trim(),
          projectId: localStorage.getItem(currentProjectIdKey)!,
          invitationStatus,
          sortOrder,
          phone,
          fillForm,
        });
      }
    },
  );
  return {
    tempSignatories,
    tempApprovals,
    tempRecipients,
    tempViewers,
  };
};
