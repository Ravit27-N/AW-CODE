import {EXTENSION_ACCEPT} from '@constant/NGContant';

export const extensionAccept = (Name: string) => {
  const name = Name.toLowerCase().split('.');
  return EXTENSION_ACCEPT.includes(name[name.length - 1]);
};
