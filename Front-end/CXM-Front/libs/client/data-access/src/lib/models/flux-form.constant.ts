import {
  IdentificationModeForm,
  IDepositMode,
  IDepositModeForm,
} from './client';
import { CriteriaDistributionFormModel } from './criteria-distribution-form-model';

export const DepositModesConstant: Array<IDepositModeForm> = [
  {
    key: 'flow.traceability.deposit.mode.portal',
    value: 'Portal',
    scanActivation: false,
    disabled: true,
  },
  {
    key: 'flow.traceability.deposit.mode.iv',
    value: 'IV',
    scanActivation: false,
    disabled: false,
  },
  {
    key: 'flow.traceability.deposit.mode.api',
    value: 'API',
    scanActivation: false,
    disabled: false,
  },
  {
    key: 'flow.traceability.deposit.mode.batch',
    value: 'Batch',
    scanActivation: false,
    disabled: false,
  },
];

export const IdentificationModesConstant: IdentificationModeForm = {
  key: 'flow.traceability.deposit.identification_mode.configuration_file',
  checked: false,
  disabled: false,
  value: false,
};

export const DepositModes: IDepositMode[] = [
  {
    key: 'flow.traceability.deposit.mode.portal',
    value: 'Portal',
    scanActivation: false,
  },
  {
    key: 'flow.traceability.deposit.mode.iv',
    value: 'IV',
    scanActivation: false,
  },
  {
    key: 'flow.traceability.deposit.mode.api',
    value: 'API',
    scanActivation: false,
  },
  {
    key: 'flow.traceability.deposit.mode.batch',
    value: 'Batch',
    scanActivation: false,
  },
];

export const CriteriaDistributionFormConstant: Array<CriteriaDistributionFormModel> =
  [
    {
      name: 'Postal',
      key: 'client.distribution_criteria_postal',
      enabled: false,
      active: false,
      categories: [],
      manageable: false,
    },
    {
      name: 'Digital',
      key: 'client.distribution_criteria_digital',
      enabled: false,
      active: true,
      manageable: true,
      categories: [
        {
          key: 'client.distribution_category_email',
          name: 'Email',
          active: false,
          enabled: false,
        },
        {
          key: 'client.distribution_category_sms',
          name: 'Sms',
          active: false,
          enabled: false,
        },
        {
          key: 'client.distribution_category_cse',
          name: 'CSE',
          active: false,
          enabled: true,
        },
        {
          key: 'client.distribution_category_cse_ar',
          name: 'CSE AR',
          active: false,
          enabled: true,
        },
        {
          key: 'client.distribution_category_lre',
          name: 'LRE',
          active: false,
          enabled: true,
        },
      ],
    },
  ];
