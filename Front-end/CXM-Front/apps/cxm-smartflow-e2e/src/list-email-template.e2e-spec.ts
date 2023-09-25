import { waitForAngular } from 'testcafe-angular-selectors';

import { userCredentials } from './environment';
import EmailTemplatePage from './po/email-template.po';
import LoginPage from './po/login.po';
import { NavigatePage } from './po/navigate.po';
import { BASE_URL } from './utils/index';

const listEmailTemplatePage = new EmailTemplatePage();
const navigate = new NavigatePage();
const loginPage = new LoginPage();

// delay for testing action
const MOCK_DELAY = 2000;

fixture('cxm-smartflow testing')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
    // testing login page
    await t
      .typeText(loginPage.useranmeField, userCredentials.username)
      .wait(MOCK_DELAY)
      .typeText(loginPage.passwordField, userCredentials.password)
      .wait(MOCK_DELAY)
      .click(loginPage.loginButton)
      .wait(MOCK_DELAY);
  });

test('should click on next page of pagination', async (t) => {
  // click on the button next of pagination
  await t
    .click(listEmailTemplatePage.nextButton)
    .expect(listEmailTemplatePage.disabledButton.textContent)
    .eql('2')
    .wait(MOCK_DELAY);

  // expect testing with count card of list email template
  await t.expect(listEmailTemplatePage.countCardEmailTemplate.count).eql(12);

  // click to next page
  await t
    .click(listEmailTemplatePage.nextButton)
    .expect(listEmailTemplatePage.disabledButton.textContent)
    .eql('3')
    .wait(MOCK_DELAY);
});
