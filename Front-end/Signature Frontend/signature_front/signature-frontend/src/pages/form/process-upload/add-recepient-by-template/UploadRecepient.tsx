import {CountryCode, ICountry} from '@/components/ng-phone/type';
import {
  FONT_TYPE,
  KeySignatureLevel,
  Participant,
  STEP,
  defaultColor,
} from '@/constant/NGContant';
import {colorSuccess} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  setActiveActorRole,
  setOrderSignature,
  storeAnnotation,
  storeApprovals,
  storeEnvoiByRole,
  storeRecipient,
  storeSignatories,
} from '@/redux/slides/authentication/authenticationSlide';
import {useUpdateProjectMutation} from '@/redux/slides/project-management/project';
import {ISignatory} from '@/redux/slides/project-management/signatory';
import {
  getCountryPhones,
  getTemporaryParticipants,
  handleSignatureStep,
  validateProjectTemplate,
} from '@/utils/common/SignatureProjectCommon';
import {$ok} from '@/utils/request/common/type';
import {NGInputField} from '@components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import InfoIcon from '@mui/icons-material/Info';
import {
  Backdrop,
  Box,
  Checkbox,
  CircularProgress,
  IconButton,
  MenuItem,
  Stack,
  TextField,
} from '@mui/material';
import {grey} from '@mui/material/colors';
import asYouType from 'google-libphonenumber';
import React, {useEffect} from 'react';
import {Controller, useFieldArray, useForm} from 'react-hook-form';
import {useTranslation} from 'react-i18next';
import flags from 'react-phone-number-input/flags';
import en from 'react-phone-number-input/locale/en.json';
import {alertConsole} from '../common';
import {AntSwitch} from '../edit-pdf/other/common';
import {IRecipient, IUploadForm} from '../type';

const PNF = asYouType.PhoneNumberFormat;
const phoneUtil = asYouType.PhoneNumberUtil.getInstance();

type IUploadReceptient = {
  setActiveStep: React.Dispatch<React.SetStateAction<number>>;
  uploadStep2: boolean;
  setUploadStep2: React.Dispatch<React.SetStateAction<boolean>>;
  setIsSignatoryFill: React.Dispatch<React.SetStateAction<boolean>>;
};

