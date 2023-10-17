import NGDialog from '@/components/ng-dialog-corporate/NGDialog';

import {
  AutoReminder,
  ChannelOptions,
  CreateModelStep,
  currentProjectIdKey,
  KeySignatureLevel,
  NotificationValue,
  Participant,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  clearApproval,
  clearReceptient,
  clearSignatory,
  clearViewers,
  resetToInitial,
  setActiveActorRole,
  storeAnnotationExist,
  storeApprovals,
  storeEnvoiByRole,
  storeProject,
  storeRecipient,
  storeSignatories,
  storeViewers,
} from '@/redux/slides/authentication/authenticationSlide';
import {
  IBodyProjectDetail,
  useUpdateProjectMutation,
  useUpdateProjectStepFourMutation,
} from '@/redux/slides/project-management/project';
import {router} from '@/router';
import {Navigate} from '@/utils/common';
import {
  ErrorServer,
  HandleException,
  IResponseServer,
} from '@/utils/common/HandleException';
import {getTemporaryParticipants} from '@/utils/common/SignatureProjectCommon';
import {splitUserCompany} from '@/utils/common/String';
import {Route} from '@constant/Route';
import {Backdrop, CircularProgress} from '@mui/material';
import ProjectEnvoi from '@pages/end-user/sidebar/models/form/create-project-by-template/ProjectEnvoi';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React from 'react';
import {useForm} from 'react-hook-form';
import {useParams} from 'react-router';
import ProjectPreparation from './ProjectPreparation';
import TopNav from './top-nav/TopNav';

