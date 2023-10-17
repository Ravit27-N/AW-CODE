import {NGPlus} from '@/assets/Icon';
import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import {
  initialLogoFileType,
  LogoFileType,
} from '@/components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import NGText from '@/components/ng-text/NGText';
import {FONT_TYPE, STOP_TYPING_TIMEOUT, UNKOWNERROR} from '@/constant/NGContant';
import {FigmaCTA} from '@/constant/style/themFigma/CTA';
import {FigmaHeading} from '@/constant/style/themFigma/FigmaHeading';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {
  Content,
  ICreateCompany,
  useCreateCompanyMutation,
  useGetCompaniesQuery,
  useUploadLogoMutation,
} from '@/redux/slides/company/query/companyRTK';
import {HandleException} from '@/utils/common/HandleException';
import {viewImage} from '@/utils/common/ViewImage';
import {pixelToRem} from '@/utils/common/pxToRem';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import ClearIcon from '@mui/icons-material/Clear';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  IconButton,
  InputAdornment,
  Skeleton,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {GridSearchIcon} from '@mui/x-data-grid';
import CreateCompanyAccount, {
  companyFormDefaultValues,
  ICreateCompanyAccount,
  ICreateCompanyForm,
} from '@pages/super-admin/register/Company.register';
import {exists, t} from 'i18next';
import {closeSnackbar, enqueueSnackbar} from 'notistack';
import React from 'react';
import {
  FormProvider,
  NonUndefined,
  useForm,
  useFormContext,
} from 'react-hook-form';
import flags from 'react-phone-number-input/flags';
import {NavLink, useOutletContext} from 'react-router-dom';
import {Waypoint} from 'react-waypoint';
import {StyleConstant} from '@constant/style/StyleConstant';
import {debounce} from "lodash";

export type ISuperOutletContext = {
  setToggleForm: React.Dispatch<React.SetStateAction<boolean>>;
  toggleForm: boolean;
};

