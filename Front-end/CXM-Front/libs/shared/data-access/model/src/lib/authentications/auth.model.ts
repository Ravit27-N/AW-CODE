import { UserInfo } from 'angular-oauth2-oidc';

export interface LoginModel {
  username: string;
  password: string;
}

export interface AuthenticationAttemptsRequest {
  userName: string;
}

export interface AuthenticationAttempts {
  currentCountOfRemainingAttempts: number;
  minutesRemaining: number;
  isBlocked: boolean;
  forceToChangePassword: boolean;
}

export interface UserLoginAttempt {
  userName: string;
  loginStatus: boolean;
  password: string
}

export interface UserInfoAndAuthorizedToAuthenticate extends UserInfo {
  forceToChangePassword?: boolean;
}

export interface keyCloakModel {
  sub?: string;
  email_verified?: boolean;
  name?: string;
  preferred_username?: string;
  given_name?: string;
  family_name?: string;
  email?: string;
  forceToChangePassword?: boolean;
}

export class AuthenticationConstant {
  public static USER_PRIVILEGES = 'userPrivileges';
}
