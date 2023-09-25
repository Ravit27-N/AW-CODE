import { AppPage } from './po/app.po';
import { BASE_URL } from './utils/index';

const page = new AppPage();

fixture('cxm-campaign app')
  .page(BASE_URL)
  .beforeEach(async (t) => {});

test('compare title', async (t) => {
  await page.navigateTo();

  const paragraphText = await page.getParagraphText();

  await t.expect(paragraphText).contains('Suivre mes campagnes');
});
