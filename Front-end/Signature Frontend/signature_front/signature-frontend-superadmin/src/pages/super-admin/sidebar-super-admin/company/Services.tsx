import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {
  ContentsInterface,
  useLazyGetDepartmentOrServiceQuery,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {Center} from '@/theme';
import CatchErrorLoading from '@/utils/common/CatchErrorLoading';
import {shortNameCap} from '@/utils/common/SortName';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {NGThreeDotHorizontal} from '@assets/iconExport/ExportIcon';
import {NGButton} from '@components/ng-button/NGButton';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import NGText from '@components/ng-text/NGText';
import {
  ClosePage,
  FONT_TYPE,
  STOP_TYPING_TIMEOUT,
  UNKOWNERROR,
} from '@constant/NGContant';
import {
  StyleConstant,
  colorBlack,
  colorDisable,
  colorWhite,
} from '@constant/style/StyleConstant';
import SearchIcon from '@mui/icons-material/Search';
import {Grid, InputAdornment, Stack, TextField} from '@mui/material';
import {Box} from '@mui/system';
import React from 'react';
import {useTranslation} from 'react-i18next';
import AddATeam from './services/AddATeam';
import {useSnackbar} from 'notistack';
import {HandleException} from '@/utils/common/HandleException';
import {Waypoint} from 'react-waypoint';
import {debounce} from 'lodash';
import {HtmlTooltip} from '@/components/ng-table/TableDashboard/resource/TCell';

export type IServiceState = {
  toggle: boolean;
};

function Services() {
  const {companyProviderTheme} = useAppSelector(state => state.enterprise);
  const {t} = useTranslation();
  const pageSize = 100;
  const {enqueueSnackbar} = useSnackbar();
  const [currData, setCurrData] = React.useState<ContentsInterface[]>([]);
  const [filter, setFilter] = React.useState({
    companyId: companyProviderTheme.companyId ?? '',
    search: '',
    page: 1,
    pageSize,
  });
  /** function for trigger get template or service **/
  const [trigger, result] = useLazyGetDepartmentOrServiceQuery();
  /** fetching the first time when companyId have or changed **/
  const handleFetch = async () => {
    try {
      const resData = await trigger(filter).unwrap();
      if (filter.page <= 1) {
        return setCurrData(resData.contents);
      }
      return setCurrData(pre => [...pre, ...resData.contents]);
    } catch (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  React.useEffect(() => {
    setFilter(pre => ({...pre, companyId: companyProviderTheme.companyId!}));
  }, [companyProviderTheme.companyId]);

  React.useEffect(() => {
    if (filter.companyId) {
      handleFetch();
    }
  }, [filter]);

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

  const handleGetNextPage = ({currentPosition}: Waypoint.CallbackArgs) => {
    if (currentPosition === 'inside') {
      setFilter(pre => ({...pre, page: pre.page + 1}));
    }
  };

  const handleSearchDebounce = React.useCallback(
    debounce((search: string) => {
      setFilter(pre => ({
        ...pre,
        search,
        page: 1,
      }));
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  if (ClosePage) {
    return (
      <Center sx={{height: '100vh'}}>
        <NGText text={'Service'} />
      </Center>
    );
  }

  const handleAddSuccess = () => {
    setFilter(pre => ({...pre, page: 1}));
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
          // enterprise-services
          <NGText
            text={
              t(Localization('enterprise-services', 'department')) +
              ' (' +
              result.data.total +
              ')'
            }
            myStyle={{fontSize: 16, fontWeight: 600}}
          />
        ) : (
          <NGText
            text={
              t(Localization('enterprise-services', 'department')) +
              ' (' +
              0 +
              ')'
            }
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
            bgcolor: 'primary.main',
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
            placeholder={'Rechercher une département'}
            type="search"
            onChange={(event: any) => {
              handleSearchDebounce(event.target.value);
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
          sx={{
            overflowY: 'scroll',
            maxHeight: 'calc(100vh - 200px)',
            py: '10px',
            ...StyleConstant.scrollNormal,
            '&::-webkit-scrollbar': {
              width: '0.1em',
            },
          }}>
          {currData.map((item, index) => (
            <Box key={item.id}>
              <Grid item width={pixelToRem(246)}>
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
                  <Stack direction="row">
                    <HtmlTooltip
                      placement={'top-start'}
                      title={
                        <NGText
                          text={item.unitName}
                          myStyle={{
                            fontSize: pixelToRem(14),
                            fontWeight: 600,
                          }}
                        />
                      }>
                      <Box
                        sx={{
                          width: pixelToRem(246),
                          overflow: 'hidden',
                          display: '-webkit-box',
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: 'vertical',
                        }}>
                        <NGText
                          text={item.unitName}
                          myStyle={{
                            fontSize: pixelToRem(14),
                            fontWeight: 600,
                          }}
                        />
                      </Box>
                    </HtmlTooltip>

                    <NGThreeDotHorizontal
                      sx={{
                        color: companyProviderTheme.mainColor ?? 'info.main',
                        fontSize: pixelToRem(12),
                        ml: 0.5,
                        mt: 0.5,
                      }}
                    />
                  </Stack>
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
              {index + 1 === currData.length &&
                result?.currentData?.hasNext && (
                  // rerender it every time array change and trigger it after last item render
                  <Waypoint onEnter={handleGetNextPage} />
                )}
            </Box>
          ))}
        </Grid>
      </Center>
      <AddATeam
        open={state.toggle}
        setState={setState}
        state={state}
        onAddSuccess={handleAddSuccess}
      />
      {/*<AddParticipant*/}
      {/*  open={stateParticipant.toggle}*/}
      {/*  setState={setStateParticipant}*/}
      {/*  state={stateParticipant}*/}
      {/*/>*/}
    </Box>
  );
}

export default Services;
