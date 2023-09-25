import {Injectable} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Store} from '@ngrx/store';
import {filter, take} from 'rxjs/operators';
import {updateStatusToFinalize} from '@cxm-smartflow/flow-deposit/data-access';

@Injectable({
  providedIn: 'root'
})
export class FlowDepositControlService {

  constructor(private readonly activateRoute: ActivatedRoute,
              private readonly store: Store) {
  }

  public updateFlowToFinalize(): void {
    this.activateRoute.queryParams
      .pipe(
        take(1),
        filter(e => Object.keys(e).length > 0)
      )
      .subscribe(e => {
        const {step, fileId, composedFileId, validation} = e;
        this.store.dispatch(updateStatusToFinalize({
          step,
          fileId,
          composedFileId: step === 2 ? '' : composedFileId,
          validation
        }));
      });
  }
}
