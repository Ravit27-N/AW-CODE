import {ReactNode} from 'react';
import {HStack} from '@/theme';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {SxProps} from '@mui/material';
import NGText from '@components/ng-text/NGText';

interface Type {
  title1: string | ReactNode;
  title2: string | ReactNode;
  props?: SxProps;
}

export function NGTitle({title1, title2, props}: Type) {
  return (
    <HStack sx={{width: '100%', justifyContent: 'space-between', ...props}}>
      {/*<Text sx={{...MyStyle.textBold, fontSize: 18}}>{title1}</Text>*/}
      <NGText
        text={title1}
        myStyle={{...StyleConstant.textBold, fontSize: 22, fontWeight: 600}}
      />

      <NGText
        text={title2}
        myStyle={{fontSize: 13, color: 'Primary.main', fontWeight: 600}}
      />
    </HStack>
  );
}
