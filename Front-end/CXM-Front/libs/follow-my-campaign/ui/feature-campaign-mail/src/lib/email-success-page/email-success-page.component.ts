import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnimatedConfirmationPageOptions } from '@cxm-smartflow/shared/common-typo';
import {
  appRoute,
  CampaignConstant,
  FlowTraceability,
  SmsTemplate,
  TemplateConstant
} from '@cxm-smartflow/template/data-access';
import { TranslateService } from '@ngx-translate/core';
import { UserProfileUtil } from '@cxm-smartflow/shared/data-access/services';



@Component({
  selector: 'cxm-smartflow-email-success-page',
  templateUrl: './email-success-page.component.html',
  styleUrls: ['./email-success-page.component.scss']
})
export class EmailSuccessPageComponent implements OnInit {

  animationConfig: AnimatedConfirmationPageOptions;


  handleEvent(event: any) {
    if(event === 'Main') {
      this.router.navigate([appRoute.cxmFlowTraceability.list]);
    } else if(event === 'Alter') {
      this.router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaign]);
    }
  }

  ngOnInit(): void {
    const isCanCreate = UserProfileUtil.canAccess(CampaignConstant.CXM_CAMPAIGN, CampaignConstant.CREATE);
    const isCanList = UserProfileUtil.canAccess(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.LIST);

    this.translate.get('cxmCampaign.followMyCampaign.campaign_success_page').toPromise().then((text) => {
      this.animationConfig = {
        or: text.or,
        title: text.title,
        subtitle: text.subtitle,
        alterButton: isCanCreate? text.alterButton : '',
        mainButton: isCanList? text.mainButton : '',
        isShowSubContent: true
      }
    })
  }

  constructor(private translate: TranslateService, private router: Router) { }

}
