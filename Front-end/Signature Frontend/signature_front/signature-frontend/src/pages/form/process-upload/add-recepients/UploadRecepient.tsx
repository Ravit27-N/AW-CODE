import {NGBinIcon} from '@/assets/Icon';
import {CountryCode, ICountry} from '@/components/ng-phone/type';
import {
  FONT_TYPE,
  KeySignatureLevel,
  Participant,
  STEP,
} from '@/constant/NGContant';
import {StyleConstant, colorSuccess} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  setActiveActorRole,
  setOrderApprove,
  setOrderSignature,
  setSignatureLevels,
  storeAnnotation,
  storeApprovals,
  storeEnvoiByRole,
  storeRecipient,
  storeSignatories,
  storeViewers,
} from '@/redux/slides/authentication/authenticationSlide';
import {useGetSignatureLevelsQuery} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  IProjectById,
  useUpdateProjectMutation,
} from '@/redux/slides/project-management/project';
import {
  ISignatory,
  useDeleteSignatoryMutation,
} from '@/redux/slides/project-management/signatory';
import {ErrorServer, IResponseServer} from '@/utils/common/HandleException';
import {
  getCountryPhones,
  getTemporaryParticipants,
  handleSignatureStep,
  validateProjectTemplate,
} from '@/utils/common/SignatureProjectCommon';
import {splitUserCompany} from '@/utils/common/String';

import {$ok} from '@/utils/request/common/type';
import {NGInputField} from '@components/ng-input/NGInputField';
import FlagComponent, {
  NGCountrySelect,
} from '@components/ng-phone/NGCountrySelect';
import {HtmlTooltip} from '@components/ng-table/TableDashboard/resource/TCell';

import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';

import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import InfoIcon from '@mui/icons-material/Info';
import {
  Backdrop,
  Box,
  Button,
  CircularProgress,
  IconButton,
  MenuItem,
  Radio,
  Skeleton,
  Stack,
  TextField,
} from '@mui/material';
import FormControlLabel from '@mui/material/FormControlLabel';

import {ISignatureLevels} from '@/redux/slides/authentication/type';
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

