import { Component, OnDestroy } from '@angular/core';
import { fromClientFluxExt, fromModifyClientActions } from '@cxm-smartflow/client/data-access';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cxm-smartflow-parameter-client',
  templateUrl: './parameter-client.component.html',
  styleUrls: ['./parameter-client.component.scss']
})
export class ParameterClientComponent implements OnDestroy {

  ngOnDestroy(): void {
    this.store.dispatch(fromClientFluxExt.clearPreserveState());
    this.store.dispatch(fromModifyClientActions.unloadClientForm());
  }

  constructor(private store: Store) { }
}
