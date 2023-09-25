import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import {
  CampaignModel,
  CampaignType,
  cancelCampaign,
  downloadCsvFile,
  gotoCreateCampaign,
  gotoEmailCampaignDetail,
  gotoSmsCampaignDetail,
  gotoUpdateEmail,
  gotoUpdateSms,
  loadFeatureCampaignList,
  selectListCampaign, StepOnActivated,
  unloadCampaignSms,
  unloadEmailCampaignFormData
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { Store } from '@ngrx/store';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { Sort } from '@angular/material/sort';
import { MatMenuTrigger } from '@angular/material/menu';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';

interface CustomPaginator {
  pageIndex: number,
  pageSize: number,
  length: number,
  showLength?: boolean,
  center?: boolean,
  withCriteria?: boolean,

  sortByField?: string,
  sortDirection?: string,
  filter?: string,
  type?: string,
  mode?: string
}

interface FilterComponent {
  mode?: string,
  type?: string,
  useFilter: boolean
}

@Component({
  selector: 'cxm-smartflow-list-campaign',
  templateUrl: './list-campaign.component.html',
  styleUrls: ['./list-campaign.component.scss']
})
export class ListCampaignComponent implements OnInit, OnDestroy, AfterViewInit, AfterContentChecked {

  destroyed$ = new Subject<boolean>();
  listCampaignSubscription$: Subscription;

  // table properties.
  displayColumns: string [] = [
    'nom',
    'model',
    'dateCreation',
    'channel',
    'category',
    'mode',
    'status',
    'action'
  ];

  data$ = new BehaviorSubject<CampaignModel[]>([]);

  cancelCampaignPropertyLabel: any;

  // tooltip properties.
  showTooltip$ = new BehaviorSubject(false);
  showTooltipBackground = false;

  filter: any;
  searchTerm$ = new BehaviorSubject<string>('');
  $hasFilter = new BehaviorSubject<boolean>(false);

  // pagination properties.
  paginator: CustomPaginator = {
    pageIndex: 1,
    pageSize: 10,
    length: 0,
    sortByField: 'CreatedAt',
    sortDirection: 'DESC',
    type: 'EMAIL,SMS',
    mode: 'Manual,Automated'
  };

  usingFilter = false;

  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;

  constructor(private store: Store,
              private confirmationService: ConfirmationMessageService,
              private translate: TranslateService,
              private router: Router,
              private ref: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    // clear sms/email data before create new
    this.store.dispatch(unloadEmailCampaignFormData());
    this.store.dispatch(unloadCampaignSms());
    this.store.dispatch(StepOnActivated({ active: false, leave: true }));

    this.translate.get('cxmCampaign.followMyCampaign.cancelEmailCampaignPopUp')
      .subscribe((response) => {
        this.cancelCampaignPropertyLabel = response;
      });

    // load template.
    this.doLoadFeatureListCampaign(this.paginator);
  }

  ngOnDestroy(): void {
    this.data$.complete();
    this.searchTerm$.complete();
    this.showTooltip$.complete();
    this.destroyed$.complete();
    this.store.complete();
    this.listCampaignSubscription$.unsubscribe();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.listCampaignSubscription$ = this.store.select(selectListCampaign)
        .pipe(takeUntil(this.destroyed$))
        .subscribe(data => {
          // Pass content to list.
          this.data$.next(data?.contents || []);

          // set paginator properties.
          this.paginator = {
            ...this.paginator,
            pageIndex: data?.page || 1,
            length: data?.total || 0
          };

          // show error tooltip.
          this.showTooltip$.next(data?.total <= 0 && this.searchTerm$.value?.trim()?.length > 0);
        });

      // Setup search box
      this.searchTerm$
        .pipe(distinctUntilChanged(), debounceTime(800))
        .pipe(takeUntil(this.destroyed$))
        .subscribe((value) => {

            this.paginator = {
              ...this.paginator,
              filter: value?.trim()
            };

            // this.doLoadFeatureListCampaign(this.paginator);
          }
        );
    });
  }

  trackTask(index: number, item: CampaignModel): number {
    // Return a unique identifier for each item in the `dataSource`
    return item.id || 0; // Assuming `id` is a unique identifier property of each item
  }

  preparedFieldActive(sort: Sort): Observable<string> {
    let field: string;
    switch (sort.active?.toLowerCase()) {
      case 'nom':
        field = 'campaignName';
        break;
      case 'model':
        field = 'modelName';
        break;
      case 'dateCreation':
        field = 'createdAt';
        break;
      case 'channel':
        field = 'channel';
        break;
      case 'category':
        field = 'type';
        break;
      case 'mode':
        field = 'mode';
        break;
      case 'status':
        field = 'status';
        break;
      default:
        field = 'createdAt';
        break;
    }
    return of(field);
  }

  sortData(sort: Sort) {
    this.preparedFieldActive(sort).subscribe(field => {
      this.paginator = {
        ...this.paginator,
        sortByField: field,
        sortDirection: sort.direction || 'desc'
      };

      this.doLoadFeatureListCampaign(this.paginator);
    });
  }

  cancel(id: number) {
    this.confirmationService.showConfirmationPopup({
      title: this.cancelCampaignPropertyLabel?.header,
      message: this.cancelCampaignPropertyLabel?.message,
      type: 'Warning',
      cancelButton: this.cancelCampaignPropertyLabel?.cancelButton,
      confirmButton: this.cancelCampaignPropertyLabel?.confirmButton
    })
      .subscribe(response => {
        if (response) {
          this.store.dispatch(cancelCampaign({ id: id, status: 'Canceled' }));
        }
      });
  }

  goToDetail(row: CampaignModel) {
    if (row.type?.toUpperCase() === CampaignType.SMS) {
      this.store.dispatch(gotoSmsCampaignDetail({ id: <number>row.id, ownerId: row.ownerId || 0 }));
    } else {
      this.store.dispatch(gotoEmailCampaignDetail({ id: <number>row.id, ownerId: row.ownerId || 0 }));
    }
  }

  gotoUpdate(row: CampaignModel) {
    if (row === undefined) return;

    this.store.dispatch(StepOnActivated({ active: true, modify: true }));
    if (row.type?.toUpperCase() === CampaignType.SMS) {
      this.store.dispatch(gotoUpdateSms({ id: <number>row.id }));
    } else {
      this.store.dispatch(gotoUpdateEmail({ id: <number>row.id }));
    }
  }

  downloadCsvFile(row: CampaignModel) {
    if (row === undefined) return;
    this.store.dispatch(downloadCsvFile({ campaign: row }));
  }

  get tooltipMessage() {
    let message = '';
    this.translate
      .get('cxmCampaign.followMyCampaign.list.tableHeader.notFound')
      ?.subscribe((v) => (message = v));
    return message;
  }

  searchTermChanged(searchTerm: any) {
    this.paginator = {
      ...this.paginator,
      filter: (searchTerm as string)?.trim(),
    };

    this.searchTerm$.next(searchTerm);
    this.$hasFilter.next(searchTerm.length > 0 || this.usingFilter);
    this.filterFromFirstPage();
    this.doLoadFeatureListCampaign(this.paginator);
  }

  filterChange(event: FilterComponent) {
    this.usingFilter = event.useFilter;
    this.paginator = {
      ...this.paginator,
      type: event.type || '',
      mode: event.mode || ''
    };

    this.$hasFilter.next(
      event.useFilter ||
      this.searchTerm$.value.length > 0
    );

    this.filterFromFirstPage();
    this.doLoadFeatureListCampaign(this.paginator);
  }

  create() {
    this.store.dispatch(gotoCreateCampaign());
  }

  paginationChanged(event: PageEvent) {
    this.paginator = {
      ...this.paginator,
      pageIndex: event?.pageIndex || 1,
      pageSize: event?.pageSize || 5,
      length: event?.length || 0
    };

    this.filterFromFirstPage(this.paginator.pageIndex);
    this.doLoadFeatureListCampaign(this.paginator);
  }

  doLoadFeatureListCampaign(paginator: CustomPaginator) {
    this.store.dispatch(loadFeatureCampaignList({
      page: paginator.pageIndex,
      pageSize: paginator.pageSize,
      sortByField: paginator.sortByField,
      sortDirection: paginator.sortDirection,
      filter: paginator.filter || '',
      _type: paginator.type || '',
      mode: paginator.mode || ''
    }));
  }

  filterFromFirstPage(page?: number) {
    const firstPageIndex = 1;
    this.paginator.pageIndex = (this.usingFilter || this.paginator.filter?.length) && !page ? firstPageIndex : this.paginator.pageIndex;
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  ngAfterContentChecked() {
    this.ref.detectChanges();
  }
}
