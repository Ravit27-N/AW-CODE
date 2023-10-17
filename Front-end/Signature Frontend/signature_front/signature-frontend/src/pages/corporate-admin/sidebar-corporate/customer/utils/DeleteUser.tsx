import {NGBinCycleFill} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {
  IGetUsersContent,
  useDeleteEndUserMutation,
  useLazyGetUserByIdQuery,
  useLazyGetUsersQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {splitUserCompany} from '@/utils/common/String';
import {pixelToRem} from '@/utils/common/pxToRem';
import CloseIcon from '@mui/icons-material/Close';
import {
  Backdrop,
  Box,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  IconButton,
  Stack,
  TextField,
  debounce,
} from '@mui/material';
import {t} from 'i18next';
import React, {useEffect} from 'react';
import {
  Controller,
  FormProvider,
  NonUndefined,
  useForm,
  useFormContext,
} from 'react-hook-form';

import {STOP_TYPING_TIMEOUT, UNKOWNERROR} from '@/constant/NGContant';
import Autocomplete from '@mui/material/Autocomplete';
import {$isarray} from '@/utils/request/common/type';
import {useSnackbar} from 'notistack';

type IDeleteUser = {
  open: boolean;
  onClose: () => void;
  data: IGetUsersContent;
  onDeleteSuccess?: (data: IGetUsersContent) => void;
};

export type IDeleteCorporateUserForm = {
  assignTo: IGetUsersContent | null;
};

const defaultValues: IDeleteCorporateUserForm = {
  assignTo: null,
};

export const DeleteUser = ({
  open,
  onClose,
  data,
  onDeleteSuccess,
}: IDeleteUser) => {
  const methods = useForm({
    defaultValues,
  });
  const handleClose = () => {
    methods.reset();
    onClose();
  };
  return (
    <FormProvider {...methods}>
      <NGDialog
        maxWidth={'sm'}
        fullWidth
        open={open}
        sx={{
          '& .MuiPaper-root': {
            boxSizing: 'border-box',
            borderRadius: '16px',
          },
        }}
        sxProp={{
          titleSx: {
            borderRadius: '28px',
            p: '20px',
          },
          contentsSx: {
            p: 0,
          },
        }}
        contentDialog={
          <DeleteUserContent
            data={data}
            onClose={handleClose}
            onDeleteSuccess={onDeleteSuccess}
          />
        }
      />
    </FormProvider>
  );
};

const DeleteUserContent = ({
  data,
  onClose,
  onDeleteSuccess,
}: Omit<IDeleteUser, 'open'>): JSX.Element => {
  const {mainColor} = store.getState().enterprise.theme[0];
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const {enqueueSnackbar} = useSnackbar();
  /** ----------------------------------------------------------  */
  const [search, setSearch] = React.useState('');
  const [check, setCheck] = React.useState<boolean>(false);
  const [trigger, result] = useLazyGetUsersQuery();
  const [userTrigger, userResult] = useLazyGetUserByIdQuery();
  const [deleteEndUser, deleteResult] = useDeleteEndUserMutation();
  useEffect(() => {
    const handleFetch = async (companyId: number) => {
      await trigger({
        page: 1,
        pageSize: 15,
        search,
        sortDirection: 'desc',
        sortField: 'id',
        companyId,
      }).unwrap();
    };

    /** Waiting for they stop typing 1.5 seconds for searching **/
    if (company.companyId) {
      handleFetch(Number(company.companyId));
    }
  }, [search]);

  const handleSearchDebounce = React.useCallback(
    debounce((search: string) => {
      setSearch(search);
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  const {control, handleSubmit, reset, getValues} =
    useFormContext<IDeleteCorporateUserForm>();

  // handle delete user
  const onSubmit = async (
    formData: NonUndefined<typeof control._defaultValues>,
  ) => {
    try {
      await deleteEndUser({
        id: data.userId,
        assignTo: formData?.assignTo?.userId,
      });
      enqueueSnackbar(
        t(Localization('corporate-form-delete-user', 'delete-success-message')),
        {
          variant: 'successSnackbar',
        },
      );

      if (typeof onDeleteSuccess === 'function') {
        onDeleteSuccess(data);
      }

      onClose();
    } catch (error) {
      enqueueSnackbar((error as any)?.data?.error?.message ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const handleCheck = (event: React.ChangeEvent<HTMLInputElement>) => {
    reset();
    setSearch('');
    setCheck(event.target.checked);
  };

  const getOptions = () => {
    if (result.isFetching) {
      // don't display anything when fetching, to void cache data
      return [];
    }
    if ($isarray(result?.data?.contents)) {
      return result.data!.contents.filter(
        // don't show user that we plan to delete
        item => item.id !== data.id,
      );
    }
    return [];
  };

  return (
    <Stack
      sx={{border: '1px solid #E9E9E9', px: 6, py: 4}}
      alignItems={'center'}
      gap={1.5}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <form style={{width: '100%'}} onSubmit={handleSubmit(onSubmit)}>
        <IconButton
          onClick={onClose}
          disableTouchRipple
          disableFocusRipple
          disableRipple
          sx={{
            position: 'absolute',
            top: theme => theme.spacing(1.5),
            right: theme => theme.spacing(1.5),
          }}
          aria-label="delete">
          <CloseIcon color={'primary'} />
        </IconButton>
        <Box width={'100%'} textAlign={'center'}>
          <NGBinCycleFill color={'error'} sx={{fontSize: pixelToRem(30)}} />
        </Box>
        <Box width={'100%'} textAlign={'center'}>
          <NGText
            myStyle={{
              fontSize: pixelToRem(25),
              fontWeight: 'bold',
              textAlign: 'center',
            }}
            text={t(
              Localization('corporate-form-delete-user', 'delete-user-title'),
            )}
          />
          <br />
          <NGText
            myStyle={{
              fontSize: pixelToRem(25),
              fontWeight: 'bold',
              textAlign: 'center',
            }}
            text={`${data.firstName} ${data.lastName}`}
          />
        </Box>
        <Box width={'100%'} textAlign={'center'}>
          <NGText
            myStyle={{width: '100%', textAlign: 'center'}}
            text={t(
              Localization('corporate-form-delete-user', 'delete-waring'),
            )}
          />
          <br />
          <NGText
            myStyle={{width: '100%', textAlign: 'center'}}
            text={t(Localization('corporate-form-delete-user', 'helper-text'))}
          />
        </Box>
        <Box
          width={'100%'}
          px={1}
          py={0.5}
          my={2}
          borderRadius={2}
          sx={{
            borderWidth: 1,
            borderStyle: 'solid',
            borderColor: check ? mainColor : '#00000030',
          }}>
          <FormControlLabel
            control={
              <Checkbox
                disableRipple
                disableTouchRipple
                disableFocusRipple
                onChange={handleCheck}
                checked={check}
              />
            }
            label={
              <NGText
                text={t(
                  Localization('corporate-form-delete-user', 'assign-user'),
                )}
              />
            }
          />
        </Box>

        {/**========= Search and Select ==========**/}
        {check && (
          <Controller
            name="assignTo"
            control={control}
            defaultValue={defaultValues.assignTo}
            render={({field: {onChange, ...props}}) => {
              return (
                <Autocomplete
                  size="small"
                  autoHighlight
                  options={getOptions()}
                  loading={result.isFetching}
                  isOptionEqualToValue={(option, value) =>
                    option.id === value.id
                  }
                  onInputChange={(event, value) => {
                    handleSearchDebounce(value);
                  }}
                  filterOptions={options => {
                    // disable default filter
                    return options;
                  }}
                  getOptionLabel={option => {
                    const renderNameText = `${option.firstName ?? ''} ${
                      option.lastName ?? ''
                    }`;
                    return `${renderNameText} ${
                      option.email ? `(${option.email})` : ''
                    }`;
                  }}
                  renderOption={(props, option) => {
                    return (
                      <li {...props}>
                        {option.firstName} {option.lastName}
                      </li>
                    );
                  }}
                  onChange={async (e, data) => {
                    // check and get user email
                    if (data?.userId) {
                      const userResponse = await userTrigger({
                        id: data.userId,
                      }).unwrap();
                      if (userResponse.email) {
                        return onChange({...data, email: userResponse.email});
                      }
                    }

                    return onChange(data);
                  }}
                  renderInput={params => (
                    <TextField
                      required={check}
                      {...params}
                      placeholder={
                        t(
                          Localization(
                            'corporate-form-delete-user',
                            'select-a-user',
                          ),
                        )!
                      }
                      InputProps={{
                        ...params.InputProps,
                        endAdornment:
                          // show loading when getting user email
                          userResult.isFetching ? (
                            <CircularProgress color="inherit" size={20} />
                          ) : (
                            params.InputProps.endAdornment
                          ),
                      }}
                    />
                  )}
                  {...props}
                />
              );
            }}
          />
        )}

        {/** ===================== Action Button ======================*/}
        <Box width={'100%'} display={'flex'} gap={1.5} mt={4}>
          <NGButton
            onClick={() => {
              if (check) {
                // if in the second step, reset state to default
                reset();
                setSearch('');
                setCheck(false);
                return;
              }

              // if in the first step close popup
              onClose();
            }}
            title={t(Localization('corporate-form-delete-user', 'cancel'))}
            variant="outlined"
            myStyle={{
              height: 45,
              width: '100%',
              borderRadius: '6px',
              border: '1px solid #000000',
            }}
            color={['#ffffff', '#000000']}
            btnProps={{
              disableFocusRipple: true,
              disableRipple: true,
              disableTouchRipple: true,
            }}
          />

          <NGButton
            title={t(
              Localization('corporate-form-delete-user', 'delete-user-button'),
            )}
            disabled={!getValues('assignTo') && check}
            type={'submit'}
            bgColor={'error'}
            myStyle={{
              height: 45,
              width: '100%',
              borderRadius: '6px',
              color: 'error',
            }}
            btnProps={{
              disableFocusRipple: true,
              disableRipple: true,
              disableTouchRipple: true,
            }}
          />
        </Box>
        <Backdrop
          sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
          open={deleteResult.isLoading}>
          <CircularProgress color="inherit" />
        </Backdrop>
      </form>
    </Stack>
  );
};
