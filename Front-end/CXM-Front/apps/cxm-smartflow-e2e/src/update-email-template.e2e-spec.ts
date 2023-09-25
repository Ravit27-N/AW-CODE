import { waitForAngular } from 'testcafe-angular-selectors';
import { userCredentials } from './environment';

import EmailTemplatePage from './po/email-template.po';
import LoginPage from './po/login.po';
import { NavigatePage } from './po/navigate.po';
import { BASE_URL } from './utils/index';

const loginPage = new LoginPage();
const navigate = new NavigatePage();
const updateEmailTemplate = new EmailTemplatePage();
const timeOut = 2000;

fixture('cxm-smartflow Update Email Template Tests')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
  });

test('Update Email Template', async (t) => {
  const rndInt = Math.floor(Math.random() * 10000) + 1;
  var modelName = 'Testing' + rndInt;

  await t
    .typeText(loginPage.useranmeField, userCredentials.username)
    .wait(timeOut)
    .typeText(loginPage.passwordField, userCredentials.password)
    .wait(timeOut)
    .click(loginPage.loginButton)
    .wait(timeOut);
  await navigate.navigateToListEmailTemplate().wait(timeOut);
  await t
    .expect(updateEmailTemplate.dialogUpdateButton)
    .ok()
    .hover(updateEmailTemplate.dialogDeleteButton)
    .wait(timeOut)
    .click(
      updateEmailTemplate.dialogUpdateButton
        .find('mat-icon')
        .withText('settings')
    )
    .wait(timeOut)
    .typeText(updateEmailTemplate.modelName, modelName)
    .wait(timeOut)
    .click(updateEmailTemplate.tapVariable)
    .wait(timeOut)
    .click(updateEmailTemplate.submitButton)
    .wait(timeOut);
});
