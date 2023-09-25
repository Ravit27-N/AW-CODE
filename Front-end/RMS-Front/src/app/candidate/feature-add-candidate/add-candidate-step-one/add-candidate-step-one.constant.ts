import { KeyValue } from '@angular/common';

export const salutationCriteria: KeyValue<string, any>[] = [
  {
    key: 'Mr.',
    value: 'Mr.',
  },
  {
    key: 'Ms.',
    value: 'Ms.',
  },
  {
    key: 'Mrs.',
    value: 'Mrs.',
  },
  {
    key: 'Miss.',
    value: 'Miss.',
  },
  {
    key: 'Dr.',
    value: 'Dr.',
  },
  {
    key: 'Prof.',
    value: 'Prof.',
  },
  {
    key: 'Rev',
    value: 'Rev',
  },
];

export const genderCriteria: KeyValue<any, any>[] = [
  {
    key: 'Male',
    value: 'Male',
  },
  {
    key: 'Female',
    value: 'Female',
  },
];

export const priorityCriteria: KeyValue<any, any>[] = [
  {
    key: 'High',
    value: 'High',
  },
  {
    key: 'Normal',
    value: 'Normal',
  },
  {
    key: 'Low',
    value: 'Low',
  },
];
