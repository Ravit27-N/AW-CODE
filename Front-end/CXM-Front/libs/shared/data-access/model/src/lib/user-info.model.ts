export interface UserAssignService{
  id: number;
  name: string;
  //add new
  divisionName: string;
  clientName:string;
}

export interface UserAssignProfile{
  id: number;
  name: string;
}

export interface UserInfoModel{
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  currentPassword?: string;
  newPassword?: string;
  confirmPassword?: string;
  service: UserAssignService;
  profiles: UserAssignProfile[];
}

export interface UserInFoModelForm{
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
  actionType: PasswordActionType
}

export enum PasswordActionType {
  UNBLOCKED = "UNBLOCKED",
  RESET = "RESET"
}