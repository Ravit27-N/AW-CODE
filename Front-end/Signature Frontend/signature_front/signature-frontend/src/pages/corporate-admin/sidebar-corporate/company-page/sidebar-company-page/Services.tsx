import {FONT_TYPE, STOP_TYPING_TIMEOUT} from '@/constant/NGContant';
import {
  StyleConstant,
  colorBlack,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {useLazyGetDepartmentOrServiceQuery} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {Center} from '@/theme';
import CatchErrorLoading from '@/utils/common/CatchErrorLoading';
import {shortNameCap} from '@/utils/common/SortName';
import {splitUserCompany} from '@/utils/common/String';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {NGThreeDotHorizontal} from '@assets/iconExport/ExportIcon';
import {NGButton} from '@components/ng-button/NGButton';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import NGText from '@components/ng-text/NGText';
import SearchIcon from '@mui/icons-material/Search';
import {
  Grid,
  InputAdornment,
  Stack,
  TextField,
  useMediaQuery,
} from '@mui/material';
import {Box} from '@mui/system';
import React from 'react';
import {useTranslation} from 'react-i18next';
import AddATeam from './services/AddATeam';

export type IServiceState = {
  toggle: boolean;
};

function Services() {
  const reduxTheme = useAppSelector(state => state.enterprise);
  const [search, setSearch] = React.useState<string | undefined>('');
  const {t} = useTranslation();
  /** function for trigger get template or service **/
  const [trigger, result] = useLazyGetDepartmentOrServiceQuery();
  /** get company ID from redux **/
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const pageSize = 150;
  /** fetching the first time when companyId have or changed **/
  const handleFetch = async (companyId: string | number) => {
    await trigger({
      companyId,
      search,
      page: 1,
      pageSize,
    }).unwrap();
  };
  React.useEffect(() => {
    if (company.companyId) {
      handleFetch(company.companyId).then(r => r);
    }
  }, [company.companyId]);
  /** fetching template when search changed **/
  React.useEffect(() => {
    /** Waiting for they stop typing 1.5 seconds for searching **/
    if (company.companyId) {
      const delayDebounceFn = setTimeout(() => {
        handleFetch(company.companyId).then(r => r);
      }, STOP_TYPING_TIMEOUT);
      return () => clearTimeout(delayDebounceFn);
    }
  }, [search]);
  /** for trigger close or open modal for create template  **/
  const [state, setState] = React.useState<IServiceState>({
    toggle: false,
  });

  const [, setStateParticipant] = React.useState<IServiceState>({
    toggle: false,
  });
  CatchErrorLoading({returnRedux: result});
  /** validate end point before show data  **/
  const validateEP =
    result.isSuccess &&
    !result.isLoading &&
    !result.error &&
    result.data.contents.length > 0;
  /** Responsive height **/
  const xl = useMediaQuery(`(max-width:1440px)`);
  const xxl = useMediaQuery(`(max-width:2000px)`);
  let height = `calc(100vh - 90vh)`;
  const handlerHeight = () => {
    if (xl) {
      height = `calc(100vh - 220px)`;
    } else if (xxl) {
      height = `calc(100vh - 220px)`;
    } else {
      height = `calc(100vh - 240px)`;
    }
    return height;
  };
  return (
    <Box width={'100%'} height={'100%'} bgcolor={'#FAFEFE'}>
      <Stack
        width={'100%'}
        direction={'row'}
        justifyContent={'space-between'}
        borderBottom={2}
        py={'8px'}
        px={'20px'}
        borderColor={'bg.main'}
        alignItems={'center'}>
        {validateEP ? (
          // models-corporate
          <NGText
            text={
              t(Localization('models-corporate', 'department')) +
              ' (' +
              result.data.contents.length +
              ')'
            }
            myStyle={{fontSize: 16, fontWeight: 600}}
          />
        ) : (
          <NGText
            text={'Servies (' + 0 + ')'}
            myStyle={{fontSize: 16, fontWeight: 600}}
          />
        )}
        <NGButton
          title={
            <NGText
              text={t(Localization('enterprise-services', 'add-a-team'))}
              myStyle={{color: 'white', fontSize: 12}}
            />
          }
          onClick={() => setState({...state, toggle: true})}
          disabled={false}
          myStyle={{
            bgcolor: reduxTheme.theme[0].mainColor,
            py: 1,
            '&.MuiButton-contained': {
              fontWeight: 600,
            },
            '&.Mui-disabled': {
              bgcolor: colorDisable,
              color: colorWhite,
            },
            '&:hover': {
              bgcolor: colorBlack,
            },
          }}
        />
      </Stack>
      <Center m={2} spacing={2}>
        <Stack width={'100%'}>
          <TextField
            size={'small'}
            placeholder={'Rechercher une équipe'}
            type="search"
            value={search}
            onChange={(event: any) => {
              setSearch(event.target.value);
            }}
            sx={{width: 500, ...StyleConstant.inputStyleLogin}}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start" sx={{mr: 1}}>
                  <SearchIcon />
                </InputAdornment>
              ),
              sx: {fontFamily: FONT_TYPE.POPPINS, fontSize: 14},
            }}
          />
        </Stack>
        <Grid
          container
          gap={pixelToRem(20)}
          width={'100%'}
          height={handlerHeight()}
          sx={{
            overflow: 'hidden',
            overflowY: 'scroll',
            ...StyleConstant.scrollNormal,
            py: '10px',
          }}>
          {validateEP && result.currentData ? (
            result.currentData.contents.map(item => (
              <Grid item key={item.unitName} width={pixelToRem(246)}>
                <Stack
                  onClick={() =>
                    setStateParticipant({
                      toggle: true,
                    })
                  }
                  width={pixelToRem(246)}
                  minWidth={pixelToRem(246)}
                  boxShadow={'0px 0px 10px rgba(0, 0, 0, 0.05)'}
                  bgcolor={'white'}
                  justifyContent={'space-between'}
                  borderRadius={pixelToRem(8)}
                  padding={pixelToRem(16)}
                  gap={pixelToRem(8)}
                  sx={{cursor: 'pointer'}}
                  height={pixelToRem(148)}>
                  <NGText
                    text={item.unitName}
                    myStyle={{
                      fontSize: pixelToRem(14),
                      fontWeight: 600,
                      width: '100%',
                    }}
                    iconEnd={
                      <NGThreeDotHorizontal
                        sx={{
                          color: reduxTheme.theme[0].mainColor ?? 'info.main',
                        }}
                      />
                    }
                  />
                  <NGGroupAvatar
                    character={
                      $isarray(item.employees)
                        ? item.employees.map(name =>
                            shortNameCap(name.firstName + ' ' + name.firstName),
                          )
                        : ['N/A']
                    }
                  />
                </Stack>
              </Grid>
            ))
          ) : (
            <Box sx={{width: '100%'}}>{/*<LinearProgress />*/}</Box>
          )}
        </Grid>
      </Center>
      <AddATeam open={state.toggle} setState={setState} state={state} />
    </Box>
  );
}

export default Services;
