import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  createTemplateByDuplicate,
  deleteEmailTemplate,
  downloadTemplateAsFile,
  editTemplate,
  loadFeatureListEmailTemplate,
  modifiedTemplate,
  selectTemplateModelList,
  selectTemplateModelListFilter,
  selectTotalTemplate,
  showCreateTemplatePopup,
  TemplateConstant,
  unloadFeatureListEmailTemplate,
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, ReplaySubject, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

import { Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { ITemplateCardEvent } from '../grid-list-template/grid-list-template.component';
import { TemplateFilterComponentComponent } from '../template-filter-component/template-filter-component.component';
import {
  TemplateCriteria,
  templateFiltering,
} from '../template-filter-remember';
import { templateEnv } from '@env-cxm-template';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-list-email-template-page',
  templateUrl: './list-email-template-page.component.html',
  styleUrls: ['./list-email-template-page.component.scss'],
})
export class ListEmailTemplatePageComponent
  implements OnInit, OnDestroy, AfterViewInit {
  destroy$ = new Subject();

  emailTemplateList$: Observable<any>;
  @ViewChild(TemplateFilterComponentComponent)
  templateFitler: TemplateFilterComponentComponent;

  filterCriteria: TemplateCriteria;
  searchTerm$ = new ReplaySubject<string>(1);

  // tooltip properties.
  showTooltip$ = new BehaviorSubject(false);
  showTooltipBackground = false;
  showTooltipWhenValueChange = true;
  readonly pageSizeLimit: number = Math.max(
    1,
    templateEnv.numberOfCardPerPage - 1
  );

  // privileges.
  canCreate$ = new BehaviorSubject(false);

  ngOnInit(): void {
    this.setup();
    this.validatePrivilege();
  }

  setup(): void {
    // Template list
    this.emailTemplateList$ = this.store
      .select(selectTemplateModelList)
      .pipe(takeUntil(this.destroy$));

    // Restore filter
    const shouldUseFilter = templateFiltering.shouldRestoreFilter();

    if (shouldUseFilter) {
      this.filterCriteria = shouldUseFilter;
      this.doLoadFeatureListEmailTemplate(shouldUseFilter);
    } else {
      this.doLoadFeatureListEmailTemplate({
        page: 1,
        pageSize: this.pageSizeLimit,
        sortByField: 'lastModified',
        sortDirection: 'desc',
      });
    }

    // Setup search box
    this.searchTerm$
      .pipe(distinctUntilChanged(), debounceTime(800))
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.filterCriteria = {
          ...this.filterCriteria,
          filter: value,
          page: 1,
        };
        this.doLoadFeatureListEmailTemplate(this.filterCriteria);
      });

    // validate to show error tooltip.
    this.store
      .select(selectTotalTemplate)
      ?.pipe(takeUntil(this.destroy$))
      .subscribe((total) => {
        this.searchTerm$.subscribe((value) => {
          this.showTooltip$.next(total <= 0 && value?.trim()?.length > 0);
        });
      });
  }

  doLoadFeatureListEmailTemplate(filters: {
    page: number;
    pageSize: number;
    sortByField: string;
    sortDirection: string;
    filter?: string;
  }): void {
    templateFiltering.rememberFilter(filters);

    this.store.dispatch(
      loadFeatureListEmailTemplate({ ...filters, templateType: 'EMAILING' })
    );
  }

  filterTemplateChanged(filterEvent: any) {
    this.doLoadFeatureListEmailTemplate({
      ...this.filterCriteria,
      sortByField: filterEvent.sortByField,
      sortDirection: filterEvent.sortDirection,
      page: 1,
    });
  }

  searchTermChanged(searchTerm: any) {
    this.searchTerm$.next(searchTerm);
  }

  ngOnDestroy(): void {
    this.canCreate$.complete();
    this.destroy$.next();
    this.destroy$.complete();
    this.showTooltip$.next(false);
    this.showTooltip$.complete();
    this.store.dispatch(unloadFeatureListEmailTemplate());
  }

  get tooltipMessage() {
    let message = '';
    this.translate
      .get('cxmCampaign.followMyCampaign.list.tableHeader.notFound')
      ?.subscribe((v) => (message = v));
    return message;
  }

  constructor(
    private store: Store,
    private translate: TranslateService,
    private route: Router,
    private messageService: ConfirmationMessageService,
    private canAccessibilityService: CanAccessibilityService
  ) {}

  ngAfterViewInit(): void {
    this.store
      .select(selectTemplateModelListFilter)
      .pipe(takeUntil(this.destroy$))
      .subscribe((filter) => {
        this.filterCriteria = filter;
        this.templateFitler.patchValue(filter.sortByField);
      });
  }

  validatePrivilege() {
    this.canCreate$.next(
      this.canAccessibilityService.canAccessible(
        TemplateConstant.CXM_TEMPLATE,
        TemplateConstant.CREATE,
        true
      )
    );
  }

  createTemplate() {
    this.store.dispatch(showCreateTemplatePopup({ modelType: 'EMAILING' }));
  }

  requestPagination(pageEvent: any) {
    this.doLoadFeatureListEmailTemplate({
      ...this.filterCriteria,
      page: pageEvent.pageIndex,
    });
  }

  handleCardEvent(cardEvent: ITemplateCardEvent) {
    const { type, template } = cardEvent;

    if (type === 'Read') {
      this.store.dispatch(editTemplate({ template: template }));
    } else if (type === 'Modifiy') {
      this.store.dispatch(modifiedTemplate({ template: template }));
    } else if (type === 'Copy') {
      this.store.dispatch(createTemplateByDuplicate({ template: template }));
    } else if (type === 'Delete') {
      Promise.all([
        this.translate.get('dialog').toPromise(),
        this.translate.get('button').toPromise(),
      ]).then(([dialog, button]) => {
        this.messageService
          .showConfirmationPopup({
            icon: 'feedback',
            title: dialog.cxmTemplate.emailTemplate.confirmDelete.title,
            message: dialog.cxmTemplate.emailTemplate.confirmDelete.body,
            confirmButton: button.validate,
            cancelButton: button.cancel,
            importanceWorld: template.modelName,
            type: 'Warning',
          })
          .subscribe((ok) => {
            if (ok === true) {
              // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
              this.store.dispatch(
                deleteEmailTemplate({
                  id: template.id!,
                  isLoading: true,
                  modelName: template.modelName,
                })
              );
            }
          });
      });
    } else if (type === 'Download') {
      this.store.dispatch(downloadTemplateAsFile({ template }));
    }
  }
}
