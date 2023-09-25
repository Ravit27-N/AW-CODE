import { Selector } from 'testcafe';
import { AngularSelector } from 'testcafe-angular-selectors';

export default class DemoPage {
  fullname: Selector;
  btnSubmit: Selector;
  constructor() {
    const loginForm = AngularSelector('cxm-smartflow-demo1');

    this.fullname = loginForm.find('#fullname');
    this.btnSubmit = loginForm.find('.mat-raised-button');
  }
}
