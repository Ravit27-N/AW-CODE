import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-cxm-template',
  templateUrl: './cxm-template.component.html',
  styleUrls: ['./cxm-template.component.css'],
})
export class CxmTemplateComponent {
  constructor(private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr') ;
  }
}