const UploadRecipient = ({
  setActiveStep,
  uploadStep2,
  setUploadStep2,
  setIsSignatoryFill,
}: IUploadReceptient) => {
  const {t} = useTranslation();
  const {signatories, approvals, recipients, project, viewers, annotations} =
    useAppSelector(state => state.authentication);
  const [deleteSignatory] = useDeleteSignatoryMutation();
  const [check, setCheck] = React.useState<{active: boolean}[]>(
    Array.from(
      {
        length:
          signatories.length +
            approvals.length +
            recipients.length +
            viewers.length >
          0
            ? signatories.length +
              approvals.length +
              recipients.length +
              viewers.length
            : 1,
      },
      () => ({
        active: false,
      }),
    ),
  );
  const dispatch = useAppDispatch();
  /** Error message state **/
  const [errorMessageSignatory, setErrorMessageSignatory] =
    React.useState<JSX.Element | null>(null);
  /**  Upload loading **/
  const [uploadLoading, setUploadLoading] = React.useState<boolean>(false);
  const [updateProject] = useUpdateProjectMutation();
  /** Form data **/
  const {
    control,
    formState: {errors},
    watch,
    getValues,
    trigger,
    handleSubmit,
  } = useForm<IUploadForm>({
    defaultValues: {
      recipients:
        signatories.length +
          approvals.length +
          viewers.length +
          recipients.length >
        0
          ? [...signatories, ...approvals, ...recipients, ...viewers].sort(
              (a, b) => Number(a.id) - Number(b.id),
            )
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
            ],
    },
  });

  const fillForm = watch([`recipients`]);

  /** Form fields array */
  const {fields, append, remove} = useFieldArray({
    control,
    name: 'recipients',
  });
  const [country, setCountry] = React.useState<ICountry[]>(
    getCountryPhones(approvals, signatories, recipients, viewers),
  );

  /** Country select popup toggle open/close */
  const [selectToggle, setToggle] = React.useState<{phoneNumber: boolean}[]>(
    Array.from(
      {
        length:
          signatories.length +
            approvals.length +
            recipients.length +
            viewers.length >
          0
            ? signatories.length +
              approvals.length +
              recipients.length +
              viewers.length
            : 1,
      },
      () => ({
        phoneNumber: false,
      }),
    ),
  );

  /** Handle checked the order or unordered signatory **/
  const handleOrderApprove = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setOrderApprove({orderApprove: event.target.checked}));
  };

  /** Handle checked the order or unordered signatory **/
  const handleOrderSignatory = (event: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setOrderSignature({orderSign: event.target.checked}));
  };

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
  const handlePhone = async (data: string, index?: number) => {
    const res = data.split(' ');
    const tempCountry = [...country];
    tempCountry[index!].name = res[0];
    tempCountry[index!].callingCode = res[1];
    tempCountry[index!].code = res[2] as CountryCode;
    setCountry(tempCountry);
  };
  /** signatureLevel **/
  const [signatureLevel, setSignatureLevel] = React.useState<KeySignatureLevel>(
    KeySignatureLevel.NONE,
  );
  /** Submit form data**/
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

    try {
      const data = (await updateProject({
        details: [],
        orderSign: store.getState().authentication.project.orderSign,
        orderApprove: store.getState().authentication.project.orderApprove,
        signatories: storeData,
        name: project.name!,
        step: 2,
        id: project.id as string | number,
        signatureLevel,
      }).unwrap()) as IProjectById;

      const tempParticipants = getTemporaryParticipants(data.signatories);
      dispatch(storeSignatories({data: tempParticipants.tempSignatories}));
      dispatch(storeApprovals({data: tempParticipants.tempApprovals}));
      dispatch(storeViewers({data: tempParticipants.tempViewers}));
      dispatch(storeRecipient({data: tempParticipants.tempRecipients}));
      // const annot = annotations.map(i => i.signatoryId) as string[];
      store.dispatch(
        storeAnnotation({
          signatories: tempParticipants.tempSignatories.filter(
            element =>
              !annotations.some(
                comingElement => comingElement.signatoryId === element.id,
              ),
          ),
        }),
      );
      dispatch(
        setActiveActorRole({
          role: `${data.signatories[0].role}` as Participant,
          id: Number(data.signatories[0].id),
          signatoryName:
            data.signatories[0].firstName + ' ' + data.signatories[0].lastName,
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
      setErrorMessageSignatory(null);
    } catch (error) {
      setErrorMessageSignatory(
        alertConsole('error', ErrorServer(error as IResponseServer)),
      );
      setUploadLoading(false);
    }
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

  /** get company UUID form redux **/
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  /** end point for call signature levels form super admin and corporate **/
  const getSignatureLevels = useGetSignatureLevelsQuery(
    {
      uuid: company.companyUuid,
    },
    {skip: !company.companyUuid},
  );
  const [dataStaticSignatureLevels] = React.useState<ISignatureLevels[]>([
    {
      channelReminder: '',
      companyChannel: '',
      companyFileType: [],
      companyUuid: '',
      documentTerms: '',
      fileType: [],
      identityTerms: '',
      personalTerms: '',
      signatureLevel: KeySignatureLevel.SIMPLE,
    },
    {
      channelReminder: '',
      companyChannel: '',
      companyFileType: [],
      companyUuid: '',
      documentTerms: '',
      fileType: [],
      identityTerms: '',
      personalTerms: '',
      signatureLevel: KeySignatureLevel.ADVANCE,
    },
  ]);

  React.useEffect(() => {
    if (
      getSignatureLevels.currentData &&
      getSignatureLevels.currentData.length > 0
    ) {
      /** default the first elements **/
      setSignatureLevel(
        store.getState().authentication.signatureLevels.signatureLevel === ''
          ? getSignatureLevels.currentData[0].signatureLevel
          : store.getState().authentication.signatureLevels.signatureLevel,
      );
      dispatch(
        setSignatureLevels({
          ...store.getState().authentication.signatureLevels,
          companyUuid: store.getState().authentication.C_UUID!,
          signatureLevel: getSignatureLevels.currentData[0].signatureLevel,
          fileType: getSignatureLevels.currentData[0].fileType,
          companyFileType: getSignatureLevels.currentData[0].companyFileType,
          documentTerms: getSignatureLevels.currentData[0].documentTerms,
          personalTerms: getSignatureLevels.currentData[0].personalTerms,
          identityTerms: getSignatureLevels.currentData[0].identityTerms,
          channelReminder: getSignatureLevels.currentData[0].channelReminder,
          companyChannel: getSignatureLevels.currentData[0].companyChannel,
        }),
      );
    }
  }, [getSignatureLevels.currentData, getSignatureLevels.isSuccess]);
  const handleTitle = (title: KeySignatureLevel) => {
    if (title === KeySignatureLevel.SIMPLE) {
      return t(Localization('signature-level', 'signature-simple'));
    } else if (title === KeySignatureLevel.ADVANCE) {
      return t(Localization('signature-level', 'advanced-signature'));
    } else {
      return 'not implemented';
    }
  };
  const handleHoverTitle = (title: KeySignatureLevel) => {
    if (title === KeySignatureLevel.SIMPLE) {
      return 'Code OTP reçu sur le téléphone pour signer.';
    } else if (title === KeySignatureLevel.ADVANCE) {
      return `Code OTP reçu sur le téléphone et
      vérification de l'identité pour signer.`;
    } else {
      return 'not implemented';
    }
  };
  const condition = (text: KeySignatureLevel) => {
    return getSignatureLevels
      .currentData!.map(items => items.signatureLevel)
      .includes(text);
  };

  return (
    <Stack sx={{p: '1rem', backgroundColor: 'bg.main'}} alignItems={'center'}>
      <Stack
        spacing={3}
        maxWidth={{xs: '100%', sm: '80%', md: '90%'}}
        sx={{pb: '2rem'}}>
        {/** Signature level **/}
        <Stack spacing={2}>
          <NGText
            text={t(Localization('signature-level', 'set-signature-level'))}
            myStyle={{...FigmaBody.Title_Bold}}
          />

          <Stack direction={'row'} spacing={4} height={'42.29px'}>
            {/** Choose signature Levels **/}
            {getSignatureLevels.currentData && !getSignatureLevels.isLoading ? (
              dataStaticSignatureLevels.map(item => (
                <Box
                  key={item.signatureLevel}
                  onClick={() => {
                    if (
                      getSignatureLevels.currentData &&
                      condition(item.signatureLevel)
                    ) {
                      setSignatureLevel(item.signatureLevel);
                      const dataPassing = getSignatureLevels.currentData.find(
                        items => items.signatureLevel === item.signatureLevel,
                      );
                      if (dataPassing) {
                        dispatch(
                          setSignatureLevels({
                            ...store.getState().authentication.signatureLevels,
                            signatureLevel: dataPassing.signatureLevel,
                            fileType: dataPassing.fileType,
                            companyFileType: dataPassing.companyFileType,
                            companyChannel: dataPassing.companyChannel,
                            documentTerms: dataPassing.documentTerms,
                            personalTerms: dataPassing.personalTerms,
                            identityTerms: dataPassing.identityTerms,
                            channelReminder: dataPassing.channelReminder,
                          }),
                        );
                      }
                    }
                  }}
                  sx={{
                    ...StyleConstant.box.advancedSignature,
                    border:
                      signatureLevel === item.signatureLevel
                        ? `1.5px ${colorSuccess} solid`
                        : '1.5px solid #E9E9E9',
                  }}>
                  <Stack direction={'row'} alignItems={'center'}>
                    <FormControlLabel
                      value="labelSignature"
                      control={<Radio />}
                      disabled={true}
                      label={''}
                      style={{margin: 0}}
                      sx={{
                        '& .MuiRadio-colorPrimary.Mui-checked': {
                          color:
                            signatureLevel === item.signatureLevel
                              ? colorSuccess
                              : '#E9E9E9',
                          opacity: 1,
                        },
                      }}
                      checked={signatureLevel === item.signatureLevel}
                    />
                    <NGText
                      myStyle={{...FigmaBody.BodySmall}}
                      text={handleTitle(item.signatureLevel)}
                    />
                  </Stack>

                  <HtmlTooltip
                    title={
                      <NGText
                        myStyle={{...FigmaBody.BodySmallBold}}
                        text={handleHoverTitle(item.signatureLevel)}
                      />
                    }>
                    <IconButton
                      disableFocusRipple
                      disableRipple
                      disableTouchRipple>
                      <InfoIcon
                        sx={{
                          color: getSignatureLevels
                            .currentData!.map(items => items.signatureLevel)
                            .includes(item.signatureLevel)
                            ? 'blue.main'
                            : 'grey',
                        }}
                      />
                    </IconButton>
                  </HtmlTooltip>
                </Box>
              ))
            ) : (
              <Stack
                width={'100%'}
                direction={'row'}
                spacing={4}
                height={'45px'}>
                <Skeleton
                  variant="rectangular"
                  sx={{
                    ...StyleConstant.box.advancedSignature,
                    borderRadius: '6px',
                  }}
                  animation="wave"
                  height={'100%'}
                />
                <Skeleton
                  variant="rectangular"
                  sx={{
                    ...StyleConstant.box.advancedSignature,
                    borderRadius: '6px',
                  }}
                  animation="wave"
                  height={'100%'}
                />
              </Stack>
            )}
          </Stack>
        </Stack>
        <Stack spacing={2}>
          <NGText text={errorMessageSignatory} />
          <NGText
            text={t(Localization('project', 'participants'))}
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
        <Stack direction="row" gap={4}>
          {/** Switch on/off approvals order */}
          <Box
            sx={{
              padding: 1,
              width: ['100%', '100%', '50%'],
              borderRadius: 1.5,
              display: 'flex',
              bgcolor: 'white',

              alignItems: 'center',
              justifyContent: 'space-between',
              border: store.getState().authentication.project.orderApprove
                ? `1.5px ${colorSuccess} solid`
                : '1.5px solid #E9E9E9',
            }}>
            <Stack direction={'row'} alignItems={'center'} spacing={2}>
              <AntSwitch
                checked={store.getState().authentication.project.orderApprove}
                onChange={handleOrderApprove}
                overridecolor="#1C8752"
              />

              <NGText
                text={t(
                  Localization(
                    'upload-signatories',
                    'define-a-approving-order',
                  ),
                )}
                myStyle={{fontSize: 12, fontWeight: 400}}
              />
            </Stack>
            <InfoIcon sx={{color: 'blue.main'}} />
          </Box>

          {/** Switch on/off signatories order */}
          <Box
            sx={{
              padding: 1,
              width: ['100%', '100%', '50%'],
              borderRadius: 1.5,
              display: 'flex',
              bgcolor: 'white',

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
        </Stack>
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
                      <MenuItem value={Participant.Viewer}>
                        <NGText
                          text={t(Localization('upload-signatories', 'viewer'))}
                          myStyle={{fontSize: 14, fontWeight: 400}}
                        />
                      </MenuItem>
                      <MenuItem value={Participant.Approval}>
                        <NGText
                          text={t(
                            Localization('upload-signatories', 'approval'),
                          )}
                          myStyle={{fontSize: 14, fontWeight: 400}}
                        />
                      </MenuItem>
                      <MenuItem value={Participant.Signatory}>
                        <NGText
                          text={t(
                            Localization('upload-signatories', 'signatory'),
                          )}
                          myStyle={{fontSize: 14, fontWeight: 400}}
                        />
                      </MenuItem>
                      <MenuItem value={Participant.Receipt}>
                        <NGText
                          text={t(
                            Localization('upload-signatories', 'receipt'),
                          )}
                          myStyle={{fontSize: 14, fontWeight: 400}}
                        />
                      </MenuItem>
                    </NGInputField>
                  </Stack>
                  {/* Email field */}
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
                      placeholder={
                        t(Localization('form', 'email-participant'))!
                      }
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
              {fields.length > 1 && (
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
                    const signatoryId = getValues(`recipients.${index}.id`);
                    if (signatoryId) {
                      await deleteSignatory({id: signatoryId})
                        .unwrap()
                        .then(() => {
                          remove(index);
                          const tempCheck = [...check];
                          setCheck(tempCheck);
                        });
                    } else {
                      remove(index);
                      const tempCheck = [...check];
                      setCheck(tempCheck);
                    }
                  }}>
                  <NGBinIcon
                    sx={{color: 'red', width: '15px', height: '15px'}}
                  />
                </IconButton>
              )}
            </Stack>
          ))}
        <Stack
          direction={'row'}
          justifyContent={'space-between'}
          flexWrap={'wrap'}>
          <Stack>
            <Button
              endIcon={<AddOutlinedIcon sx={{color: 'Primary.main'}} />}
              sx={{fontWeight: 600, textTransform: 'inherit'}}
              onClick={() => {
                const data = getValues('recipients');

                setCountry([
                  ...country,
                  {
                    callingCode: '33',
                    name: 'France',
                    code: 'FR',
                  },
                ]);
                setToggle([...selectToggle, {phoneNumber: false}]);
                setCheck([...check, {active: false}]);

                append({
                  firstName: '',
                  lastName: '',
                  role: Participant['Signatory'],
                  email: '',
                  phone: '',
                  sortOrder: Math.max(...data.map(d => d.sortOrder!)) + 1,
                });
              }}>
              <NGText
                text={t(Localization('upload-signatories', 'add-a-recipient'))}
                myStyle={{fontSize: 13, fontWeight: 600}}
              />
            </Button>
          </Stack>
        </Stack>
      </Stack>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={uploadLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

export default UploadRecipient;
