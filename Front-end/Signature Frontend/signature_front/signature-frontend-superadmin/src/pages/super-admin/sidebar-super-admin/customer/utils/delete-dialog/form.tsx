import {NGBinCycleFill} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {
  IGetUsersContent,
  useDeleteUserMutation,
  useLazyGetUsersQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
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
import {Controller, NonUndefined, useFormContext} from 'react-hook-form';

import {STOP_TYPING_TIMEOUT, UNKOWNERROR} from '@/constant/NGContant';
import Autocomplete from '@mui/material/Autocomplete';
import {$isarray} from '@/utils/request/common/type';
import {useSnackbar} from 'notistack';
import {DeleteCorporateAdmin} from '.';
import {useParams} from 'react-router-dom';
import {FigmaHeading} from '@/constant/style/themFigma/FigmaHeading';
import {FigmaBody} from '@/constant/style/themFigma/Body';

const DeleteCorporateUserForm = ({
  data,
  onClose,
  onDeleteSuccess,
}: {
  onClose: () => void;
  data: IGetUsersContent;
  onDeleteSuccess?: (data: IGetUsersContent) => void;
}): JSX.Element => {
  const {uuid} = useParams();
  const {enqueueSnackbar} = useSnackbar();
  const [search, setSearch] = React.useState('');
  const [check, setCheck] = React.useState<boolean>(false);
  const [trigger, result] = useLazyGetUsersQuery();
  const [deleteUser, deleteResult] = useDeleteUserMutation();

  useEffect(() => {
    const handleFetch = async (uuidProp: string) => {
      await trigger({
        page: 1,
        pageSize: 15,
        search,
        sortDirection: 'desc',
        sortField: 'id',
        uuid: uuidProp,
      }).unwrap();
    };

    if (uuid) {
      handleFetch(uuid);
    }
  }, [search]);

  const handleSearchDebounce = React.useCallback(
    debounce((search: string) => {
      setSearch(search);
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  const {reset, control, handleSubmit, getValues} =
    useFormContext<DeleteCorporateAdmin>();

  // handle delete user
  const onSubmit = async (
    formData: NonUndefined<typeof control._defaultValues>,
  ) => {
    try {
      await deleteUser({
        id: data.id,
        assignTo: formData?.assignTo?.id,
      });

      enqueueSnackbar(
        t(
          Localization(
            'super-admin-delete-corporate-user',
            'delete-successfully',
          ),
        ),
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
          <NGBinCycleFill color={'error'} />
        </Box>
        <Box width={'100%'} textAlign={'center'}>
          <NGText
            myStyle={{
              ...FigmaHeading.H3,
            }}
            text={t(
              Localization(
                'super-admin-delete-corporate-user',
                'delete-corporate-admin',
              ),
            )}
          />
          <br />
          <NGText
            myStyle={{
              ...FigmaHeading.H3,
            }}
            text={`${data.firstName} ${data.lastName}`}
          />
        </Box>
        <Box width={'100%'} textAlign={'center'}>
          <NGText
            myStyle={{...FigmaBody.BodyMedium}}
            text={t(
              Localization(
                'super-admin-delete-corporate-user',
                'delete-waring',
              ),
            )}
          />
          <br />
          <NGText
            myStyle={{...FigmaBody.BodyMedium}}
            text={t(
              Localization('super-admin-delete-corporate-user', 'helper-text'),
            )}
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
            borderColor: check ? 'Primary.main' : '#00000030',
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
                myStyle={{...FigmaBody.BodyMedium}}
                text={t(
                  Localization(
                    'super-admin-delete-corporate-user',
                    'assign-user',
                  ),
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
            // defaultValue={null}
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
                    return onChange(data);
                  }}
                  renderInput={params => (
                    <TextField
                      required={check}
                      {...params}
                      placeholder={
                        t(
                          Localization(
                            'super-admin-delete-corporate-user',
                            'select-a-user',
                          ),
                        )!
                      }
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
            title={t(
              Localization('super-admin-delete-corporate-user', 'cancel'),
            )}
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
              Localization('super-admin-delete-corporate-user', 'submit'),
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

export default DeleteCorporateUserForm;
