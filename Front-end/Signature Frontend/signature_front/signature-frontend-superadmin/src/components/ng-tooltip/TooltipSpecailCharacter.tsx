import React, {PropsWithChildren} from 'react';
import NGText from '@components/ng-text/NGText';
import {HtmlTooltip} from '@components/ng-table/TableDashboard/resource/TCell';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {IconButton, Stack} from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';

function TooltipSpecialCharacterLayout({
  children,
}: {position?: boolean} & PropsWithChildren) {
  return (
    <Stack width={'100%'}>
      <Stack width={'80%'} direction={'row'} height={'20px'}>
        {children}
        <HtmlTooltip
          placement="top"
          title={
            <NGText
              myStyle={{...FigmaBody.BodySmallBold}}
              text={"~! @ # $% ^& * _-+ =' | \\ \\ (){}\\ []:; \"' <>,.? /"}
            />
          }>
          <IconButton disableFocusRipple disableRipple disableTouchRipple>
            <InfoIcon sx={{color: 'grey', fontSize: '16px'}} />
          </IconButton>
        </HtmlTooltip>
      </Stack>
    </Stack>
  );
}

export default TooltipSpecialCharacterLayout;
