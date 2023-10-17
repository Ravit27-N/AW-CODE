import {FONT_TYPE} from '@/constant/NGContant';
import {M_size, StyleConstant} from '@/constant/style/StyleConstant';
import {FigmaInput} from '@/constant/style/themFigma/Input';
import {
  ISortField,
  ISortFieldUser,
  useGetDepartmentOrServiceDashboardQuery,
  useGetUserByDepartmentOrServiceQuery,
  useLazyGetDashboardStatusQuery,
  useLazyGetDepartmentOrServiceQuery,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {formatDate} from '@/utils/common/Date';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import {NGSelectCorporate} from '@components/ng-inputField/NGInput';
import NgPopOver from '@components/ng-popover/NGPopOver';
import NGText from '@components/ng-text/NGText';
import Stack from '@mui/material/Stack';
import {Box} from '@mui/system';
import {CalendarPicker} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';

import {NGDateSelectIcon} from '@/assets/Icon';
import {NGDot, NGInfo} from '@/assets/iconExport/Allicon';
import {NGCardCorporate} from '@/components/ng-box-of-model/NGBoxModel';
import {
  NGPie,
  Progressing,
} from '@/components/ng-dashboard/DonutChat/DonutChat';
import {NGSwitchCaseBoxStatus} from '@/components/ng-switch-case-status/statusOfThreeBox/NGSwitchCaseBoxStatus';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {Center, VStack} from '@/theme';
import {CheckColorDonut} from '@/utils/common/CheckColorDonut';
import {maxValue} from '@/utils/common/MaxValue';
import {splitUserCompany} from '@/utils/common/String';
import {validateCard} from '@/utils/roles/corporate/corporate-dashboard';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {Grid, Skeleton} from '@mui/material';
import TableService from '@pages/corporate-admin/table/service/TableService';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import React, {PropsWithChildren} from 'react';
import TableUsers from './table/user/TableUser';

export type ISortFieldAndDirService = {
  sortField: ISortField;
  sortDirection: 'asc' | 'desc';
};

export type ISortFieldAndDirUser = {
  sortField: ISortFieldUser;
  sortDirection: 'asc' | 'desc';
};

export type ISelectEnterPrise = {
  key: string | number;
};
const Header = ({children}: PropsWithChildren) => {
  return (
    <Box
      sx={{
        width: 'full',
        backgroundImage: `url(${bgLogo})`,
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover',
        borderBottom: 1,
        borderColor: '#E9E9E9',
      }}>
      <Stack
        direction={'row'}
        justifyContent={'space-between'}
        alignItems={'center'}
        height={'100%'}
        sx={{
          p: '40px 72px',
        }}
        spacing={2}>
        <NGText
          text={t(Localization('text', 'Dashboard'))}
          sx={{
            color: 'black.main',
            ...StyleConstant.textBold,
            fontSize: M_size.h1,
            fontFamily: FONT_TYPE.POPPINS,
            fontWeight: 700,
          }}
        />
        {children}
      </Stack>
    </Box>
  );
};

/** width of header table title */
const DashboardCorporate = () => {
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const [dateStart, setDateStart] = React.useState<Dayjs | null>(
    dayjs(
      new Date(Date.now())
        .toLocaleDateString('zh-Hans-CN', {
          month: '2-digit',
          day: '2-digit',
          year: 'numeric',
        })
        .replace(/\//g, '-'),
    ).startOf('M'),
  );

  const [dateFinish, setDateFinish] = React.useState<Dayjs | null>(
    dayjs(
      new Date(Date.now())
        .toLocaleDateString('zh-Hans-CN', {
          month: '2-digit',
          day: '2-digit',
          year: 'numeric',
        })
        .replace(/\//g, '-'),
    ),
  );

  const [sortService, setSortService] = React.useState<ISortFieldAndDirService>(
    {
      sortField: 'id',
      sortDirection: 'desc',
    },
  );
  const [sortUser, setSortUser] = React.useState<ISortFieldAndDirUser>({
    sortField: 'id',
    sortDirection: 'desc',
  });
  const companyId = company.companyId as string;
  const [selectData, setSelectData] = React.useState<ISelectEnterPrise>({
    key: `C-${companyId}`,
  });

  /** get department or service **/
  const [triggerCorServiceData, corServiceData] =
    useLazyGetDepartmentOrServiceQuery();
  /** get dashboard status **/
  const [triggerCorDashboardData, corDashboardData] =
    useLazyGetDashboardStatusQuery();
  /** fetching the first time when companyId have or changed **/
  React.useEffect(() => {
    const handleFetchCorServiceData = async (companyId: string) => {
      await triggerCorServiceData({
        companyId,
        search: '',
        page: 1,
        pageSize: 15,
      }).unwrap();
      await triggerCorDashboardData({
        companyId,
        businessUnitId,
        startDate: dateStart?.format('YYYY-MM-DDTHH:mm:ss[Z]'),
        endDate: dateFinish?.format('YYYY-MM-DDTHH:mm:ss[Z]'),
      }).unwrap();
    };
    if (company.companyId) {
      handleFetchCorServiceData(company.companyId.toString()).then(r => r);
    }
  }, [company.companyId, dateStart, dateFinish]);

  const businessUnitId =
    selectData.key.toString().split('-')[1] === companyId ? '' : selectData.key;
  const {
    currentData: corServiceDataDashboard,
    isLoading: corServiceLoadingDashboard,
    isFetching: corServiceFetchingDashboard,
  } = useGetDepartmentOrServiceDashboardQuery({
    companyId,
    sortDirection: sortService.sortDirection,
    sortField: sortService.sortField,
    startDate: dateStart?.format('YYYY-MM-DDTHH:mm:ss[Z]')!,
    endDate: dateFinish?.format('YYYY-MM-DDTHH:mm:ss[Z]')!,
    search: '',
    page: 1,
    pageSize: 15,
  });
  const {
    currentData: corporateUserData,
    isLoading: corporateUserLoading,
    isFetching: corporateUserFetching,
  } = useGetUserByDepartmentOrServiceQuery(
    {
      companyId,
      businessUnitId,
      sortField: sortUser.sortField,
      sortDirection: sortUser.sortDirection,
      startDate: dateStart?.format('YYYY-MM-DDTHH:mm:ss[Z]')!,
      endDate: dateFinish?.format('YYYY-MM-DDTHH:mm:ss[Z]')!,
      search: '',
    },
    {skip: !companyId},
  );

  React.useMemo(() => {
    if (dateFinish?.isBefore(dateStart)) {
      setDateFinish(dateStart);
    }
  }, [dateFinish, dateStart]);
  return (
    <Stack sx={{width: '100%', height: '100vh', bgcolor: 'white'}}>
      {/** Header **/}
      <Header>
        <Stack direction={'row'} spacing={1} width="auto">
          <NGSelectCorporate
            label={''}
            name={selectData}
            setName={setSelectData}
            enterprise={[{key: `C-${companyId}`, value: 'Mon entreprise'}]}
            group={
              corServiceData.currentData
                ? corServiceData.currentData.contents.map(item => ({
                    key: item.id,
                    value: item.unitName,
                  }))
                : []
            }
          />
          <NgPopOver
            Sx={{mt: 2}}
            button={
              <Stack
                sx={{...StyleConstant.inputStyleField}}
                justifyContent="space-between"
                direction="row"
                alignItems="center">
                <Stack direction="row" alignItems="center">
                  <NGDateSelectIcon sx={{color: 'Primary.main', mr: 0.5}} />
                  <NGText
                    text={
                      formatDate(dateStart!) +
                      ` ${t(Localization('corporate-form', 'to'))} ` +
                      formatDate(dateFinish!)
                    }
                    sx={{...FigmaInput.InputTextMediumBold}}
                  />
                </Stack>
                <ArrowDropDownIcon />
              </Stack>
            }
            contain={
              <Stack direction={'row'} spacing={2} p={2}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <CalendarPicker
                    disableHighlightToday={true}
                    date={dateStart}
                    onChange={newDate => setDateStart(newDate)}
                  />
                </LocalizationProvider>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <CalendarPicker
                    disableHighlightToday={true}
                    minDate={dateStart}
                    date={dateFinish}
                    onChange={newDate => {
                      const d = newDate!
                        .set('hour', 23)
                        .set('minute', 59)
                        .set('second', 59);
                      setDateFinish(d);
                    }}
                  />
                </LocalizationProvider>
              </Stack>
            }
            horizontal={'left'}
            vertical={'bottom'}
            horizontalT={'left'}
            verticalT={'top'}
          />
        </Stack>
      </Header>
      {/** body have 3 box **/}
      <Stack
        spacing={5}
        height={`calc(100vh - 190px)`}
        sx={{
          overflow: 'auto',
          '&::-webkit-scrollbar': {
            width: '0',
          },
        }}>
        <Stack
          width={'100%'}
          height={'132px'}
          alignItems="center"
          sx={{
            p: '40px 72px',
          }}>
          <Box width={'100%'}>
            <Grid container spacing={2}>
              {corDashboardData.isLoading ||
              corDashboardData.isFetching ||
              !corDashboardData.currentData
                ? [1, 2, 3].map(item => (
                    <Grid item lg={4} md={4} key={item}>
                      <Skeleton
                        variant="rectangular"
                        sx={{
                          borderRadius: '6px',
                        }}
                        animation="wave"
                        width={'auto'}
                        height={'148px'}
                      />
                    </Grid>
                  ))
                : (corDashboardData.currentData.contents.cards ?? []).map(
                    item => (
                      <Grid item lg={4} md={4} key={item.label}>
                        <NGCardCorporate
                          borderBottomColorBox={
                            NGSwitchCaseBoxStatus({
                              key: item.id,
                              label: item.label,
                              value: item.value,
                            })!.colorBorder
                          }
                          title={
                            NGSwitchCaseBoxStatus({
                              key: item.id,
                              label: t(
                                Localization(
                                  'corporate-form',
                                  validateCard(item.id),
                                ),
                              ),
                              value: item.value,
                            })!.title
                          }
                          iconTitle={<NGInfo />}
                          semiTitle={
                            NGSwitchCaseBoxStatus({
                              key: item.id,
                              label: item.label,
                              value: item.value,
                            })!.time
                          }
                          iconSemiTitle={
                            <Center
                              bgcolor={
                                NGSwitchCaseBoxStatus({
                                  key: item.id,
                                  label: item.label,
                                  value: item.value,
                                })!.bgColor
                              }
                              p={1.2}
                              borderRadius={'6px'}
                              mr={1}>
                              {
                                NGSwitchCaseBoxStatus({
                                  key: item.id,
                                  label: item.label,
                                  value: item.value,
                                })!.icon
                              }
                            </Center>
                          }
                        />
                      </Grid>
                    ),
                  ) ?? <></>}
            </Grid>
          </Box>
        </Stack>
        <Stack
          sx={{
            p: '40px 72px',
          }}>
          <Grid
            container
            borderLeft={2}
            borderRight={2}
            borderBottom={2}
            borderTop={2}
            borderColor={'#E9E9E9'}
            height={'auto'}>
            <Grid item lg={5} md={12}>
              <Stack direction={{lg: 'column', md: 'row'}}>
                {corDashboardData.isLoading || corDashboardData.isFetching ? (
                  <Skeleton
                    variant="rectangular"
                    sx={{
                      borderRadius: '6px',
                    }}
                    animation="wave"
                    width={'auto'}
                    height={'710px'}
                  />
                ) : (
                  <>
                    <Center>
                      <Stack
                        direction={'row'}
                        justifyContent={'space-between'}
                        borderBottom={2}
                        py={2}
                        px={5}
                        borderColor={'#E9E9E9'}
                        width={'100%'}>
                        <NGText
                          text={t(
                            Localization(
                              'corporate-form',
                              'break-down-project',
                            ),
                          )}
                          myStyle={{
                            fontWeight: 600,
                            fontSize: 16,
                            lineHeight: '28px',
                          }}
                        />
                        <NGText
                          text={
                            corDashboardData.currentData
                              ? corDashboardData.currentData.contents.statuses
                                  .map(a => Number(a.value))
                                  .reduce(function (a: number, b: number) {
                                    return a + b;
                                  })
                              : 0
                          }
                          myStyle={{
                            fontWeight: 600,
                            fontSize: 16,
                            lineHeight: '28px',
                          }}
                        />
                      </Stack>
                    </Center>
                    <NGPie
                      innerRadius={0.75}
                      width={250}
                      TextCenter={
                        <VStack>
                          <NGText
                            text={
                              corDashboardData.currentData
                                ? t(
                                    Localization(
                                      'status-dashboard-donut',
                                      maxValue({
                                        data: corDashboardData.currentData
                                          .contents.statuses,
                                      }).id,
                                    ),
                                  )
                                : 0
                            }
                            sx={{
                              ...StyleConstant.textBold,
                              color: 'black.main',
                              fontSize: {lg: 14, md: 14},
                            }}
                          />
                          <NGText
                            text={
                              corDashboardData.currentData
                                ? maxValue({
                                    data: corDashboardData.currentData.contents
                                      .statuses,
                                  }).value
                                : 0
                            }
                            sx={{
                              ...StyleConstant.textBold,
                              color: 'black.main',
                              fontSize: {lg: 20, md: 20},
                            }}
                          />
                        </VStack>
                      }
                      data={
                        corDashboardData.currentData
                          ? corDashboardData.currentData.contents.statuses
                          : []
                      }
                    />
                    <Center width={'100%'} spacing={1} height={'full'}>
                      {(
                        (corDashboardData!.currentData &&
                          corDashboardData.currentData.contents.statuses.slice(
                            0,
                            5,
                          )) ??
                        []
                      ).map(item => {
                        return (
                          <Progressing
                            key={item.id}
                            label={
                              <NGText
                                text={t(
                                  Localization(
                                    'status-dashboard-donut',
                                    item.id,
                                  ),
                                )}
                                sx={{color: 'black.main'}}
                                iconStart={
                                  <NGDot
                                    sx={{
                                      color: CheckColorDonut({
                                        statusId: item.id,
                                      })!,
                                    }}
                                  />
                                }
                              />
                            }
                            color={CheckColorDonut({statusId: item.id})!}
                            value={Number(item.value)}
                            totals={(
                              (corDashboardData!.currentData &&
                                corDashboardData.currentData.contents
                                  .statuses) ??
                              []
                            ).reduce(function (prev: any, current: any) {
                              return prev + +current.value;
                            }, 0)}
                          />
                        );
                      })}
                    </Center>
                  </>
                )}
              </Stack>
            </Grid>
            <Grid item lg={7} md={12} borderLeft={2} borderColor={'#E9E9E9'}>
              <Center>
                <Stack
                  direction={'row'}
                  justifyContent={'space-between'}
                  borderBottom={2}
                  py={2}
                  px={2}
                  borderColor={'#E9E9E9'}
                  width={'100%'}>
                  <NGText
                    text={t(
                      Localization('corporate-form', 'depart-most-files'),
                    )}
                    myStyle={{
                      fontWeight: 600,
                      fontSize: 16,
                      lineHeight: '28px',
                    }}
                  />
                </Stack>
              </Center>
              {corServiceLoadingDashboard || corServiceFetchingDashboard ? (
                <Stack p={2} height="290px">
                  {Array.from({length: 7}, (_, index: number) => (
                    <Skeleton
                      key={index}
                      variant="rectangular"
                      sx={{
                        borderRadius: '6px',
                        mb: 0.5,
                      }}
                      animation="wave"
                      width={'auto'}
                      height={'35px'}
                    />
                  ))}
                </Stack>
              ) : (
                <TableService
                  currentData={corServiceDataDashboard}
                  setSortService={setSortService}
                  sortService={sortService}
                />
              )}

              <Center>
                <Stack
                  direction={'row'}
                  justifyContent={'space-between'}
                  borderBottom={2}
                  borderTop={2}
                  py={2}
                  px={2}
                  borderColor={'#E9E9E9'}
                  width={'100%'}>
                  <NGText
                    text={t(Localization('corporate-form', 'user-most-files'))}
                    myStyle={{
                      fontWeight: 600,
                      fontSize: 16,
                      lineHeight: '28px',
                    }}
                  />
                </Stack>
              </Center>
              {corporateUserLoading || corporateUserFetching ? (
                <Stack p={2} height="290px">
                  {Array.from({length: 7}, (_, index: number) => (
                    <Skeleton
                      key={index}
                      variant="rectangular"
                      sx={{
                        borderRadius: '6px',
                        mb: 0.5,
                      }}
                      animation="wave"
                      width={'auto'}
                      height={'35px'}
                    />
                  ))}
                </Stack>
              ) : (
                <TableUsers
                  currentData={corporateUserData}
                  setSortUser={setSortUser}
                  sortUser={sortUser}
                />
              )}
            </Grid>
          </Grid>
        </Stack>
      </Stack>
    </Stack>
  );
};

export default DashboardCorporate;
