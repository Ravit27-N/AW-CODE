import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NEVER, Observable, Subject, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

type ErrorActionCallback = () => void;

export interface IErrorMessage {
  message?: string;
  error?: string;
  subErrors?: any[] | string;
  code?: number;
  level?: string;
  action?: ErrorActionCallback;
  actionName?: string;
}

@Injectable()
export class ErrorMessageHandler {
  private subject$ = new Subject<IErrorMessage>();
  public errorMessages = this.subject$.asObservable();

  dispatch(message: IErrorMessage): void {
    this.subject$.next(message);
  }
}


@Injectable()
export class HttpErrorHandlerInterceptor implements HttpInterceptor {

  private readonly customErrMSg = 'Something went wrong!!';

  constructor(private handler: ErrorMessageHandler) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      map((event: HttpEvent<any>) => event),
      // catchError((httpError: HttpErrorResponse) => {
      //   this.handler.dispatch(this.errorMessage(httpError));
      //   if (environment.production) {
      //     console.log('Application error: ' + httpError.message);
      //     return NEVER;
      //   } else {
      //     return throwError(httpError);
      //   }
      // })
    );
  }

  private errorMessage(http: HttpErrorResponse): IErrorMessage {
    let errMessage: IErrorMessage = {
      code: http.status,
      actionName: 'close'
    };

    if (http.error && http.error.apierror) {
      errMessage = { ...errMessage, error: this.customErrMSg,
      subErrors: this.customErrMSg };
    } else {
      errMessage = { ...errMessage, error: this.customErrMSg };
    }
    if (http.status === 400 || http.status === 409) {
      return { ...errMessage, message: 'Invalid user action' };
    } else if (http.status === 401) {
      return { ...errMessage, message: 'Session expired: Re-login to continues'};
    } else if (http.status === 403) {
      return { ...errMessage, message: 'Forbidden: You don\'t have permission to perform this action' };
    } else if (http.status === 404) {
      return { ...errMessage, message: 'The requested content is no longer exist' };
    } else if (http.status === 422) {
      return { ...errMessage, message: 'Invalid request' };
    } else if (http.status === 500) {
      return { ...errMessage, message: 'Something went wrong. cannot communicat with server' };
    } else {
      return {
        ...errMessage, message: 'Congratulations you found a secret error'
      };
    }
  }
}
