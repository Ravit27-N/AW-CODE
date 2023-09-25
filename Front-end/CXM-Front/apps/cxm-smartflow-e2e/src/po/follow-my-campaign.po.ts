import { Selector } from 'testcafe';
import { AngularSelector } from 'testcafe-angular-selectors';

export default class FollowMyCampaignPage {

  // List follow my campaign page
  btnCreate: Selector;
  // Choice follow my campaign page
  choiceCard: Selector;
  choiceBtnNext: Selector;

  // Setting parameters follow my campaign page
  campaignName: Selector;
  subjectMail: Selector;
  settingParameterBtnNext: Selector;
  btnViewSummaryTemplate: Selector;
  btnCloseDialog: Selector;

  // destination follow my campaign page
  btnUpload: Selector;
  inputUpload: Selector;
  destinationBtnNext: Selector;

  // summary follow my campaign page
  btnValidate: Selector;

  constructor() {
    // list follow my campaign page
    const list = AngularSelector('cxm-smartflow-feature-list-of-campaign');
    this.btnCreate = list.find('.cxm-cp-btn-mr > cxm-smartflow-button > button');

    // Choice follow my campaign page
    const choice = AngularSelector('cxm-smartflow-feature-create-email-campaign');
    this.choiceCard = choice.find('#hoverButton > div').nth(1);
    this.choiceBtnNext = choice.find('#submit');

    //  Setting parameters follow my campaign page
    const settingParameter = AngularSelector('cxm-smartflow-feature-setting-parameters')
    this.campaignName = settingParameter.find('#campaignName');
    this.subjectMail = settingParameter.find('#subjectMail');
    this.settingParameterBtnNext = settingParameter.find('cxm-smartflow-button').nth(2);
    this.btnViewSummaryTemplate = settingParameter.find('.box-model-selection > div').nth(1).find('mat-icon[role="img"]')
    this.btnCloseDialog = Selector('div.w-full.text-right > mat-icon.mat-icon.notranslate.cursor-pointer.material-icons.mat-icon-no-color');

    // destination follow my campaign page
    const destination = AngularSelector('cxm-smartflow-feature-campaign-destination');
    this.btnUpload = destination.find('.mat-focus-indicator .cxm-button .mat-raised-button .mat-button-base');
    this.inputUpload = destination.find('input[type="file"]');
    this.destinationBtnNext = destination.find('#submit');

    // summary follow my campaign page
    const summary = AngularSelector('cxm-smartflow-feature-summary-of-campaign');
    this.btnValidate = summary.find('.button-flex > cxm-smartflow-button').nth(2);
  }
}
