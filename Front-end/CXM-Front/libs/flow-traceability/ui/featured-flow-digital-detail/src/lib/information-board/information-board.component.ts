import { Component, Input } from '@angular/core';

export interface InformationBoard {
  createdDate?: string,
  createdBy?: string,
  sentDate?: string,
  status?: string,
  service?: string,
  division?: string,
  type: 'success' | 'danger' | 'secondary' | 'info' | 'primary'
}

@Component({
  selector: 'cxm-smartflow-information-board',
  templateUrl: './information-board.component.html',
  styleUrls: ['./information-board.component.scss']
})
export class InformationBoardComponent {
  @Input() informationBoard: InformationBoard;

  showTooltip(content: string): string {
    return content;
  }

  getDivisionService(division?: string, service?: string): string {
    const separator = division || service ? ' / ' : '';
    return division || service ? division + separator + service : '';
  }
}
