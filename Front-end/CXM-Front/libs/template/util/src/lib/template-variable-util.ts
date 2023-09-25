import { Observable, of } from 'rxjs';

export const variableTemp = 'variableTemp';

export const getVariableTemp = (): Observable<string[]> => {
  const variables = JSON.parse(<string>localStorage.getItem(variableTemp));
  return of(variables);
};

export const keepVariableTemp = (variables: string[]): void => {
  localStorage.setItem(variableTemp, JSON.stringify(variables));
};

export const removeVariableTemp = (): Observable<boolean> => {
  localStorage.removeItem(variableTemp);
  return of(true);
};
