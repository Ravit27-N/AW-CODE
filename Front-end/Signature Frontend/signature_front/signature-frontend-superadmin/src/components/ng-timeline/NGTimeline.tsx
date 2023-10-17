import {StyleConstant} from '@/constant/style/StyleConstant';
import NGText from '@components/ng-text/NGText';
import Timeline from '@mui/lab/Timeline';
import TimelineConnector from '@mui/lab/TimelineConnector';
import TimelineDot from '@mui/lab/TimelineDot';
import TimelineItem from '@mui/lab/TimelineItem';
import TimelineOppositeContent from '@mui/lab/TimelineOppositeContent';
import TimelineSeparator from '@mui/lab/TimelineSeparator';

import {HistoryStatus} from '@/constant/NGContant';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {getTimeFromSecondCompareToCurrentDate} from '@/utils/common/Date';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray, $ok} from '@/utils/request/common/type';
import {
  NGCorrectProject,
  NGExpiredTime,
  NGFalse,
  NGFolderAddFile,
  NGTelegramOutline,
} from '@assets/iconExport/ExportIcon';
import {Stack} from '@mui/material';
import {useTranslation} from 'react-i18next';

type HistoryStatusType = keyof typeof HistoryStatus;
type HistoryType = {
  action: HistoryStatusType;
  actionBy: string;
  dateStatus: number;
  sortOrder: number;
};

export default function NGTimeline({histories}: {histories: any[]}) {
  const {username} = useAppSelector(state => state.authentication.user);
  const {t} = useTranslation();
  let tmp: any[] = [];
  if ($isarray(histories)) {
    tmp = [...histories];
    tmp = tmp.sort((a, b) => (a.sortOrder > b.sortOrder ? 1 : -1));
  }
  const handlerNotCompleted = (D: HistoryType) => {
    if (!['COMPLETED', HistoryStatus.EXPIRED].includes(D.action)) {
      return username;
    }
    return t(Localization('historyStatus', D.action));
  };

  return (
    <Stack alignItems={'flex-start'} width={'100%'}>
      <NGText
        text={'Historique'}
        myStyle={{
          color: 'Black.main',
          fontWeight: 500,
          fontSize: pixelToRem(14),
          lineHeight: pixelToRem(16),
          pl: 3,
        }}
      />

      <Timeline position="right" sx={{width: '100%'}}>
        {$isarray(tmp) &&
          tmp?.slice().reverse().map((D: HistoryType, index: number) => {
            return (
              <Stack
                direction={'row'}
                sx={{
                  width: '100%',
                  justifyContent: 'space-between',
                }}
                key={`${D.action}_${D.dateStatus}`}
                spacing={10}>
                <TimelineItem
                    key={`${D.action}_${D.dateStatus}`}
                  sx={{height: '10%', width: '100%'}}>
                  <TimelineSeparator>
                    <ChangeIconBaseOnAction action={D.action} />
                    {index === tmp?.length - 1 ? <></> : <TimelineConnector />}
                  </TimelineSeparator>
                  <TimelineOppositeContent
                    sx={{m: 'auto 0'}}
                    align="right"
                    variant="body2"
                    color="text.secondary">
                    <Stack>
                      <Stack direction={'row'} justifyContent={'space-between'}>
                        <NGText
                          text={
                            $ok(D.actionBy) && D.actionBy !== ''
                              ? D.actionBy
                              : handlerNotCompleted(D)
                          }
                          myStyle={
                            !['COMPLETED', HistoryStatus.EXPIRED].includes(
                              D.action,
                            )
                              ? {
                                  ...FigmaBody.BodySmall,
                                  width: 'auto',
                                }
                              : {
                                  ...FigmaBody.BodyMediumBold,
                                  width: 'auto',
                                }
                          }
                          textAlign={'start'}
                          width={'100%'}
                        />
                        <NGText
                          text={'Aujourd’hui'}
                          myStyle={{
                            ...FigmaBody.BodySmall,
                            width: 'auto',
                          }}
                          textAlign={'start'}
                          width={'100%'}
                        />
                      </Stack>
                      <Stack direction={'row'} justifyContent={'space-between'}>
                        <NGText
                          text={
                            !['COMPLETED', HistoryStatus.EXPIRED].includes(
                              D.action,
                            ) && t(Localization('historyStatus', D.action))
                          }
                          myStyle={{
                            ...FigmaBody.BodyMediumBold,
                            width: 'auto',
                          }}
                          textAlign={'start'}
                          width={'100%'}
                        />
                        <NGText
                          text={getTimeFromSecondCompareToCurrentDate(
                            D.dateStatus,
                          ).toString()}
                          myStyle={{
                            ...FigmaBody.BodySmall,
                            width: 'auto',
                          }}
                          textAlign={'start'}
                          width={'100%'}
                        />
                      </Stack>
                    </Stack>
                  </TimelineOppositeContent>
                </TimelineItem>
              </Stack>
            );
          })}
      </Timeline>
    </Stack>
  );
}
const ChangeIconBaseOnAction = ({action}: {action: any}) => {
  const theme = store.getState().enterprise.theme;

  switch (action) {
    case HistoryStatus.EXPIRED:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: '#CE0804',
          }}>
          <NGExpiredTime
            sx={{
              color: 'White.main',
              fontSize: pixelToRem(22),
              ...StyleConstant.timeLineIcon,
              ml: pixelToRem(7),
              mt: pixelToRem(6),
            }}
          />
        </TimelineDot>
      );
    case HistoryStatus.CREATED:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: `${theme[0].mainColor}20`,
          }}>
          <NGFolderAddFile
            sx={{
              color: 'Primary.main',
              fontSize: pixelToRem(22),
              ...StyleConstant.timeLineIcon,
              ml: pixelToRem(7),
              mt: pixelToRem(7),
            }}
          />
        </TimelineDot>
      );

    case HistoryStatus.SENT:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: 'blue.light',
          }}>
          <NGTelegramOutline
            sx={{
              color: 'blue.dark',
              fontSize: pixelToRem(25),
              ...StyleConstant.timeLineIcon,
            }}
          />
        </TimelineDot>
      );

    case HistoryStatus.SIGNED:
    case HistoryStatus.READ:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: '#E8F4EE',
          }}>
          <NGCorrectProject
            sx={{
              color: 'green',
              ...StyleConstant.timeLineIcon,
              fontSize: pixelToRem(25),
            }}
          />
        </TimelineDot>
      );

    case HistoryStatus.APPROVED:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: '#E8F4EE',
            fontSize: pixelToRem(25),
          }}>
          <NGCorrectProject
            sx={{color: 'green', ...StyleConstant.timeLineIcon}}
          />
        </TimelineDot>
      );

    case HistoryStatus.COMPLETED:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: 'green',
          }}>
          <NGCorrectProject
            sx={{
              ...StyleConstant.timeLineIcon,
              color: 'White.main',
              fontSize: pixelToRem(25),
            }}
          />
        </TimelineDot>
      );

    case HistoryStatus.REFUSED:
      return (
        <TimelineDot
          sx={{
            ...StyleConstant.timeLineCircle,
            bgcolor: '#CE0804',
          }}>
          <NGFalse
            sx={{
              ...StyleConstant.timeLineIcon,
              color: 'White.main',
              fontSize: pixelToRem(12),
              margin: '10px',
            }}
          />
        </TimelineDot>
      );

    default:
      return <span></span>;
  }
};