type ICreateProject = {
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

export type IPayloadDetail = {
  name: string;
  typeModel: string;
  department: string;
  service: Array<IServiceSelect>;
};

export type IPayloadScenario = {
  signature: number;
  approval: number;
  recipient: number;
  tag: 1 | 2;
};

export type IPayloadParameters = {
  purpose: string;
  message: string;
  dateExpired: Dayjs | null;
  period: number;
  day: 1 | 2 | 3;
  channel: ChannelOptions;
  autoReminder: AutoReminder;
  dayExpired: number;
};

export type IServiceSelect = {
  id: number;
  unitName: string;
};

export type ISubmitIndex = {
  preparation: boolean;
  documentDetail: boolean;
  shipping: boolean;
};

const getNotification = (n: number) => {
  return NotificationValue[n];
};

const CreateProject = (props: ICreateProject) => {
  const {trigger, setTrigger} = props;
  const dispatch = useAppDispatch();
  const {enqueueSnackbar} = useSnackbar();
  const param = useParams();
  /** get company UUID form redux **/
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const [optionReminder, setOptionReminder] = React.useState(true);
  const {
    project,
    signatories,
    approvals,
    recipients,
    viewers,
    storeModel,
    annotations,
  } = useAppSelector(state => state.authentication);
  const [updateProject, {isLoading: uploadSignatoriesLoading}] =
    useUpdateProjectMutation();
  const [updateStepFour, {isLoading: uploadEnvoiLoading}] =
    useUpdateProjectStepFourMutation();

  const [activeStep, setActiveStep] = React.useState(0);
  const [submitIndex, setSubmitIndex] = React.useState<ISubmitIndex>({
    preparation: false,
    documentDetail: false,
    shipping: false,
  });

  // ** hook form
  const {
    control: envoiControl,
    formState: {errors: envoiErrors},
    watch,
    setValue: envoiSetValue,
    getValues: envoiValues,
  } = useForm<IPayloadParameters & {projectName: string}>({
    defaultValues: {
      projectName: '',
      purpose: '',
      message: '',
      dateExpired: dayjs(
        new Date(Date.now())
          .toLocaleDateString('zh-Hans-CN', {
            month: '2-digit',
            day: '2-digit',
            year: 'numeric',
          })
          .replace(/\//g, '-'),
      ),
      period: 0,
      day: 1,
      dayExpired: 0,
      channel: 1,
      autoReminder: undefined,
    },
  });

  const {
    control: parameterControl,
    formState: {errors: parameterErrors},
    setValue: parameterSetValue,
    reset: parametersReset,
    getValues: parameterValues,
  } = useForm<IPayloadParameters>({
    defaultValues: {
      purpose: '',
      message: '',
      dateExpired: dayjs(
        new Date(Date.now())
          .toLocaleDateString('zh-Hans-CN', {
            month: '2-digit',
            day: '2-digit',
            year: 'numeric',
          })
          .replace(/\//g, '-'),
      ),
      period: 0,
      day: 1,
      channel: 1,

      autoReminder: undefined,
    },
  });

  /** return page content step Preparation/Shipping  */
  const returnContent = (): JSX.Element => {
    let content: JSX.Element = (
      <ProjectPreparation
        setSubmitIndex={setSubmitIndex}
        setActiveStep={setActiveStep}
        submitIndex={submitIndex}
        control={parameterControl}
        errors={parameterErrors}
        getValues={parameterValues}
        setValue={parameterSetValue}
        setOptionReminder={setOptionReminder}
      />
    );
    if (activeStep === CreateModelStep.STEP1) {
      content = (
        <ProjectEnvoi
          control={envoiControl}
          errors={envoiErrors}
          getValues={envoiValues}
          setValue={envoiSetValue}
          watch={watch}
        />
      );
    }

    return content;
  };

  /** submit fill-form preparation  */
  React.useEffect(() => {
    const uploadSignatories = async () => {
      const storeData = [
        ...signatories,
        ...approvals,
        ...recipients,
        ...viewers,
      ];
      const totalParticipant =
        storeModel!.signatories +
        storeModel!.approvals +
        storeModel!.recipient +
        storeModel!.viewer;
      try {
        if (!('projectId' in param)) {
          enqueueSnackbar(t(Localization('upload-document', 'empty-file')), {
            variant: 'errorSnackbar',
          });
          return setSubmitIndex(prev => ({...prev, preparation: false}));
        }

        if (storeData.length !== totalParticipant) {
          enqueueSnackbar(
            t(
              Localization(
                'create-project-by-template',
                'fill-all-participants',
              ),
            ),
            {
              variant: 'errorSnackbar',
            },
          );
          return setSubmitIndex(prev => ({...prev, preparation: false}));
        }
        const data = await updateProject({
          details: [],
          orderSign: project.orderSign,
          orderApprove: project.orderApprove,
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          signatories: storeData.map(
            ({fillForm, projectId, id, ...rest}, index) => {
              return {
                ...rest,
                projectId: project.id!,
                id: Number(project.step) > 1 ? id : '',
              };
            },
          ),
          signatureLevel: KeySignatureLevel.SIMPLE,
          name: project.name!,
          step: 2,
          id: project.id as string | number,
        }).unwrap();
        dispatch(clearSignatory());
        dispatch(clearApproval());
        dispatch(clearReceptient());
        dispatch(clearViewers());
        const {
          signatories,
          details,
          id: projectId,
          name,
          orderApprove,
          orderSign,
          step,
        } = data;
        const {role, id, firstName, lastName, email} = signatories[0];
        dispatch(
          setActiveActorRole({
            role: role as Participant,
            id: Number(id),
            signatoryName: firstName + ' ' + lastName,
            email,
          }),
        );
        dispatch(
          storeEnvoiByRole({
            signatories,
            projectDetails: details,
          }),
        );
        dispatch(
          storeProject({
            project: {
              id: projectId.toString(),
              name,
              orderSign,
              orderApprove,
              step,
            },
          }),
        );
        const tempParticipants = getTemporaryParticipants(signatories);
        dispatch(storeSignatories({data: tempParticipants.tempSignatories}));
        dispatch(storeApprovals({data: tempParticipants.tempApprovals}));
        dispatch(storeViewers({data: tempParticipants.tempViewers}));
        dispatch(storeRecipient({data: tempParticipants.tempRecipients}));
        dispatch(
          storeAnnotationExist({signatories: tempParticipants.tempSignatories}),
        );
        setSubmitIndex(prev => ({
          ...prev,
          preparation: false,
          documentDetail: true,
        }));
      } catch (error) {
        enqueueSnackbar(ErrorServer(error as IResponseServer) ?? UNKOWNERROR, {
          variant: 'errorSnackbar',
        });
        setSubmitIndex(prev => ({...prev, preparation: false}));
      }
    };
    if (submitIndex.preparation) {
      uploadSignatories().then(r => r);
    }
  }, [submitIndex.preparation]);

  /** submit fill-form shipping */
  React.useEffect(() => {
    const uploadShipping = async () => {
      const {selectEnvoiData, project, activeActorEnvoi} =
        store.getState().authentication;
      const details: IBodyProjectDetail[] = [];
      Object.entries(selectEnvoiData!).forEach(([key, value]) => {
        const detail = key === activeActorEnvoi?.role!;
        if (detail) {
          details.push({
            id: value.id,
            type: key as Participant,
            projectId: Number(project.id!),
            titleInvitation: envoiValues('purpose'),
            messageInvitation: envoiValues('message'),
          });
        } else {
          details.push({
            id: value.id,
            type: key as Participant,
            projectId: Number(project.id!),
            titleInvitation:
              value.title === ''
                ? storeModel!.templateMessage.titleInvitation
                : value.title,
            messageInvitation:
              value.description === ''
                ? storeModel!.templateMessage.messageInvitation
                : value.description,
          });
        }
      });
      try {
        await updateStepFour({
          id: project.id as string | number,
          step: '4',
          name: envoiValues('projectName') || 'Signature',
          status: '1',
          signatories: [],
          documents: [],
          details,
          orderSign: store.getState().authentication.project.orderSign,
          expireDate: envoiValues('dateExpired')!.toISOString(),
          autoReminder: !!envoiValues('autoReminder') ?? false,
          channelReminder: envoiValues('channel'),
          reminderOption: envoiValues('autoReminder') ?? null,
          signatureLevel: KeySignatureLevel.SIMPLE,
          setting: {
            companyUuid: company.companyUuid,
            signatureLevel: KeySignatureLevel.SIMPLE,
            personalTerms:
              'Pour continuer, consultez les conditions générales d’utilisation de la plateforme.',
            channelReminder: getNotification(Number(envoiValues('channel'))),
          },
        }).unwrap();
        dispatch(resetToInitial());
        dispatch(clearApproval());
        dispatch(clearSignatory());
        dispatch(clearReceptient());
        dispatch(clearViewers());
        setActiveStep(0);
        localStorage.setItem(currentProjectIdKey, project.id!);
        return router.navigate(
          Navigate(Route.project.projectDetail + '/' + project.id),
        );
      } catch (error) {
        enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
          variant: 'errorSnackbar',
        });
        setSubmitIndex(prev => ({...prev, shipping: false}));
      }
    };

    if (submitIndex.shipping) {
      uploadShipping().then(r => r);
    }
  }, [submitIndex.shipping]);

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
          setSubmitIndex={setSubmitIndex}
        />
      }
      contentDialog={returnContent()}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={uploadSignatoriesLoading || uploadEnvoiLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </NGDialog>
  );
};

export default CreateProject;
