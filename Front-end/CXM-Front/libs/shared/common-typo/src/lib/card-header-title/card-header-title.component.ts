import {Component, Input} from '@angular/core';


@Component({
  selector: 'cxm-smartflow-card-header-title',
  templateUrl: './card-header-title.component.html',
  styleUrls: ['./card-header-title.component.scss']
})
export class CardHeaderTitleComponent {

  @Input() styleLeft = false;

}
