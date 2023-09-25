import { CandidateInformation } from './add-candidate-step-one/add-candidate-step-one.model';

export interface AddCandidateModel extends CandidateInformation {
  currentStep: number;
}
