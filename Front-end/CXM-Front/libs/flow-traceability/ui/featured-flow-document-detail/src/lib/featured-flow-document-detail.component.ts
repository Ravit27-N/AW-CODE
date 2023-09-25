import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { filter, pluck, take, takeUntil } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import {
  backToLastURL,
  DocumentModel,
  documentTraceabilityListFilterChangeAction,
  downloadAssociateDocument,
  downloadDocumentDetail,
  FlowDocumentDetailModel,
  FlowFilterCriteriaParams,
  loadAssociateDocument,
  loadDocumentTraceabilityList,
  loadFlowDocumentDetail,
  selectAssociateDocument, selectEventHistoryInfo,
  selectFlowDocumentDetail,
  selectFlowDocumentList,
  unloadFlowDocumentDetail, viewStatusInfo
} from '@cxm-smartflow/flow-traceability/data-access'; 
import { BehaviorSubject, Subject } from 'rxjs';
import { EventHistory } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';
import {
  AssociateDocumentType,
  EventHistorySize,
  EventHistoryType,
  FlowDocumentStatus, FlowStatusConstant,
} from '@cxm-smartflow/flow-traceability/util';
import { FalsyUtil, FileExtensionEnumeration, formatDate } from '@cxm-smartflow/shared/utils';
import {
  FlowType,
  FULL_DATE_TIME_NO_SECOND_FORMAT
} from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { RecipientModel } from './recipient/recipient.component';
import { FlowDocumentDetail } from './document-detail/document-detail.component';
import { CanVisibilityService } from '@cxm-smartflow/shared/data-access/services';
import { AssociateDocument } from './flow-document-detail.interface';
import { FlowDocumentDetailControl } from './flow-document-detail.control';

@Component({
  selector: 'cxm-smartflow-featured-flow-document-detail',
  templateUrl: './featured-flow-document-detail.component.html',
  styleUrls: ['./featured-flow-document-detail.component.scss']
})
export class FeaturedFlowDocumentDetailComponent implements OnInit, OnDestroy {
  // Validated properties.
  canDownloadFeed = false;
  isAssociateButtonVisible = false;
  canDownloadAssociate = false;

  // State properties.
  fileName = '';
  eventHistories: EventHistory[] = [];
  recipient: RecipientModel = {};
  flowDocumentDetail: FlowDocumentDetail;
  associateDocuments: AssociateDocument[] = [];
  state: FlowDocumentDetailModel;
  documents: FlowDocumentDetailModel[] = [];

  // Association button.
  showPopup = false;

  // Unsubscribe properties.
  private _destroy$ = new Subject<boolean>();

  currentDocumentIndex = 1; 
   totalDocuments = 0; 

