import { waitForAngular } from 'testcafe-angular-selectors';

import { userCredentials } from './environment';
import FollowMyCampaignPage from './po/follow-my-campaign.po';
import LoginPage from './po/login.po';
import { NavigatePage } from './po/navigate.po';
import { BASE_URL } from './utils/index';

const followMyCampaignPage = new FollowMyCampaignPage();
const navigate = new NavigatePage();
const loginPage = new LoginPage();

// path file CSV on your PC
const pathFileCsv = 'E:/CXM-resource/batchMail.csv';

fixture('cxm-smartflow testing')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
    // testing login page
    await t
      .setTestSpeed(0.1)
      .typeText(loginPage.useranmeField, userCredentials.username)
      .typeText(loginPage.passwordField, userCredentials.password)
      .click(loginPage.loginButton);
  });

test('should create campaign', async (t) => {
  await navigate.navigateToFollowCampaign();

  await t
    // click on the button create
    .click(followMyCampaignPage.btnCreate)
    // choice of model page
    .click(followMyCampaignPage.choiceCard)
    .click(followMyCampaignPage.choiceBtnNext)
    // setting parameters page
    .click(followMyCampaignPage.btnViewSummaryTemplate)
    .click(followMyCampaignPage.btnCloseDialog)
    .typeText(followMyCampaignPage.campaignName, 'Test Campaign Name')
    .typeText(followMyCampaignPage.subjectMail, ' {nom}')
    .click(followMyCampaignPage.settingParameterBtnNext)
    // destination page
    .setFilesToUpload(followMyCampaignPage.inputUpload, [pathFileCsv])
    .clearUpload(followMyCampaignPage.inputUpload)
    .expect(followMyCampaignPage.btnUpload.visible)
    .notOk()
    .click(followMyCampaignPage.destinationBtnNext)
    // summary page
    .click(followMyCampaignPage.btnValidate)
    .wait(3000);
});
