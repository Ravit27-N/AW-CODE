import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";

function base64toPDF(data: any) {
  const bufferArray = base64ToArrayBuffer(data);
  const blobStore = new Blob([bufferArray], { type: "application/pdf" });
  // if (window.navigator && window.navigator.msSaveOrOpenBlob) {
  //     window.navigator.msSaveOrOpenBlob(blobStore);
  //     return;
  // }
  let pdfLink = window.URL.createObjectURL(blobStore);
  // const link = document.createElement('a');
  // document.body.appendChild(link);
  // link.href = data;
  // link.download = "file.pdf";
  // link.click();
  // window.URL.revokeObjectURL(data);
  // link.remove();
  pdfLink = pdfLink + '#toolbar=0&navpanes=0';
  return pdfLink;
}

function base64ToArrayBuffer(data: any) {
  const bString = window.atob(data);
  const bLength = bString.length;
  const bytes = new Uint8Array(bLength);
  for (let i = 0; i < bLength; i++) {
    const ascii = bString.charCodeAt(i);
      bytes[i] = ascii;
  }
  return bytes;
};


@Component({
  selector: 'cxm-smartflow-background-preview',
  templateUrl: './background-preview.component.html',
  styleUrls: ['./background-preview.component.scss']
})
export class BackgroundPrewviewComponent implements OnChanges {

  @Input() image: any;

  href: string;

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.image && changes.image.currentValue) {
      this.href = base64toPDF(changes.image.currentValue);
    }
  }


}
