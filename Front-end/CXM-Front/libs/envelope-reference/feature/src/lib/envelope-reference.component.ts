import { Component, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import {unloadEnvelopeReferencelist} from "@cxm-smartflow/envelope-reference/data-access";

@Component({
  selector: 'cxm-smartflow-envelope-reference',
  templateUrl: './envelope-reference.component.html',
  styleUrls: ['./envelope-reference.component.scss']
})
export class EnvelopeReferenceComponent implements OnDestroy {

  constructor(private store: Store) {
  }

  ngOnDestroy(): void {
    this.store.dispatch(unloadEnvelopeReferencelist())
  }

}
