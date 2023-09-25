import { VisibilityLevel } from './visibility-level.enumeration';
import { ModificationLevel } from './modification-level.enumeration';
import { FunctionalityEnumeration } from './functionality.enumeration';

export interface Privilege {
  key: string;
  modificationLevel: ModificationLevel;
  visibilityLevel: VisibilityLevel;
  modificationUsers?: string [];
  visibilityUsers?: string [];
  modificationOwners?: number[];
  visibilityOwners?: number[];
}

export interface Functionality {
  functionalityKey: FunctionalityEnumeration;
  modificationLevel: ModificationLevel;
  visibilityLevel: VisibilityLevel;
  privileges: Privilege[];
}

export interface UserPrivilegeModel {
  id: number;
  admin: boolean;
  displayName?: string;
  name?: string;
  functionalities?: Functionality[];
}