const UploadReceptientByTemplate = ({
  setActiveStep,
  uploadStep2,
  setUploadStep2,
  setIsSignatoryFill,
}: IUploadReceptient) => {
  const {t} = useTranslation();
  const {project, signatories, approvals, recipients, viewers} = useAppSelector(
    state => state.authentication,
  );
  const {signature, approval, participants} = project.template!;

  const dispatch = useAppDispatch();
  /** Error message state */
  const [errorMessageSignatory, setErrorMessageSignatory] =
    React.useState<JSX.Element | null>(null);
  /** Upload loading */
  const [uploadLoading, setUploadLoading] = React.useState<boolean>(false);

  const [updateProject] = useUpdateProjectMutation();
  const amountSignatureApproveForm = (length: number): Array<IRecipient> => {
    return length > 0
      ? Array.from({length}, (item, index: number) => ({
          id: '',
          firstName: '',
          lastName: '',
          role: participants[index],
          email: '',
          phone: '',
          checked: false,
          sortOrder: index + 1,
        }))
      : [
          {
            id: '',
            firstName: '',
            lastName: '',
            role: Participant['Signatory'],
            email: '',
            phone: '',
            checked: false,
            sortOrder: 1,
          },
        ];
  };
  /** Form data **/
  const {
    control,
    formState: {errors},
    trigger,
    getValues,
    watch,
    handleSubmit,
  } = useForm<IUploadForm>({
    defaultValues: {
      recipients:
        signatories.length + approvals.length > 0
          ? [...signatories, ...approvals]
          : amountSignatureApproveForm(signature + approval),
    },
  });

  const fillForm = watch([`recipients`]);

  /** Form fields array **/
  const {fields} = useFieldArray({
    control,
    name: 'recipients',
  });
  const [country, setCountry] = React.useState<ICountry[]>(
    signatories.length + approvals.length > 0
      ? getCountryPhones(approvals, signatories, recipients, viewers)
      : Array.from({length: signature + approval}, () => ({
          callingCode: '33',
          code: 'FR',
          name: 'france',
        })),
  );

  const amountSignatureApprove = (length: number) => {
    if (length > 0) {
      return {amount: length};
    } else {
      return {amount: 1};
    }
  };
  /** Country select popup toggle open/close **/
  const [selectToggle, setToggle] = React.useState<{phoneNumber: boolean}[]>(
    Array.from(
      {
        length:
          signatories.length + approvals.length > 0
            ? signatories.length + approvals.length
            : amountSignatureApprove(signature + approval).amount,
      },
      () => ({
        phoneNumber: false,
      }),
    ),
  );

  /** Handle checked the order or unordered signatory **/
  const handleOrderSignatory = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setOrderSignature({orderSign: event.target.checked}));
  };

  /** Handle close country select **/
  const handleClose = (
    event: React.SyntheticEvent<Element, Event>,
    index?: number,
  ) => {
    const tempToggle = [...selectToggle];
    tempToggle[index!].phoneNumber = false;
    setToggle(tempToggle);
  };

  /** Handle country/flag change **/
  const handlePhone = (data: string, index?: number) => {
    const res = data.split(' ');
    const tempCountry = [...country];
    tempCountry[index!].name = res[0];
    tempCountry[index!].callingCode = res[1];
    tempCountry[index!].code = res[2] as CountryCode;
    setCountry(tempCountry);
  };
  /** Submit form data **/
  const onSubmit = async (data: {recipients: IRecipient[]}) => {
    let right = handleSignatureStep(data.recipients);

    const storeData: ISignatory[] = [];
    setUploadLoading(true);
    data.recipients.forEach((d, index: number) => {
      const number = phoneUtil.parseAndKeepRawInput(
        d.phone!,
        country[index].code,
      );
      storeData.push({
        id: d.id!,
        email: d.email!.trimStart().trimEnd(),
        firstName: d.firstName!.trimStart().trimEnd(),
        lastName: d.lastName!.trimStart().trimEnd(),
        invitationStatus: 'in progress',
        phone: phoneUtil.format(number, PNF.E164)!,
        role: d.role!,
        sortOrder: d.sortOrder!,
        projectId: project.id as string | number,
      });
    });
    if (!right) {
      setErrorMessageSignatory(
        alertConsole(
          'error',
          t(
            Localization(
              'upload-document',
              'please-add-at-least-one-signatory',
            ),
          ),
        ),
      );
      return setUploadLoading(false);
    }
    if ($ok(project.template)) {
      right = validateProjectTemplate(
        project.template!,
        data.recipients,
        STEP.STEP2,
      );
      if (!right) {
        setErrorMessageSignatory(
          alertConsole(
            'error',
            t(Localization('upload-document', 'invalid-input-template')) +
              ', ' +
              project.template!.signature +
              t(Localization('text', 'signataire.trice.s')) +
              ', ' +
              project.template!.approval +
              t(Localization('text', 'approbateur.trice.s')),
          ),
        );
        return setUploadLoading(false);
      }
    }

    await updateProject({
      details: [],
      orderSign: store.getState().authentication.project.orderSign,
      orderApprove: store.getState().authentication.project.orderApprove,
      signatories: storeData,
      name: project.name!,
      step: 2,
      id: project.id as string | number,
      signatureLevel: KeySignatureLevel.SIMPLE,
    })
      .unwrap()
      .then(data => {
        const tempParticipants = getTemporaryParticipants(data.signatories);
        dispatch(storeSignatories({data: tempParticipants.tempSignatories}));
        dispatch(storeApprovals({data: tempParticipants.tempApprovals}));
        dispatch(storeRecipient({data: tempParticipants.tempRecipients}));
        store.dispatch(
          storeAnnotation({
            signatories: tempParticipants.tempSignatories,
          }),
        );
        dispatch(
          setActiveActorRole({
            role: `${data.signatories[0].role}` as Participant,
            id: Number(data.signatories[0].id),
            signatoryName:
              data.signatories[0].firstName +
              ' ' +
              data.signatories[0].lastName,
          }),
        );
        dispatch(
          storeEnvoiByRole({
            signatories: data.signatories,
            projectDetails: data.details,
          }),
        );
        setUploadLoading(false);
        setActiveStep(v => v + 1);
      })
      .catch(() => {
        setUploadLoading(false);
      });
    setErrorMessageSignatory(null);
  };

  useEffect(() => {
    const isComplete: boolean[] = [];
    fillForm[0].forEach(item => {
      const {firstName, lastName, phone, email} = item;
      if (firstName && lastName && phone && email) {
        isComplete.push(true);
      } else {
        isComplete.push(false);
      }
    });
    if (isComplete.every(value => value)) {
      setIsSignatoryFill(true);
    } else {
      setIsSignatoryFill(false);
    }
  }, [fillForm]);

  React.useEffect(() => {
    country.forEach((_, index) => {
      if (getValues(`recipients.${index}.phone`)) {
        trigger(`recipients.${index}.phone`);
      }
    });
  }, [country]);

  React.useEffect(() => {
    if (uploadStep2) {
      handleSubmit(onSubmit)();
    }
    return () => setUploadStep2(false);
  }, [uploadStep2]);

  return (
    <Stack sx={{p: '1rem', backgroundColor: 'bg.main'}} alignItems={'center'}>
      <Stack
        spacing={4}
        maxWidth={{xs: '100%', sm: '80%', md: '90%'}}
        sx={{pb: '2rem'}}>
        <Stack spacing={2}>
          <NGText text={errorMessageSignatory} />
          <NGText
            text={t(Localization('table', 'recipient'))}
            myStyle={{fontWeight: 600, fontSize: 18}}
          />

          <NGText
            myStyle={{fontSize: 14}}
            text={t(
              Localization(
                'upload-signatories',
                'who-will-receive-the-documents',
              ),
            )}
          />
        </Stack>
        {/** Switch on/off signatories order */}
        <Box
          sx={{
            padding: 1,
            width: ['100%', '100%', '50%'],
            borderRadius: 1.5,
            display: 'flex',
            bgcolor: 'white',
            // boxShadow: 1,
            alignItems: 'center',
            justifyContent: 'space-between',
            border: store.getState().authentication.project.orderSign
              ? `1.5px ${colorSuccess} solid`
              : '1.5px solid #E9E9E9',
          }}>
          <Stack direction={'row'} alignItems={'center'} spacing={2}>
            <AntSwitch
              checked={store.getState().authentication.project.orderSign}
              onChange={handleOrderSignatory}
              overridecolor="#1C8752"
            />

            <NGText
              text={t(
                Localization('upload-signatories', 'define-a-signing-order'),
              )}
              myStyle={{fontSize: 12, fontWeight: 400}}
            />
          </Stack>
          <InfoIcon sx={{color: 'blue.main'}} />
        </Box>
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
                boxShadow: 1,
                position: 'relative',
                width: '100%',
              }}>
              <Stack sx={{p: 4}}>
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
                  {/** Name field */}
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
                  {/** Role field */}
                  <Stack sx={{flex: '1 1 330px'}}>
                    <Stack direction={'row'}>
                      <NGText
                        text={t(Localization('upload-signatories', 'role'))}
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
                            'role-error',
                          ),
                        )!
                      }
                      errorInput={
                        errors.recipients
                          ? errors.recipients[index]?.role
                          : undefined
                      }
                      typeInput={'select'}
                      type={'text'}
                      name={`recipients.${index}.role`}
                      placeholder={
                        t(Localization('upload-signatories', 'role'))!
                      }>
                      <MenuItem value={''} sx={{display: 'none'}}>
                        <NGText
                          text={t(Localization('upload-signatories', 'role'))}
                          myStyle={{fontSize: 14, fontWeight: 400}}
                        />
                      </MenuItem>
                      {approval > 0 && (
                        <MenuItem value={Participant.Approval}>
                          <NGText
                            text={t(
                              Localization('upload-signatories', 'approval'),
                            )}
                            myStyle={{fontSize: 14, fontWeight: 400}}
                          />
                        </MenuItem>
                      )}
                      {signature > 0 && (
                        <MenuItem value={Participant.Signatory}>
                          <NGText
                            text={t(
                              Localization('upload-signatories', 'signatory'),
                            )}
                            myStyle={{fontSize: 14, fontWeight: 400}}
                          />
                        </MenuItem>
                      )}
                      {/** <MenuItem value={Participant.Receipt}>
                                             <NGText
                                             text={t(
                            Localization('upload-signatories', 'receipt'),
                          )}
                                             myStyle={{fontSize: 14, fontWeight: 400}}
                                             />
                                             </MenuItem> */}
                    </NGInputField>
                  </Stack>
                  {/** Email field */}
                  <Stack sx={{flex: '1 1 330px'}}>
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
                      placeholder={t(Localization('form', 'email'))!}
                    />
                  </Stack>
                  {/** Phone number field */}
                  <Stack sx={{flex: '1 1 330px'}}>
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
                {/** Checkbox field */}
                <Stack sx={{maxWidth: '520px', pt: 2}} display={'none'}>
                  <Stack sx={{gap: 1}} direction={'row'}>
                    <Stack
                      sx={{
                        width: '100%',
                        height: '100%',
                        p: 1,
                        border: `1px solid ${
                          getValues(`recipients.${index}.checked`)
                            ? defaultColor
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

                <Controller
                  control={control}
                  name={`recipients.${index}.checked`}
                  render={() => <TextField sx={{display: 'none'}} />}
                />
              </Stack>
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

export default UploadReceptientByTemplate;
