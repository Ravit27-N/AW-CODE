import {FONT_TYPE, STOP_TYPING_TIMEOUT} from '@/constant/NGContant';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {
  IGetUsersContent,
  IGetUsersQuery,
  useGetUsersQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {Center} from '@/theme';
import {pixelToRem} from '@/utils/common/pxToRem';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import {
  NGCirclePlus,
  NGDownload,
  NGExport,
  NGUser,
} from '@assets/iconExport/Allicon';
import {NGButton} from '@components/ng-button/NGButton';
import {useOpen} from '@components/ng-popover/hook/useOpen';
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
import NGTableUser from '@pages/corporate-admin/sidebar-corporate/customer/table/resource/NGTableUser';
import {AddUser} from '@pages/corporate-admin/sidebar-corporate/customer/utils/AddUser';
import {DeleteUser} from '@pages/corporate-admin/sidebar-corporate/customer/utils/DeleteUser';
import {debounce} from 'lodash';
import React from 'react';
import {useTranslation} from 'react-i18next';
import XLSX from 'xlsx';
import ImportCSV from './utils/AddUserCSV';
import {closeSnackbar} from 'notistack';
import {splitUserCompany} from '@/utils/common/String';
import {store} from '@/redux';
import {Waypoint} from 'react-waypoint';
import {FigmaCTA} from '@/constant/style/themFigma/CTA';

const GroupCorporate = () => {
  const {isOpen, onOpen, onClose} = useOpen();
  const [importCsvTrig, setImportCsvTri] = React.useState<boolean>(false);
  const {
    isOpen: isDeleteOpen,
    onOpen: onDeleteOpen,
    onClose: onDeleteClose,
  } = useOpen();

  const [userCount, setUserCount] = React.useState(0);
  const [selectedUser, setSelectedUser] =
    React.useState<IGetUsersContent | null>(null);
  const [currData, setCurrData] = React.useState<Array<IGetUsersContent>>([]);
  const [search, setSearch] = React.useState('');
  const {theme} = useAppSelector(state => state.enterprise);
  const xlUp = useMediaQuery(`(min-width:1441px)`);
  const activeColor = theme[0].mainColor;
  const {t} = useTranslation();
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [userFilter, setUserFilter] = React.useState<IGetUsersQuery>({
    page: 1,
    pageSize: 20,
    sortDirection: 'desc',
    sortField: 'id',
    companyId: Number(company.companyId),
    search: '',
  });
  const {data, isLoading, currentData} = useGetUsersQuery(userFilter);

  const handleExportCSV = () => {
    const workbook = XLSX.utils.book_new();
    const rows = currData.map(data => {
      // clear up data
      return {
        familyName: data.lastName,
        firstName: data.firstName,
        role: data.functional,
        team: data.department?.unitName,
        accessRight: data.userAccess?.name,
      };
    });
    const worksheet = XLSX.utils.json_to_sheet(rows);
    XLSX.utils.sheet_add_aoa(
      worksheet,
      [['Family name', 'First name', 'Role', 'Team', 'Access right']],
      {origin: 'A1'},
    );
    XLSX.utils.book_append_sheet(workbook, worksheet, 'END_USERS');
    XLSX.writeFile(workbook, 'end-users.csv', {compression: true});
  };

  const handleSearchDebounce = React.useCallback(
    debounce((search: string) => {
      setSearch(search);
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  React.useEffect(() => {
    return () => {
      // automatically close all snackbar when exit page
      closeSnackbar();
    };
  }, []);

  React.useEffect(() => {
    if (currentData) {
      const {contents, total} = currentData;
      setUserCount(total);
      if (userFilter.page <= 1) {
        // reset select
        setSelected([]);
        // don't append data if it is the first page
        setCurrData(contents);
        return;
      }

      setCurrData([...currData, ...contents]);
    }
  }, [
    userFilter.page,
    currentData,
    userFilter.search,
    userFilter.sortDirection,
    userFilter.sortField,
  ]);

  React.useEffect(() => {
    setUserFilter({...userFilter, search, page: 1});
  }, [search]);

  const handleLastItem = ({currentPosition}: Waypoint.CallbackArgs) => {
    if (currentPosition === 'inside') {
      setUserFilter({...userFilter, page: userFilter.page + 1});
    }
  };

  const handleRequestSort = (
    event: React.MouseEvent<unknown>,
    property: IGetUsersQuery['sortField'],
  ) => {
    const isAsc =
      userFilter.sortField === property && userFilter.sortDirection === 'asc';
    setUserFilter({
      ...userFilter,
      page: 1,
      sortDirection: isAsc ? 'desc' : 'asc',
      sortField: property,
    });
  };

  const handleSelectAllCheckbox = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    if (event.target.checked) {
      const newSelected = currData.map(n => n.id.toString());
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleSelectCheckbox = (
    event: React.MouseEvent<unknown>,
    name: string,
  ) => {
    const selectedIndex = selected.indexOf(name);
    let newSelected: readonly string[] = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, name);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1),
      );
    }
  };

  return (
    <Stack width={'100%'} bgcolor={'white'}>
      {/*=====================background Image ======================*/}
      <Box
        sx={{
          height: pixelToRem(156),
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
          py: {md: 2, lg: 5},
          px: '72px',
          position: 'relative',
        }}>
        <Stack gap={pixelToRem(12)}>
          <NGText
            text={t(Localization('superadmin-dashboard', 'users'))}
            myStyle={{
              fontSize: pixelToRem(27),
              fontWeight: 600,
              lineHeight: pixelToRem(36),
              textTransform: 'capitalize',
            }}
          />
          <NGText
            text={`${userCount} ${t(
              Localization('superadmin-dashboard', 'users'),
            )}`}
            iconStart={<NGUser sx={{color: activeColor}} />}
            myStyle={{
              fontSize: pixelToRem(16),
              fontWeight: 400,
              lineHeight: pixelToRem(28),
            }}
          />
        </Stack>
      </Box>
      {/*  ====================================== Container */}
      <Stack
        justifyContent={'center'}
        alignItems={'center'}
        px={{xs: 3, lg: 7, xl: 9}}>
        {/*===================== Container under background Image ======================*/}
        <Box width="100%" p="24px 0 20px 0">
          <Grid container spacing={{lg: 1, xs: 2}}>
            {/*  Left hand*/}
            <Grid item xl={5} lg={4} xs={12}>
              {/*  Search input */}
              <TextField
                size={'small'}
                placeholder={t(Localization('enterprise-services', 'search'))!}
                type="search"
                onChange={(event: any) => {
                  handleSearchDebounce(event.target.value);
                }}
                fullWidth
                sx={{
                  ...StyleConstant.inputStyleLogin,
                }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start" sx={{mr: 1}}>
                      <SearchIcon />
                    </InputAdornment>
                  ),
                  sx: {fontFamily: FONT_TYPE.POPPINS, fontSize: 14},
                }}
              />
            </Grid>
            {/*  Right hand*/}
            <Grid item xl={7} lg={8} xs={12}>
              <Stack
                direction={'row'}
                spacing={{xs: 1, xl: 2}}
                justifyContent={'flex-end'}>
                {/*  ======================================== Button 2*/}
                <NGButton
                  onClick={() => setImportCsvTri(true)}
                  title={
                    <NGText
                      text={t(
                        Localization('enterprise-dashboard', 'import-users'),
                      )}
                      myStyle={{
                        ...FigmaCTA.CtaSmall,
                      }}
                    />
                  }
                  variant={'outlined'}
                  bgColor={'black'}
                  myStyle={{
                    border: 1,
                    borderRadius: pixelToRem(6),
                    alignSelf: 'center',
                    pr: 0.5,
                    alignItems: 'flex-start',
                  }}
                  color={['grey', 'black.main']}
                  icon={
                    <NGDownload
                      sx={{
                        color: 'black.main',
                        ml: pixelToRem(8),
                        alignSelf: 'center',
                      }}
                    />
                  }
                  locationIcon={'end'}
                />

                <NGButton
                  onClick={() => handleExportCSV()}
                  title={
                    <NGText
                      text={t(
                        Localization('enterprise-dashboard', 'export-list'),
                      )}
                      myStyle={{
                        ...FigmaCTA.CtaSmall,
                      }}
                    />
                  }
                  variant={'outlined'}
                  bgColor={'black'}
                  myStyle={{
                    border: 1,
                    borderRadius: pixelToRem(6),
                    alignSelf: 'center',
                    pr: 0.5,
                    alignItems: 'flex-start',
                  }}
                  color={['grey', 'black.main']}
                  icon={
                    <NGExport
                      sx={{
                        color: 'black.main',
                        ml: pixelToRem(8),
                        alignSelf: 'center',
                      }}
                    />
                  }
                  locationIcon={'end'}
                />
                {/*  ======================================== Button 4*/}
                <NGButton
                  onClick={onOpen}
                  myStyle={{borderRadius: pixelToRem(6), alignSelf: 'center'}}
                  title={
                    <NGText
                      text={t(
                        Localization('enterprise-dashboard', 'add-a-user'),
                      )}
                      iconStart={
                        <Center height={'100%'}>
                          <NGCirclePlus
                            fontSize="small"
                            sx={{
                              cursor: 'pointer',
                              color: 'white',
                              mr: 1,
                              fontSize: pixelToRem(16),
                            }}
                          />
                        </Center>
                      }
                      myStyle={{
                        ...FigmaCTA.CtaSmall,
                        color: 'white',
                        py: 0.2,
                      }}
                    />
                  }
                />
              </Stack>
            </Grid>
          </Grid>
        </Box>
        {/*    =========================================== Table */}
        <Box width={'100%'}>
          <NGTableUser
            isLoading={isLoading}
            selected={selected}
            userList={currData}
            userFilter={userFilter}
            hasNext={data?.hasNext ?? false}
            Sx={{
              maxHeight: xlUp ? `calc(100vh - 324px)` : '556px',
            }}
            onActionOpen={data => {
              setSelectedUser(data);
            }}
            onActionClose={() => {
              // clear up data when action popup close
              setSelectedUser(null);
            }}
            onDelete={() => {
              onDeleteOpen();
            }}
            onSelectCheckbox={handleSelectCheckbox}
            onSelectAllCheckbox={handleSelectAllCheckbox}
            onLastItem={handleLastItem}
            onRequestSort={handleRequestSort}
          />
        </Box>
      </Stack>

      <AddUser
        open={isOpen}
        onClose={onClose}
        onAddSuccess={() => setUserFilter({...userFilter, page: 1})}
      />

      {selectedUser && (
        <DeleteUser
          open={isDeleteOpen}
          onClose={() => {
            // clear up data when delete popup close
            setSelectedUser(null);

            onDeleteClose();
          }}
          onDeleteSuccess={data => {
            // should replace filter with something else if we want to increase performance
            setCurrData(currData.filter(item => item.id !== data.id));
          }}
          data={selectedUser}
        />
      )}
      <ImportCSV trigger={importCsvTrig} setTrigger={setImportCsvTri} />
    </Stack>
  );
};

export default GroupCorporate;
