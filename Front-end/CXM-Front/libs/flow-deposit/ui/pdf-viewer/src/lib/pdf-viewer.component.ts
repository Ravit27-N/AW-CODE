import {Component, Input, OnChanges, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  loadComposedBase64File,
  selectAnalyzeResponseState,
  selectComposedBase64FileState,
  selectComposedFileIdState,
  selectDataTreatmentResponse,
  selectDefaultBase64FileState,
  selectDocNameAnalzeReponse,
  selectDocumentProcessingTreatmentResponse,
  selectTreatmentResponse
} from '@cxm-smartflow/flow-deposit/data-access';
import {BehaviorSubject, Subscription} from 'rxjs';
import {PagesLoadedEvent} from 'ngx-extended-pdf-viewer/lib/events/pages-loaded-event';
import {take} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {DepositManagement} from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-pdf-viewer',
  templateUrl: './pdf-viewer.component.html',
  styleUrls: ['./pdf-viewer.component.scss']
})
export class PdfViewerComponent implements OnInit, OnDestroy {
  base64$ = new BehaviorSubject('');
  subscriptions: Subscription[] = [];
  currentPage$ = new BehaviorSubject(0);
  nbPages$ = new BehaviorSubject(0);
  zoomLevels = ['auto', 'page-actual', 'page-fit', 'page-width', 0.5, 0.75, 1, 1.25, 1.5, 2, 2.5, 3];

  @Input()
  isFinishStep = false;
  @Input()
  nbDocuments = 4;
  @Input()
  currentDocument = 0;
  pageAccess = 1;

  treatmentResponse = new BehaviorSubject<any>({});
  composedFileId = new BehaviorSubject('');
  docName = new BehaviorSubject('');
  // Properties for step 5.
  docNameAnalyzeResponse = new BehaviorSubject('');

  composedFileIdFromUrl = new BehaviorSubject('');
  stepFromUrl = new BehaviorSubject('');
  fileIdFromUrl = new BehaviorSubject('');
  documentProcessingTreatmentResponse = new BehaviorSubject<any>([]);
  dataTreatmentResponse = new BehaviorSubject<any>({});

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {
    this.store.select(selectDocumentProcessingTreatmentResponse).subscribe(documentProcessing => this.documentProcessingTreatmentResponse.next(documentProcessing));
    this.store.select(selectDataTreatmentResponse).subscribe(data => this.dataTreatmentResponse.next(data));
    this.store.select(selectTreatmentResponse).subscribe(treatment => this.treatmentResponse.next(treatment));

    this.store.select(selectDocNameAnalzeReponse).subscribe(docName => this.docNameAnalyzeResponse.next(docName));
    this.store.select(selectComposedFileIdState).subscribe(composedFileId => this.composedFileId.next(composedFileId));

    this.activatedRoute.queryParams.subscribe(params => {
      const param = { step: params['step'], fileId: params['fileId'], composedFileId: params['composedFileId'] };
      this.composedFileIdFromUrl.next(param?.composedFileId);
      this.stepFromUrl.next(param?.step);
      this.fileIdFromUrl.next(param?.fileId);
    });

    this.docName.next(this.treatmentResponse?.value?.data?.documentProcessing?.[0]?.DocName);
  }

  ngOnInit(): void {
    this.loadBase64File();
  }
  setPageAccess(pageNumber:number){
    this.pageAccess = pageNumber;
  }

  onChangePage(pageChanged: number): void {
    this.pageAccess=pageChanged;
    this.currentPage$.next(pageChanged);
  }

  loadNbPages(event: PagesLoadedEvent) {
    this.nbPages$.next(event.pagesCount);
  }

  onClickNext() {
    this.currentDocument++;
    this.loadComposedBase64();
  }

  onClickPrev() {
    --this.currentDocument;
    this.loadComposedBase64();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.base64$.unsubscribe();
  }

  canNextDocument(): boolean {
    return +this.currentDocument >= +this.nbDocuments;
  }

  storeComposedBase64(nbPage: number) {
    this.store.select(selectAnalyzeResponseState).pipe(take(2)).subscribe(response => {
      if(Object.values(response).length > 0) {
        const documentOK = response?.data?.document.DOCUMENT
          .filter(d => (d?.Analyse === 'OK' || d?.Analysis === 'OK'));
        this.store.dispatch(loadComposedBase64File({
          request: {
            fileId: '',
            docId: documentOK[nbPage - 1]?.DocUUID
          },
          funcKey: DepositManagement.CXM_FLOW_DEPOSIT,
          privKey: DepositManagement.MODIFY_A_DEPOSIT
        }));
      }
    });
  }

  loadBase64File() {
    if (this.isFinishStep) {
      this.loadComposedBase64();
    } else {
      this.subscriptions.push(
        this.store.select(selectDefaultBase64FileState).subscribe(res => {
          if (res) {
            this.currentPage$.next(1);
            this.base64$.next(res);
          }
        })
      );
    }
  }

  loadComposedBase64() {
    this.storeComposedBase64(this.currentDocument);
    this.subscriptions.push(
      this.store.select(selectComposedBase64FileState).subscribe(res => {
        if (res) {
          this.currentPage$.next(1);
          this.base64$.next(res);
        }
      })
    );
  }
}
