import { Component, Input } from '@angular/core';
import {SideBarDataSource} from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-card-side-bar',
  templateUrl: './card-side-bar.component.html',
  styleUrls: ['./card-side-bar.component.scss']
})
export class CardSideBarComponent {
  linkActive = 1;
  @Input() dataSource: SideBarDataSource[];

  constructor() {
    this.dataSource = []
  }

}
