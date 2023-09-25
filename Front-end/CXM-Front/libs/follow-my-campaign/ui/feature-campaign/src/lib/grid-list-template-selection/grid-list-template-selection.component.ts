import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { getFeatureListEmailTemplateFilter, TemplateList, TemplateModel } from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { selectListChoiceOfModel } from '@cxm-smartflow/follow-my-campaign/data-access';

export interface ITemplateCardEvent {
  type: 'view' | 'select';
  template: TemplateModel;
}


@Component({
  selector: 'cxm-smartflow-grid-list-template-selection',
  templateUrl: './grid-list-template-selection.component.html',
  styleUrls: ['./grid-list-template-selection.component.scss']
})
export class GridListTemplateSelectionComponent implements OnInit, OnDestroy {

  destroy$ = new Subject<boolean>();
  emailTemplateList$: Observable<TemplateList>;
  filters$: Observable<any>;

  @Output() onpaginationChanged = new EventEmitter();
  @Output() oncardevent = new EventEmitter<ITemplateCardEvent>();

  requestView(template: TemplateModel) {
    this.oncardevent.emit({ type: 'view', template });
  }


  requestSelect(template: TemplateModel) {
    this.oncardevent.emit({ template, type: 'select' });
  }

  ngOnInit(): void {
    // Template list
    this.emailTemplateList$ = this.store.select(selectListChoiceOfModel)
      .pipe(takeUntil(this.destroy$));

    this.filters$ = this.store
      .select(getFeatureListEmailTemplateFilter)
      .pipe(takeUntil(this.destroy$));
  }

  constructor(private store: Store) {
  }

  ngOnDestroy(): void {
    this.store.complete();
    this.destroy$.complete();
  }

}
