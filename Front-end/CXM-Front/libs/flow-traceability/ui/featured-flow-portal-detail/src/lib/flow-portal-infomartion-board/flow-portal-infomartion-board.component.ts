import { Component, Input } from '@angular/core';
import {
  EventModeType,
  FlowStatusConstant,
} from '@cxm-smartflow/flow-traceability/util';

export interface FlowInfo {
  mode?: string;
  flowType?: string;
  category?: string;
  dateStatus?: string;
  unloadingDate?: Date | string;
  showUnloadingDate?: boolean;
  user?: string;
  service?: string;
  status?: string;
  statusStyle?: string;
  modelName?: string;
  division?: string;
}

@Component({
  selector: 'cxm-smartflow-flow-portal-infomartion-board',
  templateUrl: './flow-portal-infomartion-board.component.html',
  styleUrls: ['./flow-portal-infomartion-board.component.scss'],
})
export class FlowPortalInfomartionBoardComponent {
  @Input() flowInfo: FlowInfo;

  getDivisionService(division?: string, service?: string): string {
    const separator = division || service ? ' / ' : '';
    return division || service ? division + separator + service : '';
  }
}
