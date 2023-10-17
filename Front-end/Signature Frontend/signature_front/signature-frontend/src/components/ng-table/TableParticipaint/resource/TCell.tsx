import {Stack} from '@mui/material';
import TableCell from '@mui/material/TableCell';

import {Center} from '@/theme';
import {shortName} from '@/utils/common/SortName';
import {CircleBox} from '@components/ng-box-of-model/NGBoxModel';
import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';
import NGText from '@components/ng-text/NGText';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';

import {InvitationStatus} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  NGBell,
  NGStopWatch,
  NGTelegram,
  NGThreeDot,
} from '@assets/iconExport/Allicon';
import NgPopOver from '@components/ng-popover/NGPopOver';
import {NGDocStatus} from '@components/ng-switch-case-status/NGDocStatus';
import ButtonThreeDotParticipant from '@components/ng-table/TableParticipaint/action/ButtonThreeDot';
import {useTranslation} from 'react-i18next';
interface Type {
  row: NGTableParticipantTypeCreateData;
  isItemSelected: boolean;
  labelId: string;
  handleClickDot: any;
  open: boolean;
  projectId: string;
}
function TCell_participant({row, projectId}: Type) {
  const {t} = useTranslation();
  return (
    <>
      <TableCell align="center">
        <Center>
          <Avatar
            sx={{
              bgcolor: 'bg.main',
              width: pixelToRem(24),
              height: pixelToRem(24),
              color: 'Primary.main',
            }}>
            <NGText
              text={row.order}
              myStyle={{
                fontSize: pixelToRem(12),
                fontWeight: 500,
                lineHeight: pixelToRem(16),
              }}
            />
          </Avatar>
        </Center>
      </TableCell>
      <TableCell align="right">
        <Box sx={{width: '100%'}}>
          <Center sx={{alignItems: 'flex-start'}}>
            <CircleBox
              hasLabel={true}
              color={'green'}
              text={shortName(row.nom)}
              textLabel={
                <Stack textAlign={'left'}>
                  <NGText
                    text={row.nom}
                    myStyle={{
                      textTransform: 'capitalize',
                      fontSize: pixelToRem(11),
                      fontWeight: 400,
                      lineHeight: pixelToRem(20),
                    }}
                  />
                  <NGText
                    text={row.email}
                    myStyle={{
                      fontSize: pixelToRem(11),
                      fontWeight: 400,
                      lineHeight: pixelToRem(20),
                    }}
                  />
                  <NGText
                    text={row.phone}
                    myStyle={{
                      fontSize: pixelToRem(11),
                      fontWeight: 400,
                      lineHeight: pixelToRem(20),
                    }}
                  />
                </Stack>
              }
              sizeOfCircle={25}
              sxCircle={{
                bgcolor: 'bg.dark',
                border: 1,
                borderColor: 'black.main',
                width: pixelToRem(32),
                height: pixelToRem(32),
              }}
            />
          </Center>
        </Box>
      </TableCell>
      <TableCell align="left">
        <NGText
          text={row.role}
          myStyle={{
            fontSize: pixelToRem(12),
            fontWeight: 300,
            lineHeight: pixelToRem(16),
          }}
        />
      </TableCell>
      <TableCell align="left">
        <NGText
          text={row.invitation}
          iconStart={
            row.invitation === 'En attente' ? (
              <NGStopWatch
                sx={{fontSize: pixelToRem(10), color: 'blue.main'}}
              />
            ) : (
              <NGTelegram
                sx={{fontSize: pixelToRem(10), color: 'Primary.main'}}
              />
            )
          }
          styleTextHaveIcon={{
            justifyContent: 'flex-start',
            alignContent: 'center',
          }}
          myStyle={{
            fontSize: pixelToRem(12),
            fontWeight: 300,
            lineHeight: pixelToRem(16),
            bgcolor: 'bg.dark',
            color: 'Black.main',
            pl: 1,
          }}
        />
      </TableCell>
      <TableCell align="center">
        {/** add message for refused **/}
        <NGDocStatus StatusName={row.status} commentRefuse={row.comment} />
      </TableCell>
      <TableCell
        align="left"
        onClick={e => {
          e.stopPropagation();
        }}>
        <Center
          width={'100%'}
          justifyContent={'center'}
          alignItems={'flex-start'}
          height={'100%'}>
          <Stack
            direction={'row'}
            justifyContent={'center'}
            spacing={3}
            alignItems={'center'}>
            <NGBell
              sx={{
                display:
                  row.status ===
                  t(Localization('invitationStatus', InvitationStatus.SIGNED))
                    ? 'none'
                    : 'flex',
                color: 'Black.main',
                fontSize: pixelToRem(14),
              }}
            />
            <NgPopOver
              button={
                <NGThreeDot
                  sx={{
                    color: 'primary.main',
                    fontSize: pixelToRem(14),
                  }}
                />
              }
              contain={
                <ButtonThreeDotParticipant
                  participantId={row.action}
                  data={{
                    projectId,
                    projectName: row.nom,
                  }}
                />
              }
            />
          </Stack>
        </Center>
      </TableCell>
    </>
  );
}

export default TCell_participant;
