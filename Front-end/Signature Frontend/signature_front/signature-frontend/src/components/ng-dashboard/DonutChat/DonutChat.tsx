import {StyleConstant} from '@/constant/style/StyleConstant';
import {Center} from '@/theme';
import {CheckColorDonut} from '@/utils/common/CheckColorDonut';
import {NgProgress} from '@components/ng-progression/NGProgress';
import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';
import NGText from '@components/ng-text/NGText';
import {Box, Stack} from '@mui/material';
import {MayHaveLabel, ResponsivePie} from '@nivo/pie';
import {ReactNode} from 'react';

export const Progressing = ({
  label,
  color,
  value,
  totals,
}: {
  label: ReactNode;
  color: string;
  value: number;
  totals: number;
}) => {
  return (
    <Stack width={'80%'}>
      {label}
      <Center direction={'row'} spacing={2} mt={0.7}>
        <NgProgress
          sx={{
            width: '100%',
            height: '10px',
            borderRadius: 5,
            '& .MuiLinearProgress-bar': {
              backgroundColor: color,
            },
          }}
          variant="determinate"
          value={value === 0 && totals === 0 ? 0 : (value / totals) * 100}
        />
        <NGText text={value} sx={{color: 'black.main'}} />
      </Center>
    </Stack>
  );
};
export const NGPie = ({
  data,
  innerRadius = 0.8,
  TextCenter,
}: {
  data: {
    id: keyof ProjectStatusInterfaces | 'URGENT';
    label: string;
    value: string | number;
  }[];
  innerRadius?: number;
  width?: number;
  TextCenter: ReactNode;
}) => {
  return (
    <Center sx={{height: '274px', p: 4}}>
      <Box sx={{...StyleConstant.root, width: '100%'}}>
        <ResponsivePie
          data={data as MayHaveLabel[]}
          innerRadius={innerRadius}
          padAngle={0.7}
          colors={data.map(item => CheckColorDonut({statusId: item.id})!)}
          cornerRadius={3}
          activeOuterRadiusOffset={8}
          borderWidth={1}
          enableArcLinkLabels={false}
          enableArcLabels={false}
          borderColor={{from: 'color', modifiers: [['darker', 0.2]]}}
          arcLinkLabelsSkipAngle={10}
          arcLinkLabelsTextColor="#333333"
          arcLinkLabelsThickness={2}
          arcLinkLabelsColor={{from: 'color'}}
          arcLabelsSkipAngle={10}
          arcLabelsTextColor={{from: 'color', modifiers: [['darker', 2]]}}
          sortByValue={true} // sort by value
          isInteractive={false}
          endAngle={-360}
          startAngle={360}></ResponsivePie>
        <Box sx={{...StyleConstant.overlay}}>{TextCenter}</Box>
      </Box>
    </Center>
  );
};
