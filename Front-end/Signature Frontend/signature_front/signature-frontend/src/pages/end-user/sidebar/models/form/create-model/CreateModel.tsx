import NGDialog from '@/components/ng-dialog-corporate/NGDialog';

import {
  AutoReminder,
  ChannelOptions,
  CreateModelStep,
  SIGNING_PROCESS,
} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {storeCreateModel} from '@/redux/slides/authentication/authenticationSlide';
import {
  useCreateTemplateMutation,
  useUpdateTemplateMutation,
} from '@/redux/slides/profile/template/templateSlide';
import {router} from '@/router';
import {Navigate} from '@/utils/common';
import {HandleException} from '@/utils/common/HandleException';
import {splitUserCompany} from '@/utils/common/String';
import {Backdrop, CircularProgress} from '@mui/material';
import dayjs, {Dayjs} from 'dayjs';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React from 'react';
import {FormProvider, useForm} from 'react-hook-form';
import {useParams} from 'react-router-dom';
import ModelDetail from './ModelDetail';
import ModelParameter from './ModelPatameter';
import ModelScenario from './ModelScenario';
import TopNav from './top-nav/TopNav';
import {CorporateModelFolder} from '@/redux/slides/corporate-admin/corporateSettingSlide';

type ICreateModel = {
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

export type IPayloadDetail = {
  name: string;
  typeModel: CorporateModelFolder | null;
  department: string;
  // service: Array<IServiceSelect>;
};

export type IPayloadScenario = {
  signature: number;
  approval: number;
  recipient: number;
  viewer: number;
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
  detail: boolean;
  scenario: boolean;
  parameters: boolean;
};

const CreateModel = (props: ICreateModel) => {
  const {trigger, setTrigger} = props;
  const param = useParams();
  const {enqueueSnackbar} = useSnackbar();
  const {createModel} = useAppSelector(state => state.authentication);
  const [optionReminder, setOptionReminder] = React.useState(true);
  const [createTemplate, {isLoading: createModelLoading}] =
    useCreateTemplateMutation();
  const [updateTemplate, {isLoading: updateModelLoading}] =
    useUpdateTemplateMutation();
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const [activeStep, setActiveStep] = React.useState(0);
  const [submitIndex, setSubmitIndex] = React.useState<ISubmitIndex>({
    detail: false,
    scenario: false,
    parameters: false,
  });
  // ** detail form control
  const detailMethods = useForm<IPayloadDetail>({
    defaultValues: {
      name: '',
      typeModel: null,
      department: '',
      // service: [],
    },
  });

  const {reset: detailReset, handleSubmit: detailHandleSubmit} = detailMethods;

  // ** scenario form control
  const {
    control: scenarioControl,
    formState: {errors: scenarioErrors},
    setValue: scenarioSetValue,
    reset: scenarioReset,
    handleSubmit: scenarioHandleSubmit,
    getValues: scenarioValues,
  } = useForm<IPayloadScenario>({
    defaultValues: {
      signature: 0,
      approval: 0,
      recipient: 0,
      viewer: 0,
      tag: 1,
    },
  });

  // ** parameters form control
  const {
    control: parameterControl,
    formState: {errors: parameterErrors},
    setValue: parameterSetValue,
    reset: parametersReset,
    handleSubmit: parameterHandleSubmit,
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
      dayExpired: 0,
      autoReminder: undefined,
    },
  });

  React.useEffect(() => {
    if (createModel) {
      const {signProcess} = createModel;
      if (signProcess) {
        setActiveStep(CreateModelStep.STEP2);
      } else {
        setActiveStep(CreateModelStep.STEP1);
      }
    }
  }, [createModel]);

  const returnContent = (): JSX.Element => {
    let content: JSX.Element = (
      <FormProvider {...detailMethods}>
        <ModelDetail />
      </FormProvider>
    );
    if (activeStep === CreateModelStep.STEP1) {
      content = (
        <ModelScenario
          control={scenarioControl}
          errors={scenarioErrors}
          setValue={scenarioSetValue}
          getValues={scenarioValues}
        />
      );
    } else if (activeStep === CreateModelStep.STEP2) {
      content = (
        <ModelParameter
          setOptionReminder={setOptionReminder}
          control={parameterControl}
          errors={parameterErrors}
          setValue={parameterSetValue}
          getValues={parameterValues}
        />
      );
    }

    return content;
  };

  // ** Submit detail form
  const handleSubmitDetail = async (data: IPayloadDetail) => {
    const {name, department, typeModel} = data;
    let templateId = null;
    if ('templateId' in param) {
      templateId = param.templateId;
    }

    try {
      let data = null;
      if (templateId) {
        data = await updateTemplate({
          id: Number(templateId),
          level: 3,
          format: 1,
          name,
          templateServicesIds: [], //service.map(({id}) => id),
          businessUnitId: Number(department),
          folderId: Number(typeModel?.id),
          companyId: Number(company.companyId),
        }).unwrap();
      } else {
        data = await createTemplate({
          id: null,
          level: 3,
          format: 1,
          name,
          templateServicesIds: [], //service.map(({id}) => id),
          businessUnitId: Number(department),
          folderId: Number(typeModel?.id),
          companyId: Number(company.companyId),
        }).unwrap();
      }

      store.dispatch(storeCreateModel(data));
      setActiveStep(p => p + 1);
      router.navigate(`${Route.MODEL}/template/${data.id}`);
    } catch (error) {
      const {status} = error as any;
      enqueueSnackbar(HandleException(status), {
        variant: 'errorSnackbar',
      });
      return error;
    }
  };

  // ** Submit scenario form
  const handleSubmitScenario = async (data: IPayloadScenario) => {
    const {approval, recipient, signature, tag, viewer} = data;
    try {
      if (signature < 1) {
        return enqueueSnackbar(
          t(
            Localization(
              'upload-document',
              'please-add-at-least-one-signatory',
            ),
          ),
          {
            variant: 'errorSnackbar',
          },
        );
      }
      const data = await updateTemplate({
        id: createModel?.id!,
        approval,
        signature,
        recipient,
        viewer,
        signProcess:
          tag === 1 ? SIGNING_PROCESS.COSIGN : SIGNING_PROCESS.COUNTER_SIGN,
      }).unwrap();
      store.dispatch(storeCreateModel(data));
      setActiveStep(p => p + 1);
    } catch (error) {
      const {status} = error as any;
      enqueueSnackbar(HandleException(status), {
        variant: 'errorSnackbar',
      });
      return error;
    }
  };

  // ** Submit parameters form
  const handleSubmitParameters = async (data: IPayloadParameters) => {
    const {purpose, message, channel, autoReminder, dayExpired} = data;
    const getNotificationService = (c: 1 | 2 | 3): string => {
      const channel = {
        1: 'sms_email',
        2: 'email',
        3: 'sms',
      };

      return channel[c];
    };
    try {
      const res = await updateTemplate({
        id: createModel?.id!,
        templateMessage: {
          titleInvitation: purpose,
          messageInvitation: message,
          expiration: dayExpired,
          sendReminder: !optionReminder ? autoReminder : null,
        },
        notificationService: getNotificationService(channel),
      }).unwrap();
      detailReset();
      scenarioReset();
      parametersReset();
      setTrigger(false);
      store.dispatch(storeCreateModel(null));
      setActiveStep(0);
      enqueueSnackbar(
        t(Localization('models-corporate', 'template-create-successfully'), {
          name: res.name,
        }),
        {
          variant: 'successSnackbar',
        },
      );
      return router.navigate(Navigate(Route.MODEL));
    } catch (error) {
      const {status} = error as any;
      enqueueSnackbar(HandleException(status), {
        variant: 'errorSnackbar',
      });
      return error;
    }
  };

  React.useEffect(() => {
    if (submitIndex.detail) {
      detailHandleSubmit(handleSubmitDetail)();
    } else if (submitIndex.scenario) {
      scenarioHandleSubmit(handleSubmitScenario)();
    } else if (submitIndex.parameters) {
      parameterHandleSubmit(handleSubmitParameters)();
    }
  }, [submitIndex]);

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
          detailReset={detailReset}
          scenarioReset={scenarioReset}
          parametersReset={parametersReset}
          activeStep={activeStep}
          setActiveStep={setActiveStep}
          setTrigger={setTrigger}
          setSubmitIndex={setSubmitIndex}
        />
      }
      contentDialog={returnContent()}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={createModelLoading || updateModelLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </NGDialog>
  );
};

export default CreateModel;
