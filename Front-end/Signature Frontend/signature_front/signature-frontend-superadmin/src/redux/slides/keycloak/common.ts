import jwt_decode from 'jwt-decode';

export const jwtDecode = (t: string) => {
  return jwt_decode(t);
};
