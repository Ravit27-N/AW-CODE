import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

function base64toPDF(data: any) {
  const bufferArray = base64ToArrayBuffer(data);
  const blobStore = new Blob([bufferArray], { type: 'application/pdf' });
  let pdfLink = window.URL.createObjectURL(blobStore);
  pdfLink = pdfLink + '#toolbar=0&navpanes=0';
  return pdfLink;
}

function base64ToArrayBuffer(data: any) {
  const bString = window.atob(`${data}`);
  const bLength = bString.length;
  const bytes = new Uint8Array(bLength);

  for (let i = 0; i < bLength; i++) {
    const ascii = bString.charCodeAt(i);
    bytes[i] = ascii;
  }

  return bytes;
};

@Component({
  selector: 'cxm-smartflow-setting-option-preview-file',
  templateUrl: './setting-option-preview-file.component.html',
  styleUrls: ['./setting-option-preview-file.component.scss'],
})
export class SettingOptionPreviewFileComponent implements OnChanges {
  @Input() base64: string;
  @Input() type: 'pdf' | 'img' = 'pdf';
  href = '';

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.base64 && this.base64) {
      if (this.type === 'pdf') {
        this.href = base64toPDF(this.base64);
      } else if (this.type === 'img') {
        this.href = `data:image/png;base64,${this.base64}`;
      }
    }
  }
}
