import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import { appRoute, selectTemplateModelListFilter } from '@cxm-smartflow/template/data-access';
import { BehaviorSubject, ReplaySubject, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { CommonListFilterComponent } from '@cxm-smartflow/shared/common-typo';
import { ITemplateCardEvent } from '../grid-list-template-selection/grid-list-template-selection.component';
import { ActivatedRoute, Router } from '@angular/router';
import {
  loadListChoiceOfModelTemplate,
  loadPreviewTemplate,
  selectListWithFilter,
  StepOnActivated,
  unloadCampaignSms,
  unloadEmailCampaignFormData
} from '@cxm-smartflow/follow-my-campaign/data-access';


@Component({
  selector: 'cxm-smartflow-campaign-model-selection-page',
  templateUrl: './campaign-model-selection-page.component.html',
  styleUrls: ['./campaign-model-selection-page.component.scss']
})
export class CampaignModelSelectionPageComponent implements OnInit, AfterViewInit, OnDestroy {

  destroy$ = new Subject();
  searchTerm$ = new ReplaySubject<string>(1);

  filters: any;

  showTooltip$ = new BehaviorSubject(false);
  showTooltipBackground = false;

  @ViewChild(CommonListFilterComponent) templateFilter: CommonListFilterComponent;

  doFetchListTemplate(filters: {
    page: number;
    pageSize: number;
    sortByField: string;
    sortDirection: string;
    filter?: string;
  }) {
    const { campaignType } = this.activatedRoute.snapshot.data;
    this.store.dispatch(loadListChoiceOfModelTemplate({ ...filters, templateType: campaignType }));
  }

  searchTermChanged(searchTerm: any) {
    this.searchTerm$.next(searchTerm);
  }

  requestPagination(pageEvent: any) {
    this.doFetchListTemplate({ ...this.filters, page: pageEvent.pageIndex });
  }

  handleCardEvent(cardEvent: ITemplateCardEvent) {
    const { type, template } = cardEvent;
    const { campaignType } = this.activatedRoute.snapshot.data;

    if (type === 'select') {
      if (campaignType === 'EMAILING') {

        this.router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination], { queryParams: { templateId: template.id } });
      } else if (campaignType === 'SMS') {
        this.router.navigate([appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination], { queryParams: { templateId: template.id } });
      }
    } else if (type === 'view') {
      this.store.dispatch(loadPreviewTemplate({ emailTemplateModel: template }));
    }
  }

  filterChanged(filterEvent: any) {
    this.doFetchListTemplate({
      ...this.filters,
      sortByField: filterEvent.sortByField,
      sortDirection: filterEvent.sortDirection,
      page: 1
    });
  }

  ngOnInit(): void {
    // clear sms/email data before create new
    this.store.dispatch(unloadEmailCampaignFormData());
    this.store.dispatch(unloadCampaignSms());
    this.store.dispatch(StepOnActivated({ active: true }));

    // TODO: a new filter remembering for template selection
    this.doFetchListTemplate({ page: 1, pageSize: 12, sortByField: 'modelName', sortDirection: 'asc' });
  }


  ngAfterViewInit(): void {
    this.store.select(selectTemplateModelListFilter).pipe(takeUntil(this.destroy$))
      .subscribe((filter) => {
        this.filters = filter;
        this.templateFilter.patchValue(filter.sortByField);
      });

    this.store
      .select(selectListWithFilter)
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        const { response, filter } = value;
        this.showTooltip$.next(response?.total <= 0 && filter?.trim()?.length > 0);
      });

    // Setup search box
    this.searchTerm$.pipe(distinctUntilChanged(), debounceTime(500)).pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.filters = { ...this.filters, filter: value, page: 1 };
        this.doFetchListTemplate(this.filters);
      });
  }

  get tooltipMessage() {
    let message = '';
    this.translate
      .get('cxmCampaign.followMyCampaign.list.tableHeader.notFound')
      ?.subscribe((v) => (message = v));
    return message;
  }

  ngOnDestroy(): void {
    this.store.dispatch(StepOnActivated({ active: false }));
    this.destroy$.complete();
    this.searchTerm$.complete();
    this.showTooltip$.complete();
  }

  constructor(
    private store: Store, private translate: TranslateService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
  }

}