   data$ = new BehaviorSubject<DocumentModel[]>([]);
   featuredDocuments: DocumentModel[] = [];
  params = new BehaviorSubject<FlowFilterCriteriaParams>({});
  flowTraceabilityId = new BehaviorSubject<number>(0);
  isLoading = new BehaviorSubject(false);
  isFilterSearchBox = new BehaviorSubject<boolean>(false);
  isFilterCriteriaNotFound = new BehaviorSubject<boolean>(false);
  destroy$ = new Subject<boolean>();
  page = 1;
  pageSize = 15;
  total$ = new BehaviorSubject<number>(0);
  /**
   * Constructor
   */
  constructor(
    private _activateRoute: ActivatedRoute,
    private _store: Store,
    private _translate: TranslateService,
    private _canVisibility: CanVisibilityService
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    const MAX_PAGE_SIZE = 10000;
    this._activateRoute.queryParams.pipe(pluck('id')).subscribe((id) => {
        // Load flow document detail.
        if (id) this._store.dispatch(loadFlowDocumentDetail({ id }));
        this._store.dispatch(documentTraceabilityListFilterChangeAction({
          flowTraceabilityId: this.flowTraceabilityId.value,
          page: this.page,
          pageSize: MAX_PAGE_SIZE,
          params: this.params.value
        }));
      });
        

    // Load associate document.
    if (this.associateDocuments.length) {
      this.loadAssociateDocument();
    }

    // Subscribe flow document detail.
    this._store.select(selectFlowDocumentDetail)
      .pipe(takeUntil(this._destroy$), filter(e => !FalsyUtil.isEmptyObject(e)))
      .subscribe((flowDocument) => {
        // Validate flow document is not empty obje
          const { canDownloadAssociate, canDownloadFeed } = flowDocument?.privilege;
          this._translate.get('flow.document').toPromise().then((messages) => {
              this._getPageHeader(flowDocument);
              this.eventHistories = this._getEventHistory(flowDocument?.histories, messages, flowDocument.details.docName);
              this.recipient = FlowDocumentDetailControl.getRecipient(flowDocument?.details, flowDocument.channel);
              this.flowDocumentDetail = FlowDocumentDetailControl.getDocumentDetail(flowDocument, messages);
              this.state = flowDocument;
              this.canDownloadAssociate = canDownloadAssociate;
              this.canDownloadFeed = canDownloadFeed;
            this._getAssociateDocument(flowDocument.elementAssociations);
            });
      });

      this._store.select(selectFlowDocumentList)
      .pipe(takeUntil(this._destroy$))
      .subscribe((data: any) => {
        const flowDocuments = data?.flowDocument;
        this.documents = flowDocuments.contents || [];         
        this.data$.next(flowDocuments.contents || []);
        this.flowTraceabilityId.next(data?.flowTraceabilityId || 0);
        this.params.next(data?.params);
        this.totalDocuments = flowDocuments?.total;
    });
    
  } 
  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Unsubscribe states.
    this._destroy$.next(true);
    // Clear all states in flow document detail.
    this._store.dispatch(unloadFlowDocumentDetail());
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _getPageHeader(selectedDocument: any): void {
    this.fileName = selectedDocument?.details?.flowName;  
}
  private _getEventHistory(events: any[], message: any, docName: string): EventHistory[] {
    let eventHistories: EventHistory[] = [];
    // Filter success, in error and canceled status.
    const finaleStatus = events?.filter((e) => e?.historyStatus?.status === FlowDocumentStatus.IN_ERROR);
    eventHistories = events?.map((e): EventHistory => {
      // Prepare date label.
      const dateTime = formatDate.formatDateTime(e.dateTime, FULL_DATE_TIME_NO_SECOND_FORMAT);
      const dateLabel = `${message?.eventHistory?.on} ${dateTime.split(' ')[0]} ${message?.eventHistory?.at} ${dateTime.split(' ')[1]}`;
      if (e?.canSeeFullName) { // return with createdBy property.
        return {
          mode: FlowDocumentDetailControl.getMode(e?.historyStatus?.status),
          createdBy: `${message?.eventHistory?.by} ${e?.fullName ? e?.fullName : e?.createdBy}`,
          status: e?.historyStatus?.statusLabel,
          dateStatus: dateLabel,
          size: window.outerHeight > 967 ? EventHistorySize.MEDIUM : EventHistorySize.SMALL,
          eventHistoryInfo: this.isEmailStatus(docName)? FlowDocumentDetailControl.getStatusInfo(e?.historyStatus?.statusLabel) : undefined,
          comment: e?.comment,
          validatedOrRefused: this.checkStatusIsRefusedOrValidate(e?.historyStatus?.status)
        };
      } else { // return not have createdBy property.
        return {
          mode: FlowDocumentDetailControl.getMode(e?.historyStatus?.status),
          status: e?.historyStatus?.statusLabel,
          dateStatus: dateLabel,
          eventHistoryInfo: this.isEmailStatus(docName)? FlowDocumentDetailControl.getStatusInfo(e?.historyStatus?.statusLabel) : undefined,
          size: window.outerHeight > 967 ? EventHistorySize.MEDIUM : EventHistorySize.SMALL,
          comment: e?.comment,
          validatedOrRefused: this.checkStatusIsRefusedOrValidate(e?.historyStatus?.status)
        };
      }
    });

    // If status is not finished status, add next status.
    if (finaleStatus?.length === 0) {
      eventHistories = [...eventHistories, { mode: EventHistoryType.CONTINUED }];
    }

    return eventHistories;
  }

