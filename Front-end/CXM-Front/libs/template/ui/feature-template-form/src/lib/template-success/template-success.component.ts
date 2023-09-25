import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  appRoute,
  CampaignConstant,
  SmsTemplate,
  TemplateConstant
} from '@cxm-smartflow/template/data-access';
import { AnimatedConfirmationPageOptions } from '@cxm-smartflow/shared/common-typo';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { UserProfileUtil } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-template-success',
  templateUrl: './template-success.component.html',
  styleUrls: ['./template-success.component.scss'],
})
export class TemplateSuccessComponent implements OnInit {
  contentType: 'EMAILING' | 'SMS';
  id: string;

  animationConfig: AnimatedConfirmationPageOptions;

  ngOnInit(): void {
    this.contentType = this.activatedRoute.snapshot.params.content.toUpperCase();
    this.id = this.activatedRoute.snapshot.params.id;

    this.translate
      .get('template.template_success_page')
      .toPromise()
      .then((text) => {
        this.animationConfig = {
          or: text.or,
          title: text.title,
          subtitle: text.subtitle,
          alterButton: this.getAlterText(this.contentType, text.alterButton),
          mainButton: this.getMainText(
            this.contentType,
            this.contentType === 'EMAILING'
              ? text.mainButton
              : this.contentType === 'SMS'
                ? text.mainButton2
                : undefined
          ),
        };
      });
  }

  getAlterText(contentType: 'EMAILING' | 'SMS', message: string): string | undefined {
    const isCanListTemplateSMS = UserProfileUtil.canAccess(SmsTemplate.CXM_SMS_TEMPLATE, SmsTemplate.LIST);
    const isCanListTemplateEmail = UserProfileUtil.canAccess(TemplateConstant.CXM_TEMPLATE, TemplateConstant.LIST);

    if (contentType === 'EMAILING' && isCanListTemplateEmail) {
      return message;
    } else if (contentType === 'SMS' && isCanListTemplateSMS) {
      return message;
    }

    return undefined;
  }

  getMainText(contentType: 'EMAILING' | 'SMS', message: string | undefined): string | undefined {
    if(!message) return undefined;
    const isCanCreateCampaignSMS = UserProfileUtil.canAccess(CampaignConstant.CXM_CAMPAIGN_SMS, CampaignConstant.CREATE_SMS);
    const isCanCreateCampaignEmail = UserProfileUtil.canAccess(CampaignConstant.CXM_CAMPAIGN, CampaignConstant.CREATE);
    if (contentType === 'EMAILING' && isCanCreateCampaignEmail) {
      return message;
    } else if (contentType === 'SMS' && isCanCreateCampaignSMS) {
      return message;
    }

    return undefined;
  }

  handleEvent(event: any) {
    if (event === 'Main') {
      if (this.contentType === 'EMAILING') {
        this.router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaign]);
      } else if (this.contentType === 'SMS') {
        this.router.navigate([appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel]);
      }
    } else if (event === 'Alter') {
      if (this.contentType === 'EMAILING') {
        this.router.navigate([appRoute.cxmTemplate.template.listEmailTemplate]);
      } else if (this.contentType === 'SMS') {
        this.router.navigate([appRoute.cxmTemplate.template.listSMSTemplate]);
      }
    }
  }

  constructor(
    private translate: TranslateService,
    private store: Store,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}
}
