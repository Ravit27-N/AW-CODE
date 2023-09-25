import { Component, OnInit } from '@angular/core';
import { AnimatedConfirmationPageOptions } from '@cxm-smartflow/shared/common-typo';
import { TranslateService } from '@ngx-translate/core';
import { take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { UserProfileUtil } from '@cxm-smartflow/shared/data-access/services';
import { DepositManagement, FlowTraceability } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-validate-result',
  templateUrl: './validate-result.component.html',
  styleUrls: ['./validate-result.component.scss']
})
export class ValidateResultComponent implements OnInit {

  animationConfig: AnimatedConfirmationPageOptions;

  constructor(private translateService: TranslateService, private router: Router) { }

  ngOnInit(): void {
    this.translateService.get('flow.deposit.validateResult').pipe(take(1)).subscribe(res => {
      const isCanCreate = UserProfileUtil.canAccess(DepositManagement.CXM_FLOW_DEPOSIT, DepositManagement.CXM_FLOW_DEPOSIT_SEND_A_LETTER);
      const isCanList = UserProfileUtil.canAccess(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.LIST);
      this.animationConfig = {
        or: res.ou,
        title: res.title,
        subtitle: res.subtitle,
        alterButton: isCanCreate? res.alterButton : undefined,
        mainButton: isCanList? res.mainButton : undefined,
        isShowSubContent: true
      }
    })
  }

  validateAction(btnType: string) {
    if (btnType === 'Main') {
      this.router.navigateByUrl('/cxm-flow-traceability/list');
    }

    if (btnType === 'Alter') {
      this.router.navigateByUrl('/cxm-deposit/acquisition');
    }
  }
}
