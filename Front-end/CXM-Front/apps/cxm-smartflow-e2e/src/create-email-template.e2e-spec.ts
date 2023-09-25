import { waitForAngular } from 'testcafe-angular-selectors';
import { userCredentials } from './environment';

import EmailTemplatePage from './po/email-template.po';
import LoginPage from './po/login.po';
import { NavigatePage } from './po/navigate.po';
import { BASE_URL } from './utils/index';

const loginPage = new LoginPage();
const navigate = new NavigatePage();
const createEmailTemplatePage = new EmailTemplatePage();
const timeOut = 2000;

fixture('cxm-smartflow Create Email Template Tests')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
  });

test('Create Email Template', async (t) => {
  const rndInt = Math.floor(Math.random() * 10000) + 1;
  var modelName = 'MODEL ' + rndInt;
  // password and username must match on user's keycloak
  await t
    .typeText(loginPage.useranmeField, userCredentials.username)
    .wait(timeOut)
    .typeText(loginPage.passwordField, userCredentials.password)
    .wait(timeOut)
    .click(loginPage.loginButton)
    .wait(timeOut);

  await navigate.navigateToListEmailTemplate().wait(timeOut);
  await t.click(createEmailTemplatePage.createButton).wait(timeOut);
  await navigate.navigateToCreateEmailTemplate().wait(timeOut);
  await t
    .typeText(createEmailTemplatePage.modelName, modelName)
    .wait(timeOut)
    .typeText(createEmailTemplatePage.subjectMail, 'Test')
    .wait(timeOut)
    .typeText(createEmailTemplatePage.senderMail, 'noreply@tessi.fr')
    .wait(timeOut)
    .typeText(createEmailTemplatePage.senderName, 'Tessi Communication')
    .wait(timeOut)
    .typeText(
      createEmailTemplatePage.unsubscribeLink,
      'mailto:se.desabonner@tessi.fr'
    )
    .wait(timeOut)
    .click(createEmailTemplatePage.tapVariable)
    .wait(timeOut)
    .typeText(createEmailTemplatePage.variables0, 'Email')
    .click(createEmailTemplatePage.addVariable)
    .wait(timeOut)
    .typeText(createEmailTemplatePage.variables1, 'MOIS')
    .wait(timeOut)
    .click(createEmailTemplatePage.submitButton)
    .wait(timeOut);
});
