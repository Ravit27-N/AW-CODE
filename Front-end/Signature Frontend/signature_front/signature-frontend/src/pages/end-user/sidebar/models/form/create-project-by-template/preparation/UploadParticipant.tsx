import {NGBinIcon} from '@/assets/Icon';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import {FONT_TYPE, Participant} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {IRecipient, IUploadForm} from '@/pages/form/process-upload/type';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  storeAnnotation,
  storeApprovals,
  storeRecipient,
  storeSignatories,
  storeViewers,
} from '@/redux/slides/authentication/authenticationSlide';
import {
  getCountryPhones,
  getTemporaryParticipants,
} from '@/utils/common/SignatureProjectCommon';
import {NGInputField} from '@components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {
  Backdrop,
  Box,
  CircularProgress,
  IconButton,
  Stack,
  TextField,
} from '@mui/material';
import asYouType from 'google-libphonenumber';
import React from 'react';
import {Controller, useFieldArray, useForm} from 'react-hook-form';
import {useTranslation} from 'react-i18next';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import {useParams} from 'react-router';

const PNF = asYouType.PhoneNumberFormat;
const phoneUtil = asYouType.PhoneNumberUtil.getInstance();
export type IParticipant =
  | 'tempSignatories'
  | 'tempApprovals'
  | 'tempRecipients'
  | 'tempViewers';

type IUploadReceptient = {
  value: IUploadForm['recipients'][number];
  uploadTrigger: boolean;
  fillForm: boolean;
  setUploadTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  setFillForm: React.Dispatch<React.SetStateAction<boolean>>;
  setValue: React.Dispatch<React.SetStateAction<IRecipient | null>>;
};

export const getParticipant = (
  tempParticipants: {
    [k in IParticipant]: (IRecipient & {fillForm?: boolean})[];
  },
  participant: IParticipant,
  participantsArray: (IRecipient & {fillForm?: boolean})[],
) => {
  return tempParticipants[participant].length > 0
    ? [
        ...tempParticipants[participant],
        ...participantsArray.filter(
          item => item.id !== tempParticipants[participant][0].id,
        ),
      ]
    : [...tempParticipants[participant]];
};

