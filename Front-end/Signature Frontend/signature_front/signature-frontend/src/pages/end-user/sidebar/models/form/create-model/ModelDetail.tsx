import {NGInputField} from '@/components/ng-input/NGInputField';
import NGText from '@/components/ng-text/NGText';
import {
  FONT_TYPE,
  STOP_TYPING_TIMEOUT,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {
  useCreateFolderMutation,
  CorporateModelFolder,
  IBusinessUnit,
  useLazyGetModelFolderByIdQuery,
  useGetModelFolderQuery,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  Autocomplete,
  Backdrop,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import {
  Controller,
  FormProvider,
  NonUndefined,
  useForm,
  useFormContext,
} from 'react-hook-form';
import {colorDisable} from '@constant/style/StyleConstant';
import {debounce} from 'lodash';
import {IPayloadDetail} from './CreateModel';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {useSnackbar} from 'notistack';
import CloseIcon from '@mui/icons-material/Close';
import {NGPlusIcon} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import {$isarray} from '@/utils/request/common/type';
import {useLazyGetEndUserProfileQuery} from '@/redux/slides/end-user/profileSlide';

type FolderCategoryForm = {
  unitName: string;
};

const defaultCategory: FolderCategoryForm = {
  unitName: '',
};

const ModelDetail = () => {
  const [openCategoryForm, setOpenCategoryForm] = React.useState(false);
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const [search, setSearch] = React.useState('');
  const [departments, setDepartments] = React.useState<IBusinessUnit[]>([]);
  React.useState<FolderCategoryForm>(defaultCategory);
  const {createModel} = store.getState().authentication;
  // const [options, setOptions] = React.useState<IServiceSelect[]>([]);
  const categoryMethods = useForm({
    defaultValues: defaultCategory,
  });
  const {reset: resetCategory} = categoryMethods;
  const {
    control,
    setValue,
    reset,
    formState: {errors},
  } = useFormContext<IPayloadDetail>();
  const {data: folderData, isFetching} = useGetModelFolderQuery({
    search,
  });

  const [getFolderById] = useLazyGetModelFolderByIdQuery();

  React.useEffect(() => {
    if (createModel) {
      const {businessUnitId, folderId, name} = createModel;
      const handleGetFolderById = async () => {
        const responseData = await getFolderById({id: folderId}).unwrap();
        setDepartments(
          responseData.businessUnits ? [responseData.businessUnits] : [],
        );
        reset({
          name,
          typeModel: responseData,
          department: businessUnitId.toString(),
        });
      };

      handleGetFolderById();
    } else {
      reset({name: '', typeModel: null, department: ''});
    }
  }, [createModel]);

  const handleSearchDebounce = React.useCallback(
    debounce((search: string) => {
      setSearch(search);
    }, STOP_TYPING_TIMEOUT),
    [],
  );

  const handleClose = () => {
    resetCategory();
    setOpenCategoryForm(false);
  };

  const getOptions = () => {
    if (isFetching) {
      // don't display anything when fetching, to void cache data
      return [];
    }
    if ($isarray(folderData?.contents)) {
      return folderData!.contents;
    }
    return [];
  };

  const onAddSuccess = (data: CorporateModelFolder) => {
    setDepartments(data.businessUnits ? [data.businessUnits] : []);
    setValue('typeModel', data);
    setValue('department', data?.businessUnitId?.toString());
  };
  return (
    <Stack p="60px 270px">
      <Card sx={{p: '40px', width: '100%'}}>
        <CardContent
          sx={{
            p: 0,
          }}>
          <Stack gap="40px">
            <Stack gap="6px">
              <Typography
                sx={{
                  fontSize: '18px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'model-detail'))}
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'model-information'))}
              </Typography>
            </Stack>

            <Stack gap="24px">
              {/* Model name field */}
              <Stack gap="6px">
                <Stack direction={'row'}>
                  <NGText
                    text={t(Localization('models-corporate', 'model-name'))}
                    myStyle={{fontSize: 13, fontWeight: 500}}
                  />

                  <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
                </Stack>
                <NGInputField<IPayloadDetail>
                  size="small"
                  sx={{fontSize: 15, minWidth: '15rem'}}
                  control={control}
                  eMessage={t(Localization('form-error', 'required-field'))!}
                  errorInput={errors.name ?? undefined}
                  typeInput={'name'}
                  type={'text'}
                  name={'name'}
                  placeholder={
                    t(Localization('models-corporate', 'model-name'))!
                  }
                  style={{fontFamily: FONT_TYPE.POPPINS}}
                />
              </Stack>

              {/* Model type field */}
              <Stack gap="6px">
                <Stack direction={'row'}>
                  <NGText
                    text={t(Localization('models-corporate', 'model-type'))}
                    myStyle={{fontSize: 13, fontWeight: 500}}
                  />

                  <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
                </Stack>
                <Controller
                  name={'typeModel'}
                  control={control}
                  rules={{required: true}}
                  render={({field: {onChange, ...props}}) => {
                    return (
                      <Autocomplete
                        size="small"
                        autoHighlight
                        autoSelect
                        options={getOptions()}
                        loading={isFetching}
                        isOptionEqualToValue={(option, value) =>
                          option.id === value.id
                        }
                        onInputChange={(event, value) => {
                          handleSearchDebounce(value);
                        }}
                        filterOptions={(options, params) => {
                          const haveDuplicateName = !!options.find(item => {
                            return (
                              item.unitName?.toLowerCase() ===
                              params.inputValue?.toLowerCase()
                            );
                          });

                          if (isFetching) {
                            // trigger loading
                            return [];
                          }

                          if (
                            (!haveDuplicateName || options.length <= 0) &&
                            params.inputValue
                          ) {
                            return [
                              ...options,
                              {
                                inputValue: params.inputValue,
                                unitName: t(
                                  Localization(
                                    'models-corporate',
                                    'add-folder-list',
                                  ),
                                  {
                                    name: `${params.inputValue}`,
                                  },
                                ),
                                id: '',
                                createdAt: '',
                                createdBy: '',
                                modifiedAt: '',
                                modifiedBy: '',
                                businessUnitId: '',
                                businessUnit: {
                                  id: NaN,
                                  department: '',
                                  children: [],
                                },
                              },
                            ];
                          }
                          return options;
                        }}
                        getOptionLabel={option => option.unitName}
                        renderOption={(props, option) => {
                          return (
                            <li
                              style={{fontSize: 14, fontWeight: 400}}
                              {...props}
                              key={option.id}>
                              {option.inputValue && (
                                <NGPlusIcon
                                  sx={{
                                    height: '20px',
                                    mt: '-1px',
                                    color: activeColor,
                                    mr: 1,
                                  }}
                                />
                              )}
                              {option.unitName}
                            </li>
                          );
                        }}
                        onChange={async (e, data) => {
                          if (data?.inputValue) {
                            setOpenCategoryForm(true);
                            resetCategory({unitName: data.inputValue});
                            return onChange(null);
                          }
                          if (data?.businessUnits) {
                            setDepartments([data.businessUnits]);
                            setValue(
                              'department',
                              data.businessUnits.id.toString(),
                            );
                          }
                          return onChange(data);
                        }}
                        renderInput={params => {
                          return (
                            <TextField
                              {...params}
                              error={!!errors.typeModel}
                              sx={{
                                fontSize: 14,
                                fontWeight: 400,
                                '& ::placeholder': {
                                  fontSize: 14,
                                  fontWeight: 400,
                                },
                              }}
                              helperText={
                                !!errors.typeModel &&
                                t(Localization('form-error', 'required-field'))
                              }
                              placeholder={
                                t(
                                  Localization(
                                    'models-corporate',
                                    'select-category',
                                  ),
                                )!
                              }
                            />
                          );
                        }}
                        {...props}
                      />
                    );
                  }}
                />
                {/* <NGInputField<IPayloadDetail>
                  size="small"
                  sx={{
                    fontSize: 15,
                    minWidth: '15rem',
                    '& .css-14yggkj-MuiTypography-root': {
                      color: 'rgba(0, 0, 0, 0.38)',
                    },
                  }}
                  control={control}
                  eMessage={'...'}
                  errorInput={errors?.typeModel}
                  typeInput={'select'}
                  type={'text'}
                  name={'typeModel'}>
                  <MenuItem value={''} sx={{display: 'none'}}>
                    <NGText
                      text={t(
                        Localization('models-corporate', 'select-category'),
                      )}
                      myStyle={{
                        fontSize: 14,
                        fontWeight: 400,
                        color: '#767676',
                      }}
                    />
                  </MenuItem>

                  {data ? (
                    data.map(item => (
                      <MenuItem value={item.id} key={item.id}>
                        <NGText
                          text={item.name}
                          myStyle={{
                            fontSize: 14,
                            fontWeight: 400,
                          }}
                        />
                      </MenuItem>
                    ))
                  ) : (
                    <>...</>
                  )}
                </NGInputField> */}
              </Stack>

              {/* Model department field */}
              <Stack gap="6px">
                <Stack direction={'row'}>
                  <NGText
                    text={t(Localization('models-corporate', 'department'))}
                    myStyle={{fontSize: 13, fontWeight: 500}}
                  />

                  <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
                </Stack>
                <NGInputField<IPayloadDetail>
                  size="small"
                  sx={{fontSize: 15, minWidth: '15rem'}}
                  disabled={departments.length <= 0}
                  errorInput={errors.department}
                  control={control}
                  eMessage={t(Localization('form-error', 'required-field'))!}
                  typeInput={'select'}
                  type={'text'}
                  name={'department'}>
                  <MenuItem value={''} sx={{display: 'none'}}>
                    <NGText
                      text={t(
                        Localization('models-corporate', 'select-department'),
                      )}
                      myStyle={{
                        fontSize: 14,
                        fontWeight: 400,
                        color: '#767676',
                      }}
                    />
                  </MenuItem>
                  {departments.length > 0
                    ? departments.map(item => (
                        <MenuItem value={item.id} key={item.id}>
                          <NGText
                            text={item.unitName}
                            myStyle={{
                              fontSize: 14,
                              fontWeight: 400,
                            }}
                          />
                        </MenuItem>
                      ))
                    : '...'}
                </NGInputField>
              </Stack>

              {/* Model service field */}
              {/* <Stack gap="6px">
                <Stack direction={'row'}>
                  <NGText
                    text={t(Localization('models-corporate', 'services'))}
                    myStyle={{fontSize: 13, fontWeight: 500}}
                  />

                  <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
                </Stack>
                <Autocomplete
                  disabled={!category}
                  fullWidth
                  sx={{
                    '& .MuiAutocomplete-inputRoot': {
                      fieldset: {
                        borderColor:
                          category && !services.length ? '#d32f2f' : '#E9E9E9',
                      },
                      '&.Mui-focused fieldset': {
                        borderColor: '#E9E9E9',
                        borderWidth: '0.2px',
                      },
                      '&.Mui-disabled': {
                        fieldset: {
                          borderColor: '#E9E9E9',
                        },
                      },
                      '&:hover fieldset': {
                        borderColor: '#E9E9E9',
                      },
                    },
                    '& input::placeholder': {
                      fontSize: pixelToRem(14),
                    },
                  }}
                  value={services}
                  size="small"
                  multiple
                  id="checkboxes-tags-demo"
                  options={options}
                  onChange={(event: any, newValue) => {
                    setServices(newValue);
                  }}
                  isOptionEqualToValue={(option, value) => {
                    return option.id === value.id;
                  }}
                  disableCloseOnSelect
                  getOptionLabel={option => option.unitName}
                  renderOption={(props, option, {selected}) => (
                    <li
                      {...props}
                      style={{
                        padding: 0,
                      }}>
                      <Checkbox
                        icon={icon}
                        checkedIcon={checkedIcon}
                        style={{marginRight: 8}}
                        checked={selected}
                      />
                      <Typography
                        sx={{
                          fontSize: '12px',
                          fontWeight: 500,
                          fontFamily: 'Poppins',
                        }}>
                        {option.unitName}
                      </Typography>
                    </li>
                  )}
                  renderInput={params => (
                    <TextField
                      sx={{
                        fontFamily: 'Poppins',
                      }}
                      {...params}
                      placeholder={
                        t(Localization('models-corporate', 'select-service'))!
                      }
                    />
                  )}
                />
                {category && !services.length && (
                  <Typography
                    sx={{
                      color: 'red',
                      fontSize: '12px',
                    }}>
                    ...
                  </Typography>
                )}
              </Stack> */}
            </Stack>
          </Stack>
        </CardContent>
      </Card>
      <FormProvider {...categoryMethods}>
        <NGDialog
          maxWidth={'sm'}
          fullWidth
          open={openCategoryForm}
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
          titleDialog={
            <Stack gap={'12px'} alignItems={'center'} direction={'row'}>
              <NGPlusIcon
                sx={{
                  height: '20px',
                  mt: '-1px',
                  color: activeColor,
                }}
              />
              <NGText
                myStyle={{width: '517px', height: '28px'}}
                text={t(Localization('create-folder-form', 'create-folder'))}
                fontSize={'18px'}
                fontWeight="600"
              />
            </Stack>
          }
          contentDialog={
            <CreateCategoryForm
              onClose={handleClose}
              onAddSuccess={onAddSuccess}
            />
          }
          actionDialog={<AddUserAction onClose={handleClose} />}
        />
      </FormProvider>
    </Stack>
  );
};

const CreateCategoryForm = ({
  onClose,
  onAddSuccess,
}: {
  onClose: () => void;
  onAddSuccess?: (data: CorporateModelFolder) => void;
}): JSX.Element => {
  const {enqueueSnackbar} = useSnackbar();

  // slide get profile data
  const [getUserProfileTrigger, {data: userData}] =
    useLazyGetEndUserProfileQuery();
  const [createFolder, {isLoading: isCreateLoading}] =
    useCreateFolderMutation();

  const {
    control,
    handleSubmit,
    formState: {errors},
  } = useFormContext<FolderCategoryForm>();

  React.useEffect(() => {
    const handleFetchUserProfile = async () => {
      await getUserProfileTrigger(null);
    };
    handleFetchUserProfile();
  }, []);

  // handle delete user
  const onSubmit = async (
    formData: NonUndefined<typeof control._defaultValues>,
  ) => {
    try {
      const {unitName} = formData;
      if (unitName && userData?.businessUnitInfo.id) {
        const responseData = await createFolder({
          unitName,
          businessUnitId: userData.businessUnitInfo.id,
        }).unwrap();
        enqueueSnackbar(
          t(Localization('create-folder-form', 'create-successfully'), {
            name: `${responseData.unitName}`,
          }),
          {
            variant: 'successSnackbar',
          },
        );

        if (typeof onAddSuccess === 'function') {
          onAddSuccess(responseData);
        }

        return onClose();
      }
      return enqueueSnackbar(UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    } catch (error) {
      enqueueSnackbar((error as any)?.data?.error?.message ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  return (
    <Stack
      sx={{p: '20px', border: '1px solid #E9E9E9'}}
      gap={'10px'}
      alignItems={'center'}
      width={'100%'}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <form
        style={{width: '100%'}}
        id="add_folder_dialog_form"
        onSubmit={handleSubmit(onSubmit)}>
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
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Stack width="100%">
              <Stack direction={'row'}>
                <NGText
                  text={t(Localization('create-folder-form', 'name'))}
                  myStyle={{fontSize: 13, fontWeight: 500}}
                />

                <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
              </Stack>
              <NGInputField<FolderCategoryForm>
                size="small"
                sx={{
                  fontSize: 15,
                }}
                control={control}
                eMessage={t(Localization('form-error', 'required-field'))!}
                inputProps={{color: 'red'}}
                errorInput={errors?.unitName}
                typeInput={'name'}
                type={'text'}
                name={`unitName`}
                placeholder={t(Localization('create-folder-form', 'name'))!}
              />
            </Stack>
          </Grid>
        </Grid>
      </form>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isCreateLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

const AddUserAction = ({onClose}: {onClose: () => void}): JSX.Element => {
  const {
    formState: {errors},
  } = useFormContext<FolderCategoryForm>();
  const activeColor = store.getState().enterprise.theme[0].mainColor;

  return (
    <Stack
      gap={'10px'}
      width={'100%'}
      height={'64px'}
      justifyContent={'center'}>
      <Stack direction={'row'} justifyContent={'flex-end'} gap={'10px'}>
        <NGButton
          onClick={onClose}
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          locationIcon="start"
          color={['#ffffff', '#000000']}
          variant="outlined"
          fontSize="11px"
          myStyle={{
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            width: '77px',
            height: '36px',
            borderRadius: '6px',
            border: '1px solid #000000',
            '&:hover': {
              borderColor: activeColor ?? 'info.main',
            },
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('create-folder-form', 'cancel'))}
        />
        <NGButton
          type={'submit'}
          form="add_folder_dialog_form"
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          icon={
            <NGPlusIcon
              sx={{
                width: '18px',
                height: '20px',
                color: 'White.main',
              }}
            />
          }
          disabled={Object.keys(errors).length > 0}
          locationIcon="end"
          color={['#ffffff', '#ffffff']}
          variant="contained"
          fontSize="11px"
          myStyle={{
            '&.Mui-disabled': {
              bgcolor: colorDisable,
            },
            '&.MuiButtonBase-root': {
              borderColor: activeColor ?? 'Primary.main',
            },
            bgcolor: activeColor ?? 'Primary.main',
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            height: '36px',
            borderRadius: '6px',
            borderColor: '#000000',
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('create-folder-form', 'submit'))}
        />
      </Stack>
    </Stack>
  );
};

export default ModelDetail;
