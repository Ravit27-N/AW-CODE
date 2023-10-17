import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  NGCorrect,
  NGDetailRefuseIcon,
  NGFalse,
} from '@assets/iconExport/Allicon';
import {BoxStatus} from '@components/ng-switch-case-status/BoxStatus';
import {DocStatusInterfaces} from '@components/ng-switch-case-status/interface';
import {useTranslation} from 'react-i18next';
import {Stack, Typography} from '@mui/material';
import NGText from '@components/ng-text/NGText';
import {FONT_TYPE} from '@/constant/NGContant';
import {HtmlTooltip} from '@components/ng-table/TableDashboard/resource/TCell';
import IconButton from '@mui/material/IconButton';
import * as React from 'react';
import {Box} from '@mui/system';

/** interface of components **/
interface NGDocStatusInterfaces {
  StatusName: keyof DocStatusInterfaces;
  commentRefuse?: string;
}

/** style for text because it doesn't have style in figma  **/
const styleText = {
  fontFamily: FONT_TYPE.POPPINS,
  fontWeight: 500,
  fontSize: pixelToRem(11),
  lineHeight: pixelToRem(16),
  color: 'Primary.main',
};
/** pop up when we hover on refuse detail **/
const MessagePopUp = ({comments}: {comments: string}) => {
  const {t} = useTranslation();
  return (
    <Box
      width={'273px'}
      height={'80px'}
      borderRadius={'4px'}
      py={'8px'}
      px={'12px'}
      gap={'6px'}>
      <Typography sx={{...styleText, color: 'Black.main', fontWeight: 400}}>
        <b style={{...styleText, color: 'Black.main', fontWeight: 500}}>
          {t(Localization('project-detail', 'reason-reject'))}
        </b>
        {comments}
      </Typography>
    </Box>
  );
};
/** icon and text of refuse detail **/
const RefuseStatus = () => {
  const {t} = useTranslation();
  return (
    <Stack direction={'row'} justifyContent={'center'} alignItems={'center'}>
      <NGDetailRefuseIcon sx={{color: 'Primary.main'}} />
      <NGText
        text={t(Localization('project-detail', 'detail'))}
        myStyle={{
          ...styleText,
        }}
      />
    </Stack>
  );
};

export function NGDocStatus({
  StatusName,
  commentRefuse,
}: NGDocStatusInterfaces) {
  const {t} = useTranslation();
  switch (StatusName) {
    /**
         Status signed,approved, received
         **/
    case 'SIGNED':
    case 'APPROVED':
    case 'RECEIVED':
    case 'READ': {
      return (
        <BoxStatus
          title={t(Localization('documentStatus', StatusName))}
          icon={
            <NGCorrect
              sx={{
                fontSize: pixelToRem(9),
                color: 'white',
              }}
            />
          }
        />
      );
    }

    /**
         Status REFUSED
         **/

    case 'REFUSED': {
      return (
        <Stack direction={'row'}>
          <BoxStatus
            title={t(Localization('documentStatus', StatusName))}
            bgColor={'red'}
            icon={
              <NGFalse
                sx={{
                  fontSize: pixelToRem(9),
                  color: 'white',
                }}
              />
            }
          />
          {commentRefuse && (
            <HtmlTooltip title={<MessagePopUp comments={commentRefuse} />}>
              <IconButton disableFocusRipple disableRipple disableTouchRipple>
                <RefuseStatus />
              </IconButton>
            </HtmlTooltip>
          )}
        </Stack>
      );
    }
    /**
         Status don't have in enum in const file
         **/
    default: {
      return (
        <BoxStatus
          title={t(Localization('documentStatus', 'IN_PROGRESS'))}
          bgColor={'White.main'}
          textColor={'Black.main'}
          borderColor={'1px solid Black'}
        />
      );
    }
  }
}
