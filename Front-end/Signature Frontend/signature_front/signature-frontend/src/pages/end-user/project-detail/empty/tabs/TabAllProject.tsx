import {NGFilter, NGPlusIcon} from '@/assets/Icon';
import TableProjectDetail from '@/components/ng-table/TableProjectDetail/TableProjectDetail';
import {Localization} from '@/i18n/lan';
import SearchIcon from '@mui/icons-material/Search';
import {Button, InputAdornment, Stack, TextField} from '@mui/material';
import React, {Dispatch, SetStateAction} from 'react';
import {useTranslation} from 'react-i18next';
import {ITabs} from '../TabProjectDetail';
import {FilterByInterface} from '@pages/end-user/project-detail/empty/HeroProjectDetail';

export type ITableAllProject = {
  setDTab?: Dispatch<SetStateAction<ITabs>>;
  tab: ITabs;
  selectOption?: FilterByInterface;
  defaultFilterBy?: string;
  arrayOfStatus: ITabs[];
};

const TabAllProject = (props: ITableAllProject) => {
  const {setDTab, tab, arrayOfStatus} = props;
  const [search, setSearch] = React.useState<string>('');
  const {t} = useTranslation();
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(e.target.value);
  };

  return (
    <Stack>
      <Stack py="22px">
        <Stack direction="row" alignItems="center" gap="20px">
          <Stack width={'100%'}>
            <TextField
              onChange={handleSearch}
              InputProps={{
                style: {
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                  fontWeight: 400,
                  padding: 3,
                  paddingLeft: '10px',
                  color: '#767676',
                },
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
              type="text"
              placeholder={t(Localization('project-detail', 'search'))!}
              sx={{
                flexGrow: 1,
                maxWidth: '390px',
                borderRadius: '6px',
              }}
              size="small"
            />
          </Stack>
          <Stack
            width="646px"
            justifyContent="flex-end"
            direction="row"
            gap="16px">
            <Button
              startIcon={
                <NGFilter fontSize="small" sx={{mt: '5px', mr: '-8px'}} />
              }
              variant="outlined"
              sx={{
                fontFamily: 'Poppins',
                fontWeight: 600,
                color: '#000000',
                width: '89px',
                fontSize: '11px',
                borderColor: '#000000',
                textTransform: 'none',
              }}>
              {t(Localization('project-detail', 'filter'))}
            </Button>
            <Button
              startIcon={<NGPlusIcon sx={{width: '18px', mt: '-1px'}} />}
              variant="contained"
              sx={{
                fontFamily: 'Poppins',
                width: '144px',
                py: '10px',
                px: '10px',
                fontSize: '11px',
                textTransform: 'none',
              }}>
              {t(Localization('project-detail', 'new-project'))}
            </Button>
          </Stack>
        </Stack>
      </Stack>
      <TableProjectDetail
        setDTab={setDTab}
        search={search}
        tab={tab}
        arrayOfStatus={arrayOfStatus}
      />
    </Stack>
  );
};

export default TabAllProject;
