import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import {
  clearDepositFlow,
  deleteFlowDepositAfterDocumentNoOK,
  selectDocumentNoOK,
  unloadUploadFileAction
} from '@cxm-smartflow/flow-deposit/data-access';

export interface ILockableForm {
  isLocked(): Observable<boolean>;

  getLockedReason(): string[];
}

export const acceptFlowDepositURLs = [
  '/cxm-deposit/pre-analysis',
  '/cxm-deposit/analysis-result',
  '/cxm-deposit/production-criteria',
  '/cxm-deposit/finished',
  '/cxm-deposit/validate-result'
];

@Injectable()
export class LockableFormGuardService implements CanDeactivate<ILockableForm>, OnDestroy {

  documentNoOK$ = new BehaviorSubject(false);
  destroyed$ = new Subject<boolean>();

  canDeactivate(
    component: ILockableForm,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | boolean
    | UrlTree
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree> {
    return component
      .isLocked()
      .pipe(
        switchMap((isLocked) => {
          // Validate lock component.
          const nonLockURLs = !acceptFlowDepositURLs.some(e => nextState?.url?.includes(e));
          if (nonLockURLs && this.documentNoOK$.value) { // If document has no ok, we no need to show confirmation message before leave current page.
             this.deleteFlowDeposit().subscribe(() => {
              this.store.dispatch(unloadUploadFileAction());
              this.store.dispatch(clearDepositFlow());
            });

             return of(true);
          } else {
            const nl = nextState?.root.queryParams.nl; // f: force to next route state
            if(nl) return of(true)

            return nonLockURLs && isLocked ? this.validateMessage(component.getLockedReason(), '') : of(true);
          }
        }));
  }

  validateMessage = (params: any, title: string) => {
    const response = this.confirmService.showConfirmationPopup({
      icon: 'feedback',
      title: params[0],
      message: params[1],
      confirmButton: params[2],
      cancelButton: params[3],
      type: 'Warning'
    });

    response?.subscribe((yesNo) => {
      if (yesNo) {
        this.store.dispatch(unloadUploadFileAction());
        this.store.dispatch(clearDepositFlow());
      }
    });
    return response;
  };

  private deleteFlowDeposit(): Observable<boolean> {
    // get fileId from url.
    const fileId = this.route.snapshot.queryParamMap?.get('fileId') || '';
    this.store.dispatch(deleteFlowDepositAfterDocumentNoOK({ fileId: fileId }));
    return of(true);
  }

  constructor(
    private readonly confirmService: ConfirmationMessageService,
    private readonly store: Store,
    private route: ActivatedRoute
  ) {
    this.store.select(selectDocumentNoOK).subscribe(noOK => this.documentNoOK$.next(noOK));
  }

  ngOnDestroy(): void {
    this.documentNoOK$.complete();
    this.destroyed$.complete();
    this.store.complete();
  }
}
