import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {NGInputField} from '@/components/ng-input/NGInputField';
import NGText from '@/components/ng-text/NGText';
import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useGetCorporateModelFolderQuery} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {splitUserCompany} from '@/utils/common/String';
import {pixelToRem} from '@/utils/common/pxToRem';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import {
  Autocomplete,
  Card,
  CardContent,
  Checkbox,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import {Control, FieldErrors, UseFormSetValue, useForm} from 'react-hook-form';
import TopNav from './top-nav/TopNav';
const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

type ICreateModel = {
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

type IPayloadDetail = {
  name: string;
  typeModel: string;
  department: string;
  service: string;
};

type IPayloadScenario = {
  signature: number;
  approval: number;
  recipient: number;
};

const CreateModel = (props: ICreateModel) => {
  const [activeStep, setActiveStep] = React.useState(0);
  const {trigger, setTrigger} = props;
  // ** detail form control
  const {
    control: detailControl,
    formState: {errors: detailErrors},
    setValue: detailSetValue,
    handleSubmit: detailHandleSubmit,
    watch: detailWatch,
  } = useForm<IPayloadDetail>({
    defaultValues: {
      name: '',
      typeModel: '',
      department: '',
      service: '',
    },
  });
  const {
    control: scenarioControl,
    formState: {errors: dscenarioErrors},
    setValue: scenarioSetValue,
    handleSubmit: scenarioHandleSubmit,
    watch: scenarioWatch,
  } = useForm<IPayloadScenario>({
    defaultValues: {
      signature: 0,
      approval: 0,
      recipient: 0,
    },
  });

  const category = detailWatch('typeModel');

  return (
    <NGDialog
      fullScreen
      open={trigger}
      sxProp={{
        titleSx: {
          p: 0,
        },
        contentsSx: {
          p: 0,
        },
      }}
      titleDialog={
        <TopNav
          activeStep={activeStep}
          setActiveStep={setActiveStep}
          setTrigger={setTrigger}
        />
      }
      contentDialog={
        <CreateContent
          control={detailControl}
          errors={detailErrors}
          category={category}
          setValue={detailSetValue}
        />
      }
    />
  );
};

type ICreateContent = {
  control: Control<IPayloadDetail, any>;
  errors: FieldErrors<IPayloadDetail>;
  category: string;
  setValue: UseFormSetValue<IPayloadDetail>;
};

type IData = {
  id: number;
  name: string;
  businessUnit: {
    id: number;
    department: string;
    children: Array<{
      id: number;
      unitName: string;
    }>;
  };
};

const CreateContent = (props: ICreateContent) => {
  const {control, errors, category, setValue} = props;
  const [data, setData] = React.useState<Array<IData>>([]);
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const {currentData} = useGetCorporateModelFolderQuery(
    {
      id: Number(company.companyId),
    },
    {skip: !company.companyId},
  );

  React.useEffect(() => {
    if (category !== '') {
      setValue(
        'department',
        `${data.find(({id}) => id === Number(category))?.businessUnit.id}`,
        {shouldTouch: true, shouldValidate: true, shouldDirty: true},
      );
    }
  }, [category]);

  React.useMemo(() => {
    if (currentData) {
      const cur = currentData.map(item => {
        const {id, businessUnits, unitName: name} = item;
        const {id: unitId, unitName, children} = businessUnits!;
        return {
          id,
          name,
          businessUnit: {
            department: unitName,
            id: unitId,
            children: children.map(i => {
              const {id, unitName} = i;
              return {id, unitName};
            }),
          },
        } as IData;
      });
      setData(cur);
    }
  }, [currentData]);

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
                  eMessage={
                    t(
                      Localization(
                        'upload-signatories-error',
                        'first-name-error',
                      ),
                    )!
                  }
                  errorInput={errors.name ?? undefined}
                  typeInput={'first-name'}
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
                <NGInputField<IPayloadDetail>
                  size="small"
                  sx={{fontSize: 15, minWidth: '15rem'}}
                  control={control}
                  eMessage={t(Localization('models-corporate', 'model-type'))!}
                  errorInput={errors.typeModel ?? undefined}
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
                </NGInputField>
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
                  disabled={!category}
                  control={control}
                  eMessage={t(Localization('models-corporate', 'model-type'))!}
                  errorInput={errors.typeModel ?? undefined}
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
                  {data ? (
                    data.map(
                      ({businessUnit, id}) =>
                        id === Number(category) && (
                          <MenuItem
                            value={businessUnit.id}
                            key={businessUnit.id}>
                            <NGText
                              text={businessUnit.department}
                              myStyle={{
                                fontSize: 14,
                                fontWeight: 400,
                              }}
                            />
                          </MenuItem>
                        ),
                    )
                  ) : (
                    <>...</>
                  )}
                </NGInputField>
              </Stack>

              {/* Model service field */}
              <Stack gap="6px">
                <Stack direction={'row'}>
                  <NGText
                    text={t(Localization('models-corporate', 'services'))}
                    myStyle={{fontSize: 13, fontWeight: 500}}
                  />

                  <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
                </Stack>
                <Autocomplete
                  disabled={!category}
                  // renderTags={(tagValue, getTagProps) => {
                  //   return tagValue.map((option, index) => (
                  //     <MyChip {...getTagProps({ index })} label={option.title} />
                  //   ));
                  // }}
                  fullWidth
                  sx={{
                    '& .MuiAutocomplete-inputRoot': {
                      fieldset: {
                        borderColor: '#E9E9E9',
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
                  size="small"
                  multiple
                  id="checkboxes-tags-demo"
                  options={
                    data.find(({id}) => id === Number(category))?.businessUnit
                      .children! ?? [
                      {
                        id: 1,
                        unitName: '',
                      },
                    ]
                  }
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
              </Stack>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
};

export default CreateModel;
