import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import {
  getFeatureListEmailTemplateFilter,
  selectTemplateModelList,
  TemplateList,
  TemplateModel
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';

export interface ITemplateCardEvent {
  type: 'Read' | 'Modifiy' | 'Delete' | 'Copy' | 'Download';
  template: TemplateModel;
}

@Component({
  selector: 'cxm-smartflow-grid-list-template',
  templateUrl: './grid-list-template.component.html',
  styleUrls: ['./grid-list-template.component.scss']
})
export class GridListTemplateComponent implements OnInit, OnDestroy {
  destroy$ = new Subject<boolean>();

  templateList$: Observable<TemplateList>;
  filters$: Observable<any>;

  @Input() allowCreate = false;

  @Output() oncreate = new EventEmitter();
  @Output() onpaginationChanged = new EventEmitter();
  @Output() oncardevent = new EventEmitter<ITemplateCardEvent>();

  ngOnInit(): void {
    // Template list
    this.templateList$ = this.store
      .select(selectTemplateModelList)
      .pipe(takeUntil(this.destroy$));

    this.filters$ = this.store
      .select(getFeatureListEmailTemplateFilter)
      .pipe(takeUntil(this.destroy$));

  }

  requestCreate() {
    this.oncreate.emit();
  }

  requestView(template: TemplateModel) {
    if (
      template?.privilege?.canView &&
      !template?.privilege?.canModify
    ) {
      // do read only
      this.oncardevent.emit({ type: 'Read', template });
    }
  }

  requestModify(template: TemplateModel) {
    if (template?.privilege?.canModify) {
      // do modify
      this.oncardevent.emit({ type: 'Modifiy', template });
    }
  }

  requestDownload(template: TemplateModel) {
    this.oncardevent.emit({ type: 'Download', template });
  }

  requestDelete(template: TemplateModel) {
    this.oncardevent.emit({ type: 'Delete', template });
  }

  requestCopy(template: TemplateModel) {
    this.oncardevent.emit({ type: 'Copy', template });
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  constructor(private store: Store) {}

  ngOnDestroy(): void {
    this.destroy$.complete();
    this.store.complete();
  }
}