  private _getAssociateDocument(elementAssociations: Array<any>): void {
    // Prepare associate document.

    this._translate
      .get('flow.document.associateDocument')
      .toPromise()
      .then((m) => {
        // Filter image base on document type.
        const getSrc = (st: string) => {
          switch (st) {
            case AssociateDocumentType.ACCUSE_RECEPTION:
              return 'assets/icons/AR.svg';
            case AssociateDocumentType.SLIP_SHEET:
              return 'assets/icons/bordereau.svg';
            case AssociateDocumentType.DOCUMENT:
              return 'assets/icons/document.svg';
            default:
              return '';
          }
        };

        // Filter description base on type of document.
        const getDescription = (st: string, messages: any) => {
          switch (st) {
            case AssociateDocumentType.ACCUSE_RECEPTION:
              return { line1: messages?.ar?.line1, line2: messages?.ar?.line2 };
            case AssociateDocumentType.SLIP_SHEET:
              return {
                line1: messages?.bordereau?.line1,
                line2: messages?.bordereau?.line2
              };
            case AssociateDocumentType.DOCUMENT:
              return { line1: messages?.document?.line1 };
            default:
              return {};
          }
        };


        this.associateDocuments = Array.from(elementAssociations).map((data: any) => {
          return {
            ...data,
            src: getSrc(data?.element?.value),
            description: getDescription(data?.element?.value, m)
          };
        });

        // Validate associate button.

        this.isAssociateButtonVisible = this.associateDocuments.length > 0;
      });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Download flow document detail.
   */
  downloadFeed(): void {
    if (this.state?.fileUrl) {
      window.open(this.state?.fileUrl);
      return;
    }

    const documentId = this.state?.fileId || '';
    let docName = '';
    if(this.state?.document?.endsWith(FileExtensionEnumeration.EML)){ // Email.
      docName = this.state?.document;
    }else if(this.state?.details?.docName?.endsWith(FileExtensionEnumeration.TXT)){ // SMS.
      docName = this.state?.details?.docName;
    }else if(this.state?.details?.flowName?.endsWith(FileExtensionEnumeration.ZIP)){ // Batch.
      docName = this.state?.document?.concat(FileExtensionEnumeration.EML) || this.state?.fileId?.concat(FileExtensionEnumeration.EML) || '';
    }else {
      docName = this.state?.details?.docName || '';
    }

    // Download document.
    if (!documentId && !this.canDownloadFeed && !docName) return;
    this._store.dispatch(downloadDocumentDetail({ documentId, docName, _type: FlowType.DOCUMENT }));
  }

  /**
   * Redirect to the previous page and clear current state.
   */
  back() {
    this._store.dispatch(backToLastURL());
  }

  /**
   * Toggle associate document popup.
   */
  toggleAssociateDocumentPopup() {
    this.showPopup = !this.showPopup;
  }

  /**
   * Fetch the associate document.
   */
  loadAssociateDocument() {
    // Load associate document.
    this._activateRoute.queryParams.pipe(pluck('id')).subscribe((flowDocumentId) =>
        this._store.dispatch(loadAssociateDocument({ flowDocumentId }))
    );
  }

  /**
   * Download associate document.
   * @param elementAssociation
   */
  downloadAssociateDocument(elementAssociation: any) {
    // Download associate document
    if (!this.isAssociateButtonVisible) return;
    this._store.dispatch(downloadAssociateDocument({ fileId: elementAssociation?.fileId, associateDocKey: elementAssociation?.element.key }));
  }

  async viewStatus() {
    const { id } = await this._activateRoute.queryParams.pipe(take(1)).toPromise();
    const locale = localStorage.getItem('locale') || 'fr';

    this._store.dispatch(viewStatusInfo({ id, locale }));
    this._store.select(selectEventHistoryInfo)
      .pipe(filter(e => !FalsyUtil.isEmptyObject(e)), take(1))
      .subscribe(async ({ statuses, description }) => {
      const match = this.eventHistories.find(e => e.eventHistoryInfo);

      this.eventHistories = this.eventHistories?.map(e => {
        if (JSON.stringify(e) === JSON.stringify(match)) {

          if (statuses?.length > 0) {
            statuses = statuses.map((e: string) => `<span class='whitespace-nowrap'>${e}</span>`);
            return { ...e, eventHistoryInfo: { statuses, description } }
          } else {
            return { ...e, eventHistoryInfo: { statuses, description } }
          }
        }

        return e;
      });

    });
  }

  isEmailStatus(docName: string): boolean {
    return docName.endsWith(FileExtensionEnumeration.EML) || docName.endsWith(FileExtensionEnumeration.HTML) || false;
  }

  checkStatusIsRefusedOrValidate(status: string): boolean {
    return status.toLowerCase() === FlowStatusConstant.REFUSED_DOCUMENT.toLowerCase() || status.toLowerCase() == FlowStatusConstant.VALIDATED.toLowerCase();
  }


  previousDocument(): void {
    if (this.currentDocumentIndex > 1) {
        this.currentDocumentIndex--;
        this._loadDocumentByIndex(this.currentDocumentIndex);
    }
}

nextDocument(): void {
  if (this.currentDocumentIndex < this.totalDocuments) {
      this.currentDocumentIndex++;
      this._loadDocumentByIndex(this.currentDocumentIndex);
  }
}

private _loadDocumentByIndex(index: number): void {
  if (this.documents && this.documents.length >= index) {
      const selectedDocument = this.documents[index - 1];
      this.state = selectedDocument;

      
      this._getPageHeader(selectedDocument);

    
      if (selectedDocument?.id) {
          this._store.dispatch(loadFlowDocumentDetail({ id: selectedDocument.id }));
          if (selectedDocument.flowHistories && selectedDocument.details && selectedDocument.details.docName) {
              this._translate.get('flow.document').toPromise().then((messages) => {
                this.eventHistories = this._getEventHistory(selectedDocument.flowHistories ? selectedDocument.flowHistories : [], messages, 
                  selectedDocument.details?.docName ?? '');
              });
          }
         
          this.loadAssociateDocument();
      }
  }
}

}
