import {NGPlus} from '@/assets/Icon';
import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import {
  LogoFileType,
  initialLogoFileType,
} from '@/components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import NGText from '@/components/ng-text/NGText';
import {FONT_TYPE, UNKOWNERROR} from '@/constant/NGContant';
import {FigmaCTA} from '@/constant/style/themFigma/CTA';
import {FigmaHeading} from '@/constant/style/themFigma/FigmaHeading';
import {Localization} from '@/i18n/lan';
import {ISuperOutletContext} from '@/pages/protect-route/Super.admin';
import {useAppSelector} from '@/redux/config/hooks';
import {
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
  Alert,
  AlertColor,
  Button,
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
import {t} from 'i18next';
import {closeSnackbar, enqueueSnackbar} from 'notistack';
import React from 'react';
import {NonUndefined, UseFormHandleSubmit, useForm} from 'react-hook-form';
import flags from 'react-phone-number-input/flags';
import {useOutletContext} from 'react-router-dom';
import CreateCompanyAccount, {
  ICreateCompanyAccount,
} from '../register/Company.register';
import Layout from './Layout';

const HomeSuper = () => {
  const {
    user: {username},
  } = useAppSelector(state => state.authentication);
  const [search, setSearch] = React.useState<string>('');
  const {setToggleForm, toggleForm} = useOutletContext<ISuperOutletContext>();
  const {data, error, isLoading, isFetching} = useGetCompaniesQuery(search);

  const [logo, setLogo] = React.useState<LogoFileType>(initialLogoFileType);
  const [addLoading, setAddLoading] = React.useState<boolean>(false);
  const [companyExist, setCompanyExist] = React.useState<JSX.Element | null>(
    null,
  );
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

  const {
    control,
    formState: {errors},
    reset,
    watch,
    handleSubmit,
  } = useForm({
    defaultValues: {
      companyName: '',
      siretNumber: '',
      contactFirstName: '',
      contactLastName: '',
      address: '',
      email: '',
      fixNumber: '',
      phoneNumber: '',
    },
  });
  const companyName = watch('companyName');
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
  };

  const handleFixPhone = (data: string) => {
    const res = data.split(' ');
    setCountryFixNumber({
      name: res[0],
      callingCode: res[1],
      code: res[2] as CountryCode,
    });
  };

  const alertConsole = (color: AlertColor, message: string) => {
    return (
      <Alert sx={{bgcolor: 'transparent'}} severity={color}>
        {message}
      </Alert>
    );
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
        setCompanyExist(null);
        setToggleForm(false);
        reset();
      }
    } catch (error) {
      setCompanyExist(
        alertConsole(
          'error',
          (error as any)?.data?.error?.message ?? UNKOWNERROR,
        ),
      );
    } finally {
      setAddLoading(false);
    }
  };

  React.useEffect(() => {
    reset();
  }, [toggleForm]);

  const handleSearch = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ): void => {
    setSearch(e.target.value);
  };

  /** Companies get query error */
  React.useEffect(() => {
    if (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }

    return () => closeSnackbar();
  }, [error]);

  return (
    <Layout>
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
            value={search}
            onChange={handleSearch}
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
          overflowY: 'auto',
        }}>
        <Grid container spacing="24px">
          {isLoading || isFetching
            ? Array.from({length: 2}, (_, index) => {
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
            : (data ? data.contents : []).map(item => {
                return (
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
                          src={
                            item.logo ? viewImage(item.logo) : './favico.ico'
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
                            Localization('superadmin-dashboard', 'users-count'),
                            {
                              num: item.totalEmployees,
                            },
                          )}
                        </Typography>
                      </Stack>
                    </Stack>
                  </Grid>
                );
              })}
        </Grid>
      </Stack>

      <CreateForm
        addLoading={addLoading}
        companyExist={companyExist}
        companyName={companyName}
        control={control}
        country={country}
        countryFixNumber={countryFixNumber}
        fixNumber={countryFixNumber}
        flags={flags}
        handleClose={handleClose}
        handleFixPhone={handleFixPhone}
        handlePhone={handlePhone}
        handleSubmit={handleSubmit}
        logo={logo}
        onSubmit={onSubmit}
        selectToggle={selectToggle}
        setCompanyExist={setCompanyExist}
        setLogo={setLogo}
        errors={errors}
        setToggle={setToggle}
      />
    </Layout>
  );
};

type ICreateForm = Omit<
  ICreateCompanyAccount,
  'toggleForm' | 'exist' | 'setExist'
> & {
  setCompanyExist: React.Dispatch<React.SetStateAction<JSX.Element | null>>;
  companyExist: JSX.Element | null;
  countryFixNumber: ICountry;
  onSubmit: (data: any) => Promise<void>;
  handleSubmit: UseFormHandleSubmit<{[x: string]: any}>;
  companyName: string;
};

const CreateForm = (props: ICreateForm) => {
  const {
    addLoading,
    control,
    country,
    flags,
    handleClose,
    handleFixPhone,
    handlePhone,
    logo,
    selectToggle,
    setLogo,
    setToggle,
    errors,
    companyExist,
    setCompanyExist,
    countryFixNumber,
    onSubmit,
    handleSubmit,
    companyName,
  } = props;
  const {toggleForm, setToggleForm} = useOutletContext<ISuperOutletContext>();
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
          text={t(Localization('superadmin-dashboard', 'add-a-company'))!}
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
          exist={companyExist}
          setExist={setCompanyExist}
          logo={logo}
          setLogo={setLogo}
          control={control}
          errors={errors}
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
            disabled={!companyName}
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
