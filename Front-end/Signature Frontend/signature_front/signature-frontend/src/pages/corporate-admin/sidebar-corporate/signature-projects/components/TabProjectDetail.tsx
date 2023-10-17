import {NGPlusIcon} from '@/assets/Icon';
import TableProjectDetail from '@pages/corporate-admin/sidebar-corporate/signature-projects/components/TableProjectDetail';
import {FilterBy, STOP_TYPING_TIMEOUT} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import {FigmaCTA} from '@/constant/style/themFigma/CTA';
import {Localization} from '@/i18n/lan';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {setStartEndDate} from '@/redux/counter/CounterSlice';
import {useCountCorporateProjectsQuery} from '@/redux/slides/project-management/project';
import {BootstrapInput} from '@/theme';
import {DateFrench} from '@/utils/common';
import {StatusesInterface} from '@/utils/request/interface/Project.interface';
import {NGFalseListProject} from '@assets/iconExport/ExportIcon';
import NGPopOver from '@components/ng-popover/NGPopOver';
import NGText from '@components/ng-text/NGText';
import SearchIcon from '@mui/icons-material/Search';
import {TabContext, TabList, TabPanel} from '@mui/lab';
import {
  Box,
  Button,
  Chip,
  InputAdornment,
  LinearProgress,
  MenuItem,
  Select,
  SelectChangeEvent,
  Skeleton,
  Stack,
  Tab,
  TextField,
} from '@mui/material';
import {CalendarPicker} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {
  FilterByInterface,
  ISelect,
} from '@pages/end-user/project-detail/empty/HeroProjectDetail';
import FilterProjectDetail from '@pages/end-user/project-detail/empty/filter/FilterProjectDetail';
import UploadForm from '@pages/form/process-upload/Upload.form';
import dayjs, {Dayjs} from 'dayjs';
import React, {Dispatch, SetStateAction, useMemo, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {NGButton} from '@/components/ng-button/NGButton';
import {useNavigate, useParams} from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {debounce} from 'lodash';
import {$isarray} from '@/utils/request/common/type';
import {Route} from '@/constant/Route';

export type ITabs =
  | 'ALL-PROJECTS'
  | 'IN_PROGRESS'
  | 'DRAFT'
  | 'URGENT'
  | 'REFUSED'
  | 'COMPLETED'
  | 'EXPIRED'
  | 'ABANDON';

export const TabProjectDetail = ({
  selectOption,
  setOpen,
}: {
  selectOption?: FilterByInterface;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  const {t} = useTranslation();
  const {userId} = useParams();

  /** An active tab you can it default tab that you want to display. **/
  const [tab, setTab] = useState<ITabs>('ALL-PROJECTS');
  const CountProjects = useCountCorporateProjectsQuery({
    filterBy: FilterBy.ENDUSER, // selectOption ? selectOption.selectedFilterBy : FilterBy.ENDUSER,
    userId,
  });

  const handleChangeTab = (e: React.SyntheticEvent, newValue: ITabs) => {
    setTab(newValue);
  };
  if (CountProjects.isLoading) {
    return (
      <Box sx={{width: '100%'}}>
        <LinearProgress />
      </Box>
    );
  }
  const handlerCountProjects = () => {
    if (
      CountProjects.currentData &&
      $isarray(CountProjects.currentData.statuses)
    ) {
      return CountProjects.currentData.statuses;
    } else {
      return [];
    }
  };

  return (
    <Stack width="100%">
      <TabContext value={tab}>
        <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
          {CountProjects.isFetching ? (
            <LinearProgress />
          ) : (
            <TabList
              onChange={handleChangeTab}
              aria-label="lab API tabs example">
              {/**  Tab Total **/}
              <Tab
                sx={{
                  '&.MuiTab-root': {
                    fontFamily: 'Poppins',
                    fontWeight: 500,
                    fontSize: '14px',
                    color: '#000000',
                    p: 0,
                    mr: '24px',
                  },
                  textTransform: 'none',
                  pb: 3,
                }}
                // label={item.label}
                label={`${t(Localization('project-detail', 'all-project'))} (${
                  CountProjects.currentData
                    ? CountProjects.currentData.totalProjects
                    : 'error'
                })`}
                value={'ALL-PROJECTS'}
              />
              {/**  Tab form backend  **/}
              {CountProjects.isLoading ? (
                <Stack p={2} height="290px" direction={'row'} spacing={1}>
                  {Array.from({length: 8}, (_, index: number) => (
                    <Skeleton
                      key={index}
                      variant="rectangular"
                      sx={{
                        borderRadius: '3px',
                        mb: 0.5,
                      }}
                      animation="pulse"
                      width={'100px'}
                      height={'35px'}
                    />
                  ))}
                </Stack>
              ) : (
                handlerCountProjects().map(item => (
                  <Tab
                    key={item.id}
                    sx={{
                      '&.MuiTab-root': {
                        fontFamily: 'Poppins',
                        fontWeight: 500,
                        fontSize: '14px',
                        color: '#000000',
                        p: 0,
                        mr: '24px',
                      },
                      textTransform: 'none',
                      pb: 3,
                    }}
                    label={`${t(
                      Localization(
                        'project-detail',
                        item.label.toString().toLowerCase() as any,
                      ),
                    )} (${item.value})`}
                    value={item.id}
                  />
                ))
              )}
            </TabList>
          )}
        </Box>

        <TabPanel sx={{p: 0}} value={tab}>
          {/**   All tap in projects  ***/}
          <TabAllProject
            tab={tab}
            selectOption={selectOption}
            allStatus={
              CountProjects.currentData
                ? CountProjects.currentData.statuses
                : []
            }
            setOpen={setOpen}
          />
        </TabPanel>
      </TabContext>
    </Stack>
  );
};

export type ITableAllProject = {
  setDTab?: Dispatch<SetStateAction<ITabs>>;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  tab: ITabs;
  selectOption?: FilterByInterface;
  allStatus?: StatusesInterface[];
};

interface ChipData {
  key: number | string;
  label: string;
}

const TabAllProject = (props: ITableAllProject) => {
  const {tab, selectOption, allStatus, setOpen} = props;
  const [search, setSearch] = React.useState<string>('');
  const {t} = useTranslation();
  const navigate = useNavigate();

  const handleSearchDebounce = React.useCallback(
    debounce((e: React.ChangeEvent<HTMLInputElement>) => {
      setSearch(e.target.value);
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  const [popUp, setPopup] = React.useState(false);
  const handleClosePopup = () => {
    setPopup(false);
  };
  const [arrayOfStatus, setArrayOfStatus] = React.useState<ITabs[]>([]);

  const {theme} = useAppSelector(state => state.enterprise);
  const [chipData, setChipData] = React.useState<readonly ChipData[]>([
    {key: '12', label: 'React'},
  ]);
  const handleDelete = (chipToDelete: string) => () => {
    setArrayOfStatus(arrayOfStatus =>
      arrayOfStatus.filter(chip => chip !== chipToDelete),
    );
  };
  useMemo(() => {
    arrayOfStatus.forEach((status, index: number) => {
      const isHave = chipData.find(item => item.key === status);
      if (index !== 0 && !isHave) {
        setChipData([...chipData, {key: status, label: status}]);
      }
    });
  }, [arrayOfStatus]);
  // **---------------------------------------------------**/
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
  const [select, setSelect] = useState<ISelect>({
    active: 0,
    items: [
      {
        key: 0,
        project: t(Localization('drop-down', 'precise-date')),
        selectedFilterBy: FilterBy.ENDUSER,
      },
      {
        key: 1,
        project: t(Localization('drop-down', 'period')),
        selectedFilterBy: FilterBy.DEPARTMENT,
      },
    ],
  });
  const handleChangeSelect = (e: SelectChangeEvent) => {
    setSelect({...select, active: e.target.value});
  };
  // **----------------------------------------------------**/
  React.useEffect(() => {
    setArrayOfStatus([]);
    dispatch(
      setStartEndDate({
        start: null,
        end: null,
      }),
    );
  }, [tab]);
  const startEndDate = useAppSelector(state => state.counter.startEndDate);
  const startDateFrench = DateFrench(
    new Date(
      startEndDate.start
        ? startEndDate.start.toISOString()
        : dateStart!.toISOString(),
    ),
  );
  const endDateFrench = DateFrench(
    new Date(
      startEndDate.end
        ? startEndDate.end.toISOString()
        : dateFinish!.toISOString(),
    ),
  );
  const dispatch = useAppDispatch();

  return (
    <Stack>
      <Stack py="22px">
        <Stack direction="row" alignItems="center" gap="20px">
          <Stack width={'100%'}>
            <TextField
              onChange={handleSearchDebounce}
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
            <NGButton
              onClick={() => {
                navigate(Route.corporate.GROUP);
              }}
              btnProps={{
                disableFocusRipple: true,
                disableRipple: true,
                disableTouchRipple: true,
              }}
              locationIcon="start"
              icon={<ArrowBackIcon />}
              color={['#ffffff', `${theme[0].mainColor}`]}
              variant="outlined"
              fontSize="11px"
              myStyle={{
                p: '8px, 16px',
                minHeight: 0,
                minWidth: 0,
                borderRadius: '6px',
                border: `1px solid ${theme[0].mainColor}`,
              }}
              textSx={{width: '45px'}}
              fontWeight="600"
              title={t(Localization('project-detail', 'back'))}
            />

            {/**  Filter on status and date expiration **/}
            <FilterProjectDetail
              allStatus={allStatus}
              setArrayOfStatus={setArrayOfStatus}
              tab={tab}
              setSelection={setSelect}
            />

            <Button
              onClick={() => setOpen(true)}
              startIcon={<NGPlusIcon sx={{width: '18px', mt: '-1px'}} />}
              variant="contained"
              sx={{
                fontFamily: 'Poppins',
                width: '144px',
                py: '10px',
                px: '10px',
                fontSize: '11px',
                textTransform: 'none',
                boxShadow: 0,
              }}>
              {t(Localization('project-detail', 'new-project'))}
            </Button>
          </Stack>
        </Stack>
      </Stack>
      {/** Content will pop up when filter processing **/}
      {/*{arrayOfStatus.length > 0 && (*/}
      <Stack direction={'row'} justifyContent={'flex-start'} spacing={'10px'}>
        {/** ====================PopUp when checked on change status**/}
        {arrayOfStatus.length > 0 && (
          <Stack
            sx={{
              ...StyleConstant.box.statusAndDateExpiration,
              border: '1px solid ' + theme[0].mainColor,
            }}
            direction={'row'}>
            <NGText
              text={'Status'}
              myStyle={{...FigmaBody.BodySmallCaptionBold, fontWeight: 500}}
            />
            <Stack
              direction={'row'}
              spacing={1}
              justifyContent={'center'}
              alignItems={'center'}>
              {arrayOfStatus.map(status => {
                return (
                  <Chip
                    key={status}
                    size="small"
                    label={t(
                      Localization(
                        'project-detail',
                        status.toString().toLowerCase() as any,
                      ),
                    )}
                    sx={{
                      ...FigmaBody.BodySmallCaptionBold,
                      color: theme[0].mainColor,
                    }}
                    onDelete={handleDelete(status)}
                  />
                );
              })}
            </Stack>
            <NGFalseListProject
              sx={{
                width: '16px',
                height: '16px',
                color: 'White.main',
                bgcolor: 'grey',
                borderRadius: '50%',
                cursor: 'pointer',
                '&:hover': {
                  /** when we hover on row will color main Color for them and opacity 10 **/
                  backgroundColor: '#808080' + 70,
                },
              }}
              onClick={() => setArrayOfStatus([])}
            />
          </Stack>
        )}

        {/** ==============PopUp when checked on change date**/}
        {startEndDate.start !== null && (
          <Stack
            direction={'row'}
            justifyContent={'flex-start'}
            spacing={'10px'}>
            <Stack
              sx={{
                ...StyleConstant.box.statusAndDateExpiration,
                border: '1px solid ' + theme[0].mainColor,
              }}
              direction={'row'}>
              <NGText
                text={
                  select.active === 1
                    ? t(Localization('drop-down', 'period'))
                    : t(Localization('drop-down', 'precise-date'))
                }
                myStyle={{
                  ...FigmaBody.BodySmallCaptionBold,
                  fontWeight: 500,
                  color: 'Light.main',
                }}
              />

              {select.active === 0 ? (
                <NGText
                  text={
                    startDateFrench.day +
                    ' ' +
                    startDateFrench.month +
                    ' - ' +
                    startDateFrench.year
                  }
                  myStyle={{
                    ...FigmaBody.BodySmallCaptionBold,
                    color: theme[0].mainColor,
                  }}
                />
              ) : (
                <NGText
                  text={
                    startDateFrench.day +
                    ' ' +
                    startDateFrench.month +
                    ' - ' +
                    endDateFrench.day +
                    ' ' +
                    endDateFrench.month +
                    ' ' +
                    endDateFrench.year
                  }
                  myStyle={{
                    ...FigmaBody.BodySmallCaptionBold,
                    color: theme[0].mainColor,
                  }}
                />
              )}
              <NGFalseListProject
                sx={{
                  width: '16px',
                  height: '16px',
                  color: 'White.main',
                  bgcolor: 'grey',
                  borderRadius: '50%',
                  cursor: 'pointer',
                  '&:hover': {
                    /** when we hover on row will color main Color for them and opacity 10 **/
                    backgroundColor: '#808080' + 70,
                  },
                }}
                onClick={() =>
                  dispatch(
                    setStartEndDate({
                      start: null,
                      end: null,
                    }),
                  )
                }
              />
            </Stack>
          </Stack>
        )}

        {/** ==============PopUp when click on button add filter **/}

        <NGPopOver
          horizontal={'right'}
          horizontalT={'left'}
          verticalT={'top'}
          vertical={'top'}
          contain={
            <Stack spacing={2} p={2}>
              <Select
                sx={{
                  '& .MuiInputBase-input': {
                    width: '100%',
                    gap: '8px',
                    fontFamily: 'Poppins',
                    padding: '8px 12px 8px 10px',
                    fontSize: '14px',
                    fontWeight: 500,
                    borderColor: theme[0].mainColor,
                    '&:focus': {
                      borderColor: theme[0].mainColor,
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
                    sx={{
                      fontSize: '14px',
                      fontWeight: 500,
                      fontFamily: 'Poppins',
                    }}>
                    {item.project}
                  </MenuItem>
                ))}
              </Select>
              <Stack direction={'row'}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <CalendarPicker
                    disableHighlightToday={true}
                    date={startEndDate.start ?? dateStart}
                    onChange={newDate => {
                      setDateStart(newDate);
                      dispatch(
                        setStartEndDate({
                          start: newDate,
                          end: select.active === 0 ? null : startEndDate.end,
                        }),
                      );
                    }}
                  />
                </LocalizationProvider>
                {select.active === 1 && (
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <CalendarPicker
                      defaultCalendarMonth={startEndDate.end}
                      disableHighlightToday={true}
                      minDate={dateStart}
                      date={startEndDate.end ?? dateFinish}
                      onChange={newDate => {
                        const d = newDate!
                          .set('hour', 23)
                          .set('minute', 59)
                          .set('second', 59);
                        setDateFinish(d);
                        dispatch(
                          setStartEndDate({
                            start: startEndDate.start,
                            end: newDate,
                          }),
                        );
                      }}
                    />
                  </LocalizationProvider>
                )}
              </Stack>
            </Stack>
          }
          button={
            (arrayOfStatus?.length > 0 ||
              startEndDate.start !== null ||
              startEndDate.end !== null) && (
              <Stack
                direction={'row'}
                width={'110px'}
                height={'36px'}
                spacing={'2px'}
                sx={{cursor: 'pointer'}}
                justifyContent={'center'}
                alignItems={'center'}>
                <NGText
                  text={t(Localization('project-detail', 'add-filter'))}
                  myStyle={{...FigmaCTA.CtaSmall}}
                />
                <NGPlusIcon
                  sx={{width: '16px', height: '16px', color: 'Primary.main'}}
                />
              </Stack>
            )
          }
        />
      </Stack>

      <TableProjectDetail
        search={search}
        tab={tab}
        selectOption={selectOption}
        arrayOfStatus={arrayOfStatus}
      />

      <UploadForm popUp={popUp} closePopup={handleClosePopup} />
    </Stack>
  );
};
