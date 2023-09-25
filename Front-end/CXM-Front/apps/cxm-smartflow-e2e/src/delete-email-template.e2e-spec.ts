import { waitForAngular } from 'testcafe-angular-selectors';

import { userCredentials } from './environment';
import EmailTemplatePage from './po/email-template.po';
import LoginPage from './po/login.po';
import { NavigatePage } from './po/navigate.po';
import { BASE_URL } from './utils/index';

const loginPage = new LoginPage();
const emailTemplateComponent = new EmailTemplatePage();
const timeOut = 2000;

fixture('cxm-smartflow Email Template Tests')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
    await t
      .typeText(loginPage.useranmeField, userCredentials.username)
      .wait(timeOut)
      .typeText(loginPage.passwordField, userCredentials.password)
      .wait(timeOut)
      .click(loginPage.loginButton)

      .wait(timeOut);
  });

test('delete Email Template', async (t) => {
  await t
    // Assert Object
    .expect(emailTemplateComponent.dialogDeleteButton).ok()
    // Do
    .hover(emailTemplateComponent.dialogDeleteButton)
    .wait(timeOut)
    .click(
      emailTemplateComponent.dialogDeleteButton
        .find('mat-icon')
        .withText('delete')
    )
    .wait(timeOut)

    // Assert Object
    .expect(emailTemplateComponent.comfirmButton.exists).ok()
    // Do .....
    .click(emailTemplateComponent.comfirmButton).wait(timeOut);
});
