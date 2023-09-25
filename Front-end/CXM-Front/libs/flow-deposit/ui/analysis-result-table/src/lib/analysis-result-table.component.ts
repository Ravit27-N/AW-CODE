import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {BehaviorSubject, Subject, Subscription} from 'rxjs';
import {
  Addresses,
  AnalysisModel,
  selectAnalyzeContent,
  selectAnalyzePagingContent,
  selectSummaryDocument,
  sortedAnalyzeContent
} from '@cxm-smartflow/flow-deposit/data-access';
import {Store} from '@ngrx/store';
import {Sort} from '@angular/material/sort';
import {SnackBarService, UserProfileUtil} from '@cxm-smartflow/shared/data-access/services';
import {TranslateService} from '@ngx-translate/core';
import {take, takeUntil} from 'rxjs/operators';
import {DepositManagement} from "@cxm-smartflow/shared/data-access/model";
import {URLUtils} from "@cxm-smartflow/shared/utils";
import {AddressDestinationPopupService} from "@cxm-smartflow/flow-deposit/ui/address-destination-popup";


@Component({
  selector: 'cxm-smartflow-analysis-result-table',
  templateUrl: './analysis-result-table.component.html',
  styleUrls: ['./analysis-result-table.component.scss']
})
export class AnalysisResultTableComponent implements OnInit, OnDestroy {

  tableColumns = [
    "document",
    "nbPage",
    "reception",
    "resultAnalysis",
    "actions"
  ]
  data$ = new BehaviorSubject<AnalysisModel[]>([]);
  subscriptions: Subscription[] = [];
  sortField = 'nbPage';
  sortDirection = 'asc';
  nbDocuments = new BehaviorSubject(0);
  pageIndex = 1;
  pageSize = 10;
  isShowPaging = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();
  docCountMessageMapping = { '=0': 'document', '=1': 'document', 'other': 'documents' };

  pageAccess=0;

  @Output() pageAccessNumber = new EventEmitter<number>();


  isCanModifyOrCorrectAddress = UserProfileUtil.getInstance().canModify({
    func: DepositManagement.CXM_FLOW_DEPOSIT,
    priv: DepositManagement.MODIFY_OR_CORRECT_AN_ADDRESS,
    ownerId: Number(URLUtils.getQueryParamByKey('ownerId') || 0),
    checkAdmin: false,
  });

  addressModify=false;

  constructor(private store: Store, private snackbar: SnackBarService, private translate: TranslateService,
              private addressDestinationPopupService: AddressDestinationPopupService) {
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.isShowPaging.unsubscribe();
    this.nbDocuments.unsubscribe();
    this.data$.unsubscribe();
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  ngOnInit(): void {
      this.store.select(selectAnalyzeContent)
        .pipe(takeUntil(this.destroy$))
        .subscribe(res => {
        this.nbDocuments.next(res.length);
        this.isShowPaging.next(res.length > 10)
      })

    this.loadData();

    // Validate KO document.
    let i = 0;
    this.store.select(selectSummaryDocument).pipe(takeUntil(this.destroy$)).subscribe(v => {
      if(v?.KO > 0 && i !== 1) {
        i = 1;

        // If has error document partial.
        if(v?.OK > 0) {


          Promise.all([
            this.translate.get('flow.deposit.analysisResult').toPromise(),
            this.translate.get('flow.deposit.analysisResult.hasPartialKO_instruction').toPromise()
          ]).then(
            k => {
            this.snackbar.openCustomSnackbar({message: `${k[0]?.hasPartialKO} ${v?.KO} ${k[0]?.error}`, type: 'error', icon: 'close', details: k[1] });
          });
        } else {

          // If no only one document valid.
          this.translate.get('flow.deposit.analysisResult').pipe(take(1)).subscribe(k => {
            this.snackbar.openCustomSnackbar({message: k?.noValidDocumentOnlyOne, type: 'error', icon: 'close'})
          });
        }
      }
    });
  }

  sortData(sort: Sort) {
    this.sortField = sort.active;
    this.sortDirection = sort.direction;
    this.dispatchRequest();
  }

  loadData(): void {
    this.subscriptions.push(
      this.store.select(selectAnalyzePagingContent).subscribe(data => {

        let pageAcc = 0;
        const newData = data.map((value) => {
          pageAcc = Number(value.numberOfSet) - Number(value.numberOfPages);
          if (value.modify) {
            this.addressModify = true;
            return {...value, resultAnalysis: "modify", reception: value.address, page: pageAcc};
          } else {
            return {...value, reception: value.address, page: pageAcc};
          }
        });

        this.data$.next(newData);
      })
    )
  }

  dispatchRequest(): void {
    this.store.dispatch(sortedAnalyzeContent({
      sortField: this.sortField,
      sortDirection: this.sortDirection,
      pageSize: this.pageSize,
      pageIndex: this.pageIndex
    }));
  }

  onChangePaging(event: any) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.dispatchRequest();
  }

  modifyAddress(docUuid:string, addresses:Addresses) {
    this.addressDestinationPopupService.show(docUuid,addresses);
  }

  accessPageClick(page: number): void {
    this.pageAccessNumber.emit(page);
  }
}

