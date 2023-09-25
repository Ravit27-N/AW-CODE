import { AwStepModel } from '../../shared/components/aw-step';

export const addCandidateSteps: AwStepModel[] = [
  {
    title: '1. Information',
    active: true,
    disabled: false,
  },
  {
    title: '2. Education',
    active: false,
    disabled: true,
  },
  {
    title: '3. Experience',
    active: false,
    disabled: true,
  },
  {
    title: '4. Upload CV',
    active: false,
    disabled: true,
  },
  {
    title: '5. Preview',
    active: false,
    disabled: true,
  },
];


export const pageSubtitle: string[] = [
  'Fill in the information for the candidate',
  'Fill in the education for the candidate',
  'Fill in the experience for the candidate',
  'Please upload a CSV',
  'Preview candidate details',
];
