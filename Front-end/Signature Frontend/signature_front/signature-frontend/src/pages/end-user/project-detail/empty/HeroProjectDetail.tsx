import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {
  InputBase,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  styled,
} from '@mui/material';
import {useEffect, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {FilterBy} from '@/constant/NGContant';
import {NGMesProject} from '@assets/iconExport/Allicon';

const BootstrapInput = styled(InputBase)(({theme}) => ({
  '& .MuiInputBase-input': {
    border: '1px solid #E9E9E9',
    padding: '10px 26px 10px 12px',
    transition: theme.transitions.create(['border-color', 'box-shadow']),
    '&:focus': {
      borderColor: '#80bdff',
    },
  },
}));

export type ISelect = {
  active: number | string;
  items: FilterByInterface[];
};
export type FilterByInterface = {
  key: number | string;
  project: string;
  selectedFilterBy: FilterBy;
};
/** Project detail empty header **/
const HeroProjectDetail = ({setSelectOption}: {setSelectOption: any}) => {
  const {t} = useTranslation();

  const {theme} = useAppSelector(state => state.enterprise);
  const [select, setSelect] = useState<ISelect>({
    active: 0,
    items: [
      {
        key: 0,
        project: t(Localization('drop-down', 'my-projects')),
        selectedFilterBy: FilterBy.ENDUSER,
      },
      {
        key: 1,
        project: t(Localization('drop-down', 'my-department-projects')),
        selectedFilterBy: FilterBy.DEPARTMENT,
      },
      {
        key: 2,
        project: t(Localization('drop-down', 'my-company-projects')),
        selectedFilterBy: FilterBy.COMPANY,
      },
    ],
  });
  useEffect(() => {
    setSelectOption(select.items[Number(select.active)]);
  }, [select]);
  const {mainColor} = theme[0];
  const handleChangeSelect = (e: SelectChangeEvent) => {
    setSelect({...select, active: e.target.value});
  };

  return (
    <Stack gap="32px" width="100%">
      <Stack direction="row" justifyContent="space-between" flexWrap="wrap">
        <NGText
          text={t(Localization('text', 'Signature projects'))}
          myStyle={{
            fontWeight: 600,
            fontSize: '27px',
            lineHeight: '36px',
          }}
        />
        <Select
          sx={{
            '& .MuiInputBase-input': {
              width: '247px',
              gap: '8px',
              fontFamily: 'Poppins',
              padding: '8px 12px 8px 10px',
              fontSize: '14px',
              fontWeight: 500,
              borderColor: mainColor,
              '&:focus': {
                borderColor: mainColor,
              },
            },
          }}
          input={<BootstrapInput />}
          value={select.active.toString()}
          onChange={handleChangeSelect}
          displayEmpty
          inputProps={{'aria-label': 'Without label'}}>
          {select.items.map(item => (
            <MenuItem
              key={item.key}
              value={item.key}
              sx={{fontSize: '14px', fontWeight: 500, fontFamily: 'Poppins'}}>
              <Stack direction={'row'} alignItems={'center'} gap={'10px'}>
                <NGMesProject
                  sx={{width: '16px', height: '17px', color: 'primary.main'}}
                />
                {item.project}
              </Stack>
            </MenuItem>
          ))}
        </Select>
      </Stack>
    </Stack>
  );
};

export default HeroProjectDetail;
