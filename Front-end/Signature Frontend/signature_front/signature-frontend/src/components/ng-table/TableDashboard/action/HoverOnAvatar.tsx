import {Center} from '@/theme';
import Stack from '@mui/material/Stack';
import {Box, SxProps} from '@mui/system';
import Avatar from '@mui/material/Avatar';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';
import NGText from '@components/ng-text/NGText';
import {pixelToRem} from '@/utils/common/pxToRem';
import {InvitationStatus, Participant} from '@/constant/NGContant';
import {NGCorrectProject} from '@assets/iconExport/Allicon';
import {Signatory} from '@/utils/request/interface/Project.interface';
import {bg, textColor} from '@/components/ng-group-avatar/NGGroupAvatar';

function HoverOnAvtar({data, sx}: {data: Signatory[]; sx?: SxProps}) {
  const {t} = useTranslation();
  return (
    <Box width={'100%'} height={'auto'} borderRadius={2} sx={{...sx}}>
      <Stack spacing={0.5}>
        {data.map((item, index: number) => (
          <NGText
            key={item.id}
            myStyle={{
              fontSize: pixelToRem(12),
              fontWeight: 500,
              lineHeight: pixelToRem(16),
              mx: pixelToRem(6),
            }}
            text={item.firstName + ' ' + item.lastName}
            iconStart={
              [
                InvitationStatus.SIGNED,
                InvitationStatus.APPROVED,
                InvitationStatus.RECEIVED,
              ].indexOf(item.documentStatus!) > -1 ? (
                <Center sx={{width: '24px', height: '24px'}}>
                  <NGCorrectProject
                    sx={{
                      color: 'white',
                      bgcolor: 'green',
                      borderRadius: '50%',
                      p: pixelToRem(4),

                      fontSize: pixelToRem(18),
                    }}
                  />
                </Center>
              ) : (
                <Avatar
                  sx={{
                    bgcolor: bg[index],
                    width: pixelToRem(24),
                    height: pixelToRem(24),
                    color: 'green',
                  }}>
                  <NGText
                    text={
                      item.firstName?.slice(0, 1).toUpperCase() +
                      item.lastName?.slice(0, 1).toUpperCase()
                    }
                    myStyle={{
                      color: textColor[index],
                      fontWeight: 600,
                      fontSize: pixelToRem(9),
                    }}
                  />
                </Avatar>
              )
            }
            iconEnd={
              <NGText
                text={t(
                  Localization(
                    'table',
                    displayParticipantStatus(item.role, item.documentStatus!),
                  ),
                )}
                myStyle={{
                  color: 'grey',
                  fontSize: pixelToRem(12),
                  fontWeight: 300,
                  lineHeight: pixelToRem(16),
                }}
              />
            }
            styleTextHaveIcon={{
              direction: 'row',
              justifyContent: 'flex-start',
              spacing: 2,
              textTransform: 'capitalize',
            }}
          />
        ))}
      </Stack>
    </Box>
  );
}

export default HoverOnAvtar;

type StatusParticipent =
  | 'approved'
  | 'did-not-approve'
  | 'received'
  | 'did-not-recipient'
  | 'signed'
  | 'did-not-sign';
function displayParticipantStatus(
  role: Participant,
  documentStatus: InvitationStatus,
): StatusParticipent {
  switch (role) {
    case Participant.Approval:
      return documentStatus === InvitationStatus.APPROVED
        ? 'approved'
        : 'did-not-approve';

    case Participant.Receipt:
      return documentStatus === InvitationStatus.RECEIVED
        ? 'received'
        : 'did-not-recipient';

    default:
      return documentStatus === InvitationStatus.SIGNED
        ? 'signed'
        : 'did-not-sign';
  }
}
