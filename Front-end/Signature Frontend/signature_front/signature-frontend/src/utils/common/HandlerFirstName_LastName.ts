import {IGetFlowId} from '@/redux/slides/project-management/project';

export const handlerFirstNameAndLastName = ({data}: {data: IGetFlowId}) => {
  return (
    data.creatorInfo.firstName.charAt(0).toUpperCase() +
    data.creatorInfo.firstName.slice(1, data.creatorInfo.firstName.length) +
    ' ' +
    data.creatorInfo.lastName.charAt(0).toUpperCase() +
    data.creatorInfo.lastName.slice(1, data.creatorInfo.lastName.length)
  );
};

export const getFirstNameAndLastName = (name: string) => {
  let username = '';
  if (!name.includes(' ')) {
    return name;
  }

  name
    .split(' ')
    .forEach(
      n => (username += `${n.charAt(0).toUpperCase()}${n.slice(1, n.length)} `),
    );

  return username;
};

export const getParaphFirstNameAndLastName = (name: string) => {
  let username = '';
  if (!name.includes(' ')) {
    return name;
  }

  name
    .split(' ')
    .forEach(
      n => (username += `${n.charAt(0).toUpperCase()}${n.slice(1, n.length)}`),
    );

  return username;
};

export const getNameByFirstIndex = (name: string): string => {
  let username = '';

  if (!name.includes(' ')) {
    return name.charAt(0).toUpperCase();
  }

  name.split(' ').forEach(n => (username += `${n.charAt(0).toUpperCase()}`));

  return username;
};
