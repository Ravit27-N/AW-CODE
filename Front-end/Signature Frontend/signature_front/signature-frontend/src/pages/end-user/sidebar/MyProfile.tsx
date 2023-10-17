import {Center, VStack} from '@/theme';
import NGText from '@components/ng-text/NGText';
import {NGDocStatus} from '@components/ng-switch-case-status/NGDocStatus';
import {NGProjectStatus} from '@components/ng-switch-case-status/NGProjectStatus';
import {FirstName, LastName} from '@/utils/request/interface/Project.interface';
import {InvitationStatus, Participant} from '@/constant/NGContant';

const MyProfile = () => {
  return (
    <Center sx={{height: '100vh', width: '100%'}}>
      <NGText text={'My Profile'} />
      <VStack spacing={2} sx={{display: 'none'}}>
        <NGText text={'Doc status'} myStyle={{color: 'Danger.main'}} />
        <NGDocStatus StatusName={'SIGNED'} />
        <NGDocStatus StatusName={'IN_PROGRESS'} />
        <NGDocStatus StatusName={'REFUSED'} />
        <NGDocStatus StatusName={'APPROVED'} />
        <NGDocStatus StatusName={'RECEIVED'} />
        <NGText text={'Project status'} myStyle={{color: 'Danger.main'}} />
        <NGProjectStatus StatusName={'COMPLETED'} />
        <NGProjectStatus StatusName={'DRAFT'} />
        <NGProjectStatus StatusName={'EXPIRED'} />
        <NGProjectStatus StatusName={'REFUSED'} />
        <NGProjectStatus
          StatusName={'IN_PROGRESS'}
          signatories={[
            {
              id: 23,
              firstName: FirstName.Weak1,
              lastName: LastName.Jonh,
              role: Participant.Signatory,
              email: 'sila@gmail.com',
              phone: '347849',
              invitationStatus: InvitationStatus.IN_PROGRESS,
              documentStatus: null,
              sortOrder: 12,
            },
          ]}
        />
      </VStack>
    </Center>
  );
};

export default MyProfile;
