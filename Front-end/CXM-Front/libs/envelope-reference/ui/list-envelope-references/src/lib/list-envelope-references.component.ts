import {Component,OnDestroy} from '@angular/core';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import {
  EnvelopeReferenceFormControlService
} from "@cxm-smartflow/envelope-reference/data-access";

@Component({
  selector: 'cxm-smartflow-list-envelope-reference',
  templateUrl: './list-envelope-references.component.html',
  styleUrls: ['./list-envelope-references.component.scss']
})
export class ListEnvelopeReferencesComponent implements OnDestroy {
  // Validation properties.
  destroy$ = new Subject<boolean>();
  _canCreateCreate = true;
  constructor(public envelopeReferenceFormControlService: EnvelopeReferenceFormControlService,
  ) {
  }

  createEnvelopeReferenceEvent() {
    if (!this.envelopeReferenceFormControlService.isCanCreate) return;
    this.envelopeReferenceFormControlService.navigateToCreate();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
}
