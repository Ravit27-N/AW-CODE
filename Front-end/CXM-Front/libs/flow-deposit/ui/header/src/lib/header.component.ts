import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent  {

  @Input() pageTitle = '';
  @Input() subTitle = '';
}
