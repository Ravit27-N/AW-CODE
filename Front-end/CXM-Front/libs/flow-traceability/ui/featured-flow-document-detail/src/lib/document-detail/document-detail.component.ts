import {Component, Input, OnChanges} from '@angular/core';
import {FileUtils} from '@cxm-smartflow/shared/utils';
import {Attachment, BackgroundPage, Enrichment} from '@cxm-smartflow/shared/data-access/model';
import {Observable, of} from 'rxjs';

interface InternalBackgroundPage{
  name: string;
  value: string;
}

interface InternalAttachment{
  name: string;
  value: string;
}

export interface FlowDocumentDetail {
  document: {
    identify: string,
    fileSize: number,
    createdDate: string,
    pageNumber: number,
    sheetNumber: number,
    modelName: string,
    unloadingDate:string,
    status:string,
    channel:string
  },
  productionCriteria: {
    postage: string,
    printing: string,
    color: string,
    envelope: string
  },
  dataExtraction: {
    fillers: any[],
    reference: string
  },
  channel: string,
  enrichment: Enrichment
}

@Component({
  selector: 'cxm-smartflow-document-detail',
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.scss']
})
export class DocumentDetailComponent implements OnChanges {

  @Input() flowDocumentDetail: FlowDocumentDetail;

  background: InternalBackgroundPage [] = [];
  attachments: InternalAttachment [] = [];
  watermark = '';
  signature = '';

  showEnrichment = false
  panelOpenState = false;

  ngOnChanges() {
    if (this.flowDocumentDetail?.enrichment && this.flowDocumentDetail.document.channel === "Postal") {
      this.getEnrichment(this.flowDocumentDetail?.enrichment).subscribe(valueMap => {
        const {backgroundPage, attachments, watermark, signature} = valueMap;
        this.background = backgroundPage || [];
        this.attachments = attachments || [];
        this.watermark = watermark;
        this.signature = signature;
        this.showEnrichment = this.background?.length > 0 || this.attachments?.length > 0 || this.watermark?.length > 0 || this.signature?.length > 0;
      });
    }
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  getLimitSize(size: string): string {
    return FileUtils.getLimitSize(size);
  }

  getEnrichment(enrichment: Enrichment): Observable<any> {
    const backgroundPage = this.getBackground(enrichment.backgroundPage);
    const attachments = this.getAttachment(enrichment.attachments);
    const watermark = enrichment.watermark || null;
    const signature = enrichment.signature || null;

    return of({
      backgroundPage,
      attachments,
      watermark,
      signature
    })
  }

  getBackground(background: BackgroundPage): InternalBackgroundPage[] {
    if (background) {
      return Object.keys(background).map(key => {
        if (key === 'background') {
          return { name: `background.position.${background.position}`, value: background[key] };
        } else if (key === 'backgroundFirst') {
          return { name: `background.position.${background.positionFirst}`, value: background[key] };
        } else if (key === 'backgroundLast') {
          return { name: `background.position.${background.positionLast}`, value: background[key] };
        } else {
          return { name: '', value: '' };
        }
      }).filter(y => Object.keys(y).length > 0 && y.value !== '');
    }
    return background;
  }

  getAttachment(attachment: Attachment): InternalAttachment [] {
    if (attachment) {
      return Object.keys(attachment).map(key => {
        if (key === 'attachment1') {
          return { name: `background.position.FIRST_POSITION`, value: attachment[key] };
        } else if (key === 'attachment2') {
          return { name: `background.position.SECOND_POSITION`, value: attachment[key] };
        } else if (key === 'attachment3') {
          return { name: `background.position.THIRD_POSITION`, value: attachment[key] };
        } else if (key === 'attachment4') {
          return { name: `background.position.FOURTH_POSITION`, value: attachment[key] };
        } else if (key === 'attachment5') {
          return { name: `background.position.FIFTH_POSITION`, value: attachment[key] };
        } else return { name: '', value: '' };
      })
        .filter(y => Object.keys(y).length > 0 && y.value !== '');
    }
    return attachment;
  }
}
