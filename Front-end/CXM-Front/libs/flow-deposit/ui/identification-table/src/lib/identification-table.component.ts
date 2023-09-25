import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  DepositedFlowModel,
  selectPreAnalysisState,
  selectProcessControlResponseState, unlockWhenNoDocumentValid
} from '@cxm-smartflow/flow-deposit/data-access';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-identification-table',
  templateUrl: './identification-table.component.html',
  styleUrls: ['./identification-table.component.scss']
})
export class IdentificationTableComponent implements OnInit, OnDestroy {

  depositedState: DepositedFlowModel;
  fileUploadStateSubscription: Subscription;
  processControlStateSubscription: Subscription;
  modelName: string;

  constructor(private store: Store) {
  }

  ngOnInit(): void {
    this.fileUploadStateSubscription = this.store.select(selectPreAnalysisState).subscribe(response => {
      if (response) {
        this.depositedState = response;
      }
    });

    this.processControlStateSubscription = this.store.select(selectProcessControlResponseState).subscribe(res => {
      this.modelName = res.data?.ModeleName;
      if(!this.modelName) {
        this.store.dispatch(unlockWhenNoDocumentValid());
      }
    });
  }

  ngOnDestroy(): void {
    if (this.fileUploadStateSubscription) {
      this.fileUploadStateSubscription.unsubscribe();
    }
    if (this.processControlStateSubscription) {
      this.processControlStateSubscription.unsubscribe();
    }
  }

}
