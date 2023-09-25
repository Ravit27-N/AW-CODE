import { AngularSelector } from 'testcafe-angular-selectors';

export default class LoginPage {
  useranmeField: Selector;
  passwordField: Selector;
  loginButton: Selector;
  constructor() {
    const loginForm = AngularSelector('cxm-smartflow-auth');
    this.useranmeField = loginForm.find('#username');
    this.passwordField = loginForm.find('#password');
    this.loginButton = loginForm.find('cxm-smartflow-button');
  }
}
