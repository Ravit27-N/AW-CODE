import { Selector } from 'testcafe';
import { AngularSelector } from 'testcafe-angular-selectors';

export default class EmailTemplatePage {
  modelName: Selector;
  subjectMail: Selector;
  senderMail: Selector;
  senderName: Selector;
  unsubscribeLink: Selector;
  variables1: Selector;
  variables2: Selector;
  variables0: Selector;
  createButton: Selector;
  submitButton: Selector;
  tapVariable: Selector;
  addVariable: Selector;
  removeVariable: Selector;
  nextButton: Selector;
  disabledButton: Selector;
  title: Selector;
  countCardEmailTemplate: Selector;
  dialogDeleteButton: Selector;
  dialogUpdateButton: Selector;
  comfirmButton: Selector;
  cancelButton: Selector;

  constructor() {
    // dialog component
    this.comfirmButton = Selector('#comfirmButton');
    const listEmailTemplate = AngularSelector(
      'cxm-smartflow-feature-list-email-template'
    );
    this.createButton = listEmailTemplate.find('#createEmailTemplate');
    this.dialogDeleteButton = listEmailTemplate.find(
      '#dialogButton-0 mat-card-content.mat-card-content'
    );

    this.dialogUpdateButton = listEmailTemplate.find(
      '#dialogButton-0 mat-card-content.mat-card-content'
    );

    const createEmailTemplate = AngularSelector(
      'cxm-smartflow-feature-create-update-email-template'
    );
    this.modelName = createEmailTemplate.find('#modelName');
    this.subjectMail = createEmailTemplate.find('#subjectMail');
    this.senderMail = createEmailTemplate.find('#senderMail');
    this.senderName = createEmailTemplate.find('#senderName');
    this.submitButton = createEmailTemplate.find('#submit');
    this.unsubscribeLink = createEmailTemplate.find('#unsubscribeLink');
    this.tapVariable = createEmailTemplate.find('#mat-tab-label-0-1');
    this.addVariable = createEmailTemplate.find('.add');
    this.removeVariable = createEmailTemplate.find('#remove-variable-1');
    this.variables2 = createEmailTemplate.find('.unique-variable-2');
    this.variables1 = createEmailTemplate.find('.unique-variable-1');
    this.variables0 = createEmailTemplate.find('.unique-variable-0');
    // list
    const listEmailTemplatePage = AngularSelector(
      'cxm-smartflow-feature-list-email-template'
    );

    this.nextButton = listEmailTemplatePage.find(
      '.mat-paginator-navigation-next'
    );

    this.disabledButton = listEmailTemplatePage.find('.cxm-paginator-disabled');

    this.title = listEmailTemplatePage.find('mat-card-title.mat-card-title');

    this.countCardEmailTemplate = listEmailTemplatePage.find(
      'cxm-smartflow-email-template-card.ng-star-inserted'
    );
  }
}
