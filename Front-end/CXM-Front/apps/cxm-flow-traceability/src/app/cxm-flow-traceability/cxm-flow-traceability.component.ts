import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-cxm-flow-traceability',
  templateUrl: './cxm-flow-traceability.component.html',
  styleUrls: ['./cxm-flow-traceability.component.scss']
})
export class CxmFlowTraceabilityComponent{

  constructor(private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }


  getPaddingBottom(): string {
    if (location.pathname.includes(appRoute.cxmFlowTraceability.navigateToFlowDetailDeposit)) {
      return '665px';
    }

    if (location.pathname.includes(appRoute.cxmFlowTraceability.navigateToFlowDetailDigital)) {
      return '700px';
    }

    if (location.pathname.includes(appRoute.cxmFlowTraceability.navigateToViewDocumentShipment)) {
      return '666px';
    }

    if (location.pathname.includes(appRoute.cxmFlowTraceability.navigateToViewDocumentDetail)) {
      return '877px';
    }

    return '877px';
  }

}