export const HomeSuper = () => {
  const {
    user: {username},
  } = useAppSelector(state => state.authentication);
  const [filters,setFilters]=React.useState<{search:string,page:number,pageSize:number}>({
    page:1,
    pageSize:20,
    search:''
  })
  const {setToggleForm, toggleForm} = useOutletContext<ISuperOutletContext>();
  
  const [table, setTable] = React.useState<Array<Content>>([]);
  const {currentData, error, isLoading, isFetching} =
    useGetCompaniesQuery(filters);
  React.useMemo(() => {
    if (currentData) {
      const newMap: Array<Content> = currentData.contents.map(item => {
        return {
          addressLine1: item.addressLine1,
          addressLine2: item.addressLine2,
          archiving: item.archiving,
          city: item.city,
          contactFirstName: item.contactFirstName,
          contactLastName: item.contactLastName,
          country: item.country,
          email: item.email,
          fixNumber: item.fixNumber,
          mobile: item.mobile,
          postalCode: item.postalCode,
          state: item.state,
          territory: item.territory,
          totalEmployees: item.totalEmployees,
          id: item.id,
          name: item.name,
          siret: item.siret,
          createdBy: item.createdBy,
          logo: item.logo,
          modifiedBy: item.modifiedBy,
          createdAt: item.createdAt,
          modifiedAt: item.modifiedAt,
          uuid: item.uuid,
        };
      });
      let result: Array<Content> = newMap;
      if (newMap.length > 1) {
        result = newMap.filter(obj1 =>
          table?.every(obj2 => obj1.id !== obj2.id),
        );
      }
      if (filters.search) {
        setTable(result);
      } else {
        setTable(visibleRows => visibleRows.concat(result));
      }
    }
    return [];
  }, [currentData]);

  const [logo, setLogo] = React.useState<LogoFileType>(initialLogoFileType);
  const [addLoading, setAddLoading] = React.useState<boolean>(false);
  const [selectToggle, setToggle] = React.useState({
    phoneNumber: false,
    fixNumber: false,
  });
  const [country, setCountry] = React.useState<ICountry>({
    callingCode: '33',
    name: 'France',
    code: 'FR',
  });
  const [countryFixNumber, setCountryFixNumber] = React.useState<ICountry>({
    callingCode: '33',
    name: 'France',
    code: 'FR',
  });

  const [createCompany] = useCreateCompanyMutation({});

  const [uploadLogo] = useUploadLogoMutation({});

  const methods = useForm({
    defaultValues: companyFormDefaultValues,
  });
  const {control, setError, reset} = methods;
  const handleClose = () => {
    setToggle({phoneNumber: false, fixNumber: false});
  };
  const handlePhone = (data: string) => {
    const res = data.split(' ');
    setCountry({
      name: res[0],
      callingCode: res[1],
      code: res[2] as CountryCode,
    });
    setError(`phoneNumber`, {message: undefined});
  };

  const handleFixPhone = (data: string) => {
    const res = data.split(' ');
    setCountryFixNumber({
      name: res[0],
      callingCode: res[1],
      code: res[2] as CountryCode,
    });
    setError(`fixNumber`, {message: undefined});
  };

  const onSubmit = async (
    data: NonUndefined<typeof control._defaultValues>,
  ) => {
    try {
      setAddLoading(true);
      const {
        companyName,
        siretNumber,
        contactFirstName,
        contactLastName,
        phoneNumber,
        address,
        email,
        fixNumber,
      } = data;
      if (
        companyName &&
        siretNumber &&
        contactFirstName &&
        contactLastName &&
        phoneNumber
      ) {
        const payload: ICreateCompany = {
          contactFirstName,
          contactLastName,
          mobile: `+${country.callingCode}${phoneNumber}`,
          siret: siretNumber,
          name: companyName,
          fixNumber: `+${countryFixNumber.callingCode}${fixNumber}`,
          email,
          addressLine1: address,
          archiving: true,
        };
        if (logo.file) {
          const tempLogoFile = new FormData();
          tempLogoFile.append('logoFile', logo.file);
          const logoResponse = await uploadLogo(tempLogoFile).unwrap();
          if (logoResponse.fileName) {
            payload.logo = logoResponse.fileName;
          }
        }

        const companyResponse = await createCompany(payload).unwrap();

        enqueueSnackbar(
          `${t(Localization('company-form', 'add-success-message'), {
            name: companyResponse.name,
          })}`,
          {
            variant: 'successSnackbar',
          },
        );

        setLogo(initialLogoFileType);
        setToggleForm(false);
        reset();
      }
    } catch (error) {
      const key = Localization(
        'company-form',
        (error as any)?.data?.error?.key,
      );

      const errorMessage = exists(key) ? t(key) : UNKOWNERROR;
      enqueueSnackbar(errorMessage, {
        variant: 'errorSnackbar',
      });
    } finally {
      setAddLoading(false);
    }
  };

  React.useEffect(() => {
    reset();
  }, [toggleForm]);

  const handleSearch = React.useCallback(
      debounce((search: string) => {
        setFilters({...filters,page:1,search})
      }, STOP_TYPING_TIMEOUT),
      [],
  );

  /** Companies get query error */
  React.useEffect(() => {
    if (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
    return () => closeSnackbar();
  }, [error]);
  const gotoNextPage = ({currentPosition}: Waypoint.CallbackArgs) => {
    if (currentPosition === 'inside') {
      if (!currentData?.hasNext) {
        return;
      }
      setFilters({...filters,page:filters.page+1})
    }
  };
  return (
    <>
      {/* Header */}
      <Stack
        height="156px"
        sx={{
          backgroundImage: `url(${bgLogo})`,
          backgroundSize: 'cover',
          borderBottom: '1px solid #E9E9E9',
        }}
        p="40px 72px"
        direction="row"
        justifyContent="space-between">
        <Stack gap="12px">
          <Typography
            sx={{
              fontWeight: 600,
              fontFamily: FONT_TYPE.POPPINS,
              fontSize: 22,
              color: '#676767',
            }}>
            {`${t(Localization('superadmin-dashboard', 'welcome'), {
              name: username,
            })},`}
          </Typography>

          <Typography
            sx={{
              fontWeight: 600,
              fontFamily: FONT_TYPE.POPPINS,
              fontSize: 22,
            }}>
            {t(
              Localization('superadmin-dashboard', 'select-company-to-manage'),
            )}
          </Typography>
        </Stack>

        <Stack direction="row" gap="16px" alignItems="center">
          <TextField
            size={'small'}
            placeholder={
              t(Localization('models-corporate', 'search-for-model'))!
            }
            sx={{
              width: '302px',
              justifyContent: 'center',
              border: 'none',
              bgcolor: '#fff',
            }}
            type="search"
            onChange={event => {
              handleSearch(event.target.value);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <GridSearchIcon
                    sx={{
                      width: '20px',
                    }}
                  />
                </InputAdornment>
              ),
            }}
          />
          <Button
            onClick={() => setToggleForm(true)}
            variant="contained"
            sx={{
              fontFamily: FONT_TYPE.POPPINS,
              height: '40px',
              textTransform: 'none',
              fontWeight: 600,
              fontSize: 11,
              borderRadius: '6px',
            }}
            endIcon={
              <NGPlus
                sx={{
                  width: '12px',
                  height: '12px',
                }}
              />
            }>
            {t(Localization('superadmin-dashboard', 'add-a-company'))}
          </Button>
        </Stack>
      </Stack>

      <br />

      {/* Content / Main */}
      <Stack
        p="0 40px"
        height="calc(100vh - 238px)"
        sx={{
          overflow: 'hidden',
          // overflowY: 'auto',
        }}>
        <Grid container spacing="18px">
          {isLoading && isFetching ? (
            Array.from({length: 2}, (_, index) => {
              return (
                <Grid item xs={6} key={index}>
                  <Skeleton
                    variant="rectangular"
                    height="120px"
                    sx={{
                      borderRadius: '16px',
                    }}
                  />
                </Grid>
              );
            })
          ) : (
            <>
              <Grid
                item
                container
                lg={12}
                spacing={2}
                sx={{
                  height: `calc(100vh - 200px)`, // 
                  width: '100%',
                  overflow: 'scroll',
                  overflowX: 'hidden',
                  pr: '60px',
                  ...StyleConstant.scrollNormal,
                }}>
                <>
                  {table?.map(item => (
                    <Grid
                      item
                      xs={12}
                      sm={12}
                      md={12}
                      lg={6}
                      key={item.id}
                      sx={{
                        cursor: 'pointer',
                      }}>
                     
                      <NavLink
                        style={{
                          textDecoration: 'none',
                          color: '#000000',
                        }}
                        to={{
                          pathname: `/super-admin/${item.uuid}`,
                        }}>
                        <Stack
                          direction="row"
                          gap="14px"
                          height="120px"
                          borderRadius="16px"
                          border="1px solid #E9E9E9"
                          px="24px"
                          alignItems="center">
                          <Stack p="24px" bgcolor="#FAFAFA" borderRadius="50%">
                            <img
                              alt={'logo'}
                              src={
                                item.logo
                                  ? viewImage(item.logo)
                                  : './favico.ico'
                              }
                              style={{
                                width: '40px',
                                height: '40px',
                              }}
                            />
                          </Stack>
                          <Stack gap="8px" height="60px">
                            <Typography
                              sx={{
                                fontWeight: 600,
                                fontFamily: FONT_TYPE.POPPINS,
                                fontSize: 22,
                              }}>
                              {item.name}
                            </Typography>
                            <Typography
                              sx={{
                                fontFamily: FONT_TYPE.POPPINS,
                                fontSize: 16,
                                color: '#767676',
                              }}>
                              {t(
                                Localization(
                                  'superadmin-dashboard',
                                  'users-count',
                                ),
                                {
                                  num: item.totalEmployees,
                                },
                              )}
                            </Typography>
                          </Stack>
                        </Stack>
                      </NavLink>
                    </Grid>
                  ))}
                </>
                <Waypoint onEnter={gotoNextPage} />
                <Stack py={2} alignItems="center">
                  {isFetching && <CircularProgress color="primary" size={25} />}
                </Stack>
              </Grid>
            </>
          )}
        </Grid>
      </Stack>

      <FormProvider {...methods}>
        <CreateForm
          addLoading={addLoading}
          country={country}
          countryFixNumber={countryFixNumber}
          fixNumber={countryFixNumber}
          flags={flags}
          handleClose={handleClose}
          handleFixPhone={handleFixPhone}
          handlePhone={handlePhone}
          logo={logo}
          onSubmit={onSubmit}
          selectToggle={selectToggle}
          setLogo={setLogo}
          setToggle={setToggle}
        />
      </FormProvider>
    </>
  );
};

