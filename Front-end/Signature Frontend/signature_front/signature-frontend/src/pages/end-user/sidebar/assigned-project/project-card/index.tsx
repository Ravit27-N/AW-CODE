import {NGTelegramOutline, NGWatch} from '@/assets/iconExport/Allicon';
import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import {DocumentStatus, Participant, ProjectStatus} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {DateFrench} from '@/utils/common';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGText from '@components/ng-text/NGText';
import {Stack} from '@mui/material';
import {Box} from '@mui/system';
import {t} from 'i18next';
import {MouseEvent} from 'react';

export type AssignedProjectEndUserType = {
  id: number;
  title: string;
  statue: Participant;
  signatoryId: number;
  createdAt: string;
  expireAt: string;
  name: string;
  flowId: string;
  uuid: string;
  documentStatus: keyof typeof DocumentStatus; // to tell project is done or in progress
};

const ProjectCardEndUser = ({
  active,
  data,
  onClick,
}: {
  active?: boolean;
  data: AssignedProjectEndUserType;
  onClick: (
    data: AssignedProjectEndUserType,
    event: MouseEvent<HTMLDivElement>,
  ) => void;
}) => {
  const {month, year, day} = DateFrench(new Date(Number(data.expireAt)));
  const {
    month: MCreatedAt,
    year: YCreatedAt,
    day: DCreatedAt,
  } = DateFrench(new Date(Number(data.createdAt)));
  return (
    <Stack
      gap={2}
      p={3}
      onClick={event => {
        onClick(data, event);
      }}
      sx={{
        cursor: 'pointer',
        borderWidth: 1,
        borderStyle: 'solid',
        borderColor: active ? 'Primary.main' : 'rgba(0,0,0,0.2)',
        borderRadius: 2,
        boxShadow: '0 0 8px -2px rgba(0,0,0,0.2)',
      }}>
      <Box
        display={'flex'}
        justifyContent={'space-between'}
        alignItems={'center'}>
        <NGText
          text={data.title}
          myStyle={{
            fontWeight: 600,
            fontSize: '14px',
          }}
        />
        {data.documentStatus === ProjectStatus.IN_PROGRESS ? (
          <NGText
            text={t(Localization('end-user-assigned-project', data.statue))}
            myStyle={{
              textTransform: 'uppercase',
              fontWeight: 600,
              fontSize: pixelToRem(11),
              borderWidth: 1,
              borderStyle: 'solid',
              borderColor: 'black',
              px: 1,
              py: 0.5,
              borderRadius: 0.5,
            }}
          />
        ) : (
          /** Status Done, This participant done his duty. */
          <NGText
            text={t(Localization('documentStatus', data.documentStatus))}
            myStyle={{
              backgroundColor: 'green',
              color: 'white',
              textTransform: 'uppercase',
              fontWeight: 600,
              fontSize: pixelToRem(11),
              borderWidth: 1,
              borderStyle: 'solid',
              borderColor: 'black',
              px: 1,
              py: 0.5,
              borderRadius: 0.5,
            }}
          />
        )}
      </Box>
      <Box display="flex">
        <NGText
          text={`${DCreatedAt} ${MCreatedAt} ${YCreatedAt}`}
          sx={{
            fontSize: '12px',
          }}
          iconStart={<NGTelegramOutline color="primary" />}
        />
        <NGText
          text={`${day} ${month} ${year}`}
          sx={{
            fontSize: '12px',
          }}
          iconStart={
            <NGWatch
              sx={{
                color: 'Primary.main',
                mr: 0.8,
                fontSize: pixelToRem(16),
              }}
            />
          }
        />
      </Box>
      <Box display="flex" alignItems={'center'}>
        <NGGroupAvatar
          character={[
            `${data.name.split(' ')[0].charAt(0)}${data.name
              .split(' ')[1]
              .charAt(0)}`,
          ]}
        />
        <NGText
          text={data.name}
          myStyle={{
            ml: 1,
            fontSize: '11px',
          }}
        />
      </Box>
    </Stack>
  );
};

export default ProjectCardEndUser;
