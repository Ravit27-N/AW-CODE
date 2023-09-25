import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-page-sub-header',
  templateUrl: './page-sub-header.component.html',
  styleUrls: ['./page-sub-header.component.scss']
})
export class PageSubHeaderComponent {
  @Input() right = false;
 }
