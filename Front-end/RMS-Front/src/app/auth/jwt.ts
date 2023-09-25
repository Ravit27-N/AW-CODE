export interface IToken {
  // eslint-disable-next-line @typescript-eslint/naming-convention
  resource_access: Record<string, any>;
  aud: string | string[];
}

export const parseJwt = (token): IToken => {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(atob(base64).split('').map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));
  return JSON.parse(jsonPayload);
};