type ICreateForm = Omit<
  ICreateCompanyAccount,
  'toggleForm' | 'exist' | 'setExist'
> & {
  countryFixNumber: ICountry;
  onSubmit: (data: any) => Promise<void>;
};

const CreateForm = (props: ICreateForm) => {
  const {
    addLoading,
    country,
    flags,
    handleClose,
    handleFixPhone,
    handlePhone,
    logo,
    selectToggle,
    setLogo,
    setToggle,
    countryFixNumber,
    onSubmit,
  } = props;
  const {
    handleSubmit,
    formState: {dirtyFields, errors},
  } = useFormContext<ICreateCompanyForm>();
  const {toggleForm, setToggleForm} = useOutletContext<ISuperOutletContext>();

  const requiredFields: (keyof ICreateCompanyForm)[] = [
    'companyName',
    'siretNumber',
    'phoneNumber',
    'contactFirstName',
    'contactLastName',
  ];

  const requiredFieldsInvalid = !requiredFields.every(item => {
    return dirtyFields[item];
  });

  return (
    <Dialog
      open={toggleForm}
      sx={{
        '& .MuiPaper-root': {
          boxSizing: 'border-box',
          borderRadius: theme => theme.shape.borderRadius,
        },
      }}
      fullWidth={true}
      maxWidth={'md'}>
      <DialogTitle
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
        }}>
        <NGText
          text={t(Localization('superadmin-dashboard', 'add-a-company'))}
          iconStart={<NGCirclePlus color="primary" sx={{fontSize: 25}} />}
          myStyle={{...FigmaHeading.H4}}
        />
        <IconButton
          size="small"
          onClick={() => {
            setLogo(initialLogoFileType);
            return setToggleForm(false);
          }}>
          <ClearIcon color="primary" />
        </IconButton>
      </DialogTitle>
      <Divider />
      <DialogContent
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          bgcolor: 'bg.main',
        }}>
        <CreateCompanyAccount
          addLoading={addLoading}
          logo={logo}
          setLogo={setLogo}
          country={country}
          fixNumber={countryFixNumber}
          flags={flags}
          handleClose={handleClose}
          handlePhone={handlePhone}
          selectToggle={selectToggle}
          setToggle={setToggle}
          handleFixPhone={handleFixPhone}
        />
      </DialogContent>
      <DialogActions>
        <Stack
          direction={'row'}
          alignItems={'center'}
          justifyContent={'flex-end'}
          sx={{
            width: '90%',
            px: 2,
            gap: 2,
          }}>
          <Button
            sx={{
              ...FigmaCTA.CtaSmall,
              textTransform: 'capitalize',
              py: pixelToRem(8),
              px: pixelToRem(16),
            }}
            onClick={() => {
              setLogo(initialLogoFileType);
              return setToggleForm(!toggleForm);
            }}>
            {t(Localization('superadmin-dashboard', 'cancel'))!}
          </Button>
          <Button
            disabled={
              requiredFieldsInvalid ||
              !logo.file ||
              Object.keys(errors).length > 0
            }
            onClick={handleSubmit(onSubmit)}
            sx={{
              ...FigmaCTA.CtaSmall,
              textTransform: 'capitalize',
              py: pixelToRem(8),
              px: pixelToRem(16),
            }}
            variant="contained"
            size="small">
            {t(Localization('superadmin-dashboard', 'add'))!}
          </Button>
        </Stack>
      </DialogActions>
    </Dialog>
  );
};

export default HomeSuper;