const UploadParticipant = (props: IUploadReceptient) => {
  const {t} = useTranslation();
  const param = useParams();
  const {
    project,
    createProjectActiveRole,
    approvals,
    signatories,
    recipients,
    viewers,
    annotations,
  } = useAppSelector(state => state.authentication);

  const [check, setCheck] = React.useState<{active: boolean}[]>(
    Array.from(
      {
        length: [props.value].length ?? 1,
      },
      () => ({
        active: false,
      }),
    ),
  );
  const dispatch = useAppDispatch();
  /**  Upload loading **/
  const [uploadLoading, setUploadLoading] = React.useState<boolean>(false);
  /** Form data **/
  const {
    control,
    formState: {errors},
    getValues,
    trigger,
    setError,
    setValue,
    handleSubmit,
  } = useForm<IUploadForm>({
    defaultValues: {
      recipients: [{...props.value, id: createProjectActiveRole?.id}],
    },
  });

  /** Form fields array */
  const {fields} = useFieldArray({
    control,
    name: 'recipients',
  });
  const [country, setCountry] = React.useState<ICountry[]>(
    getCountryPhones([props.value], [], [], []),
  );

  /** Country select popup toggle open/close */
  const [selectToggle, setToggle] = React.useState<{phoneNumber: boolean}[]>(
    Array.from(
      {
        length: [props.value].length ?? 1,
      },
      () => ({
        phoneNumber: false,
      }),
    ),
  );

  /** Handle close country select**/
  const handleClose = (
    event: React.SyntheticEvent<Element, Event>,
    index?: number,
  ) => {
    const tempToggle = [...selectToggle];
    tempToggle[index!].phoneNumber = false;
    setToggle(tempToggle);
  };

  /** Handle country/flag change**/
  const handlePhone = (data: string, index?: number) => {
    const res = data.split(' ');
    const tempCountry = [...country];
    tempCountry[index!].name = res[0];
    tempCountry[index!].callingCode = res[1];
    tempCountry[index!].code = res[2] as CountryCode;
    setCountry(tempCountry);
  };

  const dispatchParticipant = (tempParticipants: {
    [k in IParticipant]: (IRecipient & {fillForm?: boolean})[];
  }) => {
    const temp = {
      [Participant.Approval]: storeApprovals({
        data: getParticipant(tempParticipants, 'tempApprovals', approvals),
      }),
      [Participant.Signatory]: storeSignatories({
        data: getParticipant(tempParticipants, 'tempSignatories', signatories),
      }),
      [Participant.Receipt]: storeRecipient({
        data: getParticipant(tempParticipants, 'tempRecipients', recipients),
      }),
      [Participant.Viewer]: storeViewers({
        data: getParticipant(tempParticipants, 'tempViewers', viewers),
      }),
    };

    return dispatch(temp[createProjectActiveRole?.role! as Participant]);
  };

  /** Submit form data**/
  const onSubmit = async (data: {recipients: IRecipient[]}) => {
    const storeData: (IRecipient & {fillForm: boolean})[] = [];
    data.recipients.forEach((d, index: number) => {
      const number = phoneUtil.parseAndKeepRawInput(
        d.phone!,
        country[index].code,
      );
      storeData.push({
        id: createProjectActiveRole?.id,
        email: d.email!.trimStart().trimEnd(),
        firstName: d.firstName!.trimStart().trimEnd(),
        lastName: d.lastName!.trimStart().trimEnd(),
        invitationStatus: 'in progress',
        phone: phoneUtil.format(number, PNF.E164)!,
        role: createProjectActiveRole?.role,
        sortOrder: store.getState().authentication.signatories.length + 1,
        projectId: project.id as string | number,
        fillForm: props.fillForm,
      });
    });

    const tempData: (IRecipient & {fillForm: boolean})[] = [];
    store.getState().authentication.signatories.forEach(x => {
      if (x.id === storeData[0].id) {
        tempData.push({
          ...storeData[0],
          sortOrder: x.sortOrder,
        });
      }
    });

    const tempParticipants = getTemporaryParticipants(
      tempData.length ? tempData : storeData,
    );

    dispatchParticipant(tempParticipants);
    if (
      tempParticipants.tempSignatories.length > 0 &&
      tempParticipants.tempSignatories[0].sortOrder! ===
        store.getState().authentication.signatories.length
    ) {
      dispatch(
        storeAnnotation({
          signatories: tempParticipants.tempSignatories.filter(
            element =>
              !annotations.some(
                comingElement => comingElement.sortOrder === element.sortOrder,
              ),
          ),
        }),
      );
    }

    props.setTrigger(false);
  };

  React.useEffect(() => {
    country.forEach((_, index) => {
      if (getValues(`recipients.${index}.phone`)) {
        trigger(`recipients.${index}.phone`);
      }
    });
  }, [country]);

  React.useEffect(() => {
    if (props.value) {
      setValue('recipients', [props.value]);
    }
  }, [props.value]);

  React.useEffect(() => {
    if (props.uploadTrigger) {
      handleSubmit(onSubmit)();
    }
    return () => props.setUploadTrigger(false);
  }, [props.uploadTrigger]);
  return (
    <Stack sx={{backgroundColor: 'bg.main'}} alignItems={'center'}>
      <Stack spacing={4}>
        {/** Array of forms of participants */}
        {fields.length > 0 &&
          errors &&
          fields.map((field, index: number) => (
            <Stack
              key={field.id}
              direction={'row'}
              alignItems={'center'}
              sx={{
                bgcolor: '#ffffff',
                borderRadius: 2,
                border: '1px solid #E9E9E9',
                borderTopLeftRadius: 0,
                borderTopRightRadius: 0,
                position: 'relative',
                width: '100%',
              }}>
              <Stack sx={{p: 2}}>
                <Box
                  sx={{
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: 2,
                  }}
                  flex={'1 110px'}>
                  {/** First name field */}
                  <Stack sx={{flex: '1 1 220px'}}>
                    <Stack direction={'row'}>
                      <NGText
                        text={t(
                          Localization('upload-signatories', 'first-name'),
                        )}
                        myStyle={{fontSize: 13, fontWeight: 500}}
                      />

                      <NGText
                        text={'*'}
                        myStyle={{fontSize: 16, color: 'red'}}
                      />
                    </Stack>
                    <NGInputField<IUploadForm>
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
                      errorInput={
                        errors.recipients
                          ? errors.recipients[index]?.firstName
                          : undefined
                      }
                      typeInput={'first-name'}
                      type={'text'}
                      name={`recipients.${index}.firstName`}
                      placeholder={
                        t(Localization('upload-signatories', 'first-name'))!
                      }
                      style={{fontFamily: FONT_TYPE.POPPINS}}
                    />
                  </Stack>
                  {/* Name field */}
                  <Stack sx={{flex: '1 1 220px'}}>
                    <Stack direction={'row'}>
                      <NGText
                        text={t(Localization('form', 'name'))}
                        myStyle={{fontSize: 13, fontWeight: 500}}
                      />

                      <NGText
                        text={'*'}
                        myStyle={{fontSize: 16, color: 'red'}}
                      />
                    </Stack>
                    <NGInputField<IUploadForm>
                      size="small"
                      sx={{fontSize: 15, minWidth: '15rem'}}
                      control={control}
                      eMessage={t(Localization('form-error', 'name'))!}
                      inputProps={{color: 'red'}}
                      errorInput={
                        errors.recipients
                          ? errors.recipients[index]?.lastName
                          : undefined
                      }
                      typeInput={'last-name'}
                      type={'text'}
                      name={`recipients.${index}.lastName`}
                      placeholder={t(Localization('form', 'name'))!}
                    />
                  </Stack>

                  {/* Email field */}
                  <Stack sx={{flex: '1 1 220px'}}>
                    <Stack direction={'row'}>
                      <NGText
                        text={t(Localization('form', 'email'))}
                        myStyle={{fontSize: 13, fontWeight: 500}}
                      />
                      <NGText
                        text={'*'}
                        myStyle={{fontSize: 16, color: 'red'}}
                      />
                    </Stack>
                    <NGInputField<IUploadForm>
                      size="small"
                      sx={{fontSize: 15, minWidth: '15rem'}}
                      control={control}
                      eMessage={t(Localization('form-error', 'email'))!}
                      errorInput={
                        errors.recipients
                          ? errors.recipients[index]?.email
                          : undefined
                      }
                      typeInput={'email'}
                      type={'text'}
                      name={`recipients.${index}.email`}
                      placeholder={
                        t(Localization('form', 'email-participant'))!
                      }
                    />
                  </Stack>
                  {/** Phone number field */}
                  <Stack sx={{flex: '1 1 220px'}}>
                    <Stack direction={'row'}>
                      <NGText
                        text={t(Localization('form', 'phone-number'))}
                        myStyle={{fontSize: 13, fontWeight: 500}}
                      />
                      <NGText
                        text={'*'}
                        myStyle={{fontSize: 16, color: 'red'}}
                      />
                    </Stack>

                    <Stack sx={{gap: 1}} direction={'row'}>
                      <NGCountrySelect
                        sx={{
                          display: 'block',
                          width: 0,
                          visibility: 'hidden',
                        }}
                        labels={en}
                        selectIndex={index}
                        value={country[index].code}
                        selectToggle={selectToggle[index].phoneNumber}
                        handleClose={handleClose}
                        selectChange={handlePhone}
                      />
                      <IconButton
                        disableTouchRipple
                        disableFocusRipple
                        sx={{
                          padding: 0,
                          '&:hover': {
                            backgroundColor: 'transparent',
                          },
                        }}
                        onClick={() => {
                          const tempToggle = [...selectToggle];
                          tempToggle[index].phoneNumber =
                            !tempToggle[index].phoneNumber;
                          setToggle(tempToggle);
                        }}>
                        <FlagComponent
                          flags={flags}
                          country={country[index].code ?? 'FR'}
                          countryName={`abc ${index}`}
                        />
                        <ArrowDropDownIcon />
                      </IconButton>
                      <Stack sx={{width: '100%'}}>
                        <NGInputField<IUploadForm>
                          size="small"
                          sx={{fontSize: 15, minWidth: '15rem'}}
                          countryCode={country[index].code}
                          control={control}
                          eMessage={`${t(
                            Localization('form-error', 'phone-number'),
                          )!}`}
                          errorInput={
                            errors.recipients
                              ? errors.recipients[index]?.phone
                              : undefined
                          }
                          typeInput={'phone'}
                          type={'text'}
                          name={`recipients.${index}.phone`}
                          placeholder={t(Localization('form', 'phone-number'))!}
                        />
                      </Stack>
                    </Stack>
                  </Stack>
                </Box>
                {/* Checkbox field */}
                {/**  It will be used in future (sig-544-want-to-remove-it)
                <Stack sx={{maxWidth: '520px', pt: 2}} >
                  <Stack sx={{gap: 1}} direction={'row'}>
                    <Stack
                      sx={{
                        width: '100%',
                        height: '100%',
                        p: 1,
                        border: `1px solid ${
                          getValues(`recipients.${index}.checked`)
                            ? 'Primary.main'
                            : grey[400]
                        }`,
                        borderRadius: '5px',
                        alignItem: 'center',
                      }}
                      spacing={1}
                      direction={'row'}>
                      <Controller
                        control={control}
                        name={`recipients.${index}.checked`}
                        render={({field: {onChange, onBlur, value}}) => (
                          <Checkbox
                            size="small"
                            onBlur={onBlur}
                            checked={value}
                            onChange={onChange}
                            sx={{
                              p: 0,
                              color: 'Primary.main',
                              '&.Mui-checked': {
                                color: 'Primary.main',
                              },
                            }}
                          />
                        )}
                      />

                      <NGText
                        text={t(
                          Localization(
                            'upload-signatories',
                            'add-this-new-contact-to-the-contact-book',
                          ),
                        )}
                        myStyle={{fontSize: 12, fontWeight: 400}}
                      />
                    </Stack>
                  </Stack>
                </Stack>
                 **/}

                <Controller
                  control={control}
                  name={`recipients.${index}.checked`}
                  render={() => <TextField sx={{display: 'none'}} />}
                />
              </Stack>
              {/* Delete signatory */}
              {props.fillForm && (
                <IconButton
                  sx={{
                    bgcolor: '#ffffff',
                    boxShadow: 1,
                    position: 'absolute',
                    top: -12,
                    right: -12,
                  }}
                  disableRipple
                  disableFocusRipple
                  onClick={async () => {
                    // const signatoryId = getValues(`recipients.${index}.id`);
                    // if (signatoryId) {
                    //   await deleteSignatory({id: signatoryId})
                    //     .unwrap()
                    //     .then(() => {
                    //       remove(index);
                    //       const tempCheck = [...check];
                    //       setCheck(tempCheck);
                    //     });
                    // } else {
                    //   remove(index);
                    //   const tempCheck = [...check];
                    //   setCheck(tempCheck);
                    // }
                    props.setFillForm(false);
                    props.setValue(null);
                  }}>
                  <NGBinIcon
                    sx={{color: 'red', width: '15px', height: '15px'}}
                  />
                </IconButton>
              )}
            </Stack>
          ))}
      </Stack>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={uploadLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

export default UploadParticipant;
