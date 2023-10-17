export type IStatusCode = 400 | 401 | 403 | 404 | 406 | 500;

export const HandleException = (status: IStatusCode): string => {
  const exception = {
    400: 'Bad request',
    401: 'Unauthorize',
    403: 'Forbidden',
    404: 'Not found',
    406: 'Not accepted',
    500: 'Internal server error',
  };

  return exception[status];
};
