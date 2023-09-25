import { waitForAngular } from 'testcafe-angular-selectors';
import DemoPage from './po/demo.po';
import { BASE_URL } from './utils/index';

const demoPage = new DemoPage();

fixture('cxm-template app')
  .page(BASE_URL)
  .beforeEach(async (t) => {
    await waitForAngular();
  });

test('Should typing in text box', async (t) => {
  await t
    .typeText(demoPage.fullname, 'cxm-smartflow')
    .click(demoPage.btnSubmit);
});
