import { Component, Input } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'cxm-smartflow-render-sms-template',
  templateUrl: './render-sms-template.component.html',
  styleUrls: ['./render-sms-template.component.scss']
})
export class RenderSmsTemplateComponent {

  @Input() smsContent: string;
  @Input() isHasBackground = false;
  @Input() width = '100%';

  constructor(private sanitizer: DomSanitizer) { }

  transformHTML(htmlFile: string) {
    const tap = "&ensp;&ensp;"; // 4 spaces per tap.
    htmlFile = htmlFile.replace(/\n/g, "<br/>");
    htmlFile = htmlFile.replace(/\t/g, tap)
    // Bypass safe HTML code.
    return this.sanitizer.bypassSecurityTrustHtml(htmlFile);
  }

}
