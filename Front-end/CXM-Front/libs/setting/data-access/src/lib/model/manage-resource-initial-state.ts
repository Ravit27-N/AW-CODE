import { ResourceResponse } from './resource.response';
import { CreateResourcePayload } from './create-resource.payload';
import { ListCriteriaState } from './list-criteria-state';
import { ResourceCriteriaResponse } from './resource-criteria.response';

export interface ManageResourceInitialState {
  resources: ResourceResponse[];
  form: CreateResourcePayload;
  listCriteria: ListCriteriaState;
  isHasFilter: boolean;
  isSearchBoxError: boolean;
  resourceCriteria: ResourceCriteriaResponse[];
  messages: any;
  popupForm: {
    label: string,
    resourceType: string,
    fileSize: string,
    fileName: string,
    fileId: string,
    pageNumber: number,
    fileSizePayload: number,
    files: File[],
    isLabelDuplicate: boolean,
    isLabelIsChecking: boolean,
  },
  isCloseModal: boolean,
  deleteFileId: string,
}
