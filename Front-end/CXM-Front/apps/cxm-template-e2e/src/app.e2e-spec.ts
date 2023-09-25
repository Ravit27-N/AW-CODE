import { AppPage } from './po/app.po';
import { BASE_URL } from './utils/index';

const page = new AppPage();

fixture('cxm-template app')
  .page(BASE_URL)
  .beforeEach(async (t) => {});

test('should have title wtih p tag', async (t) => {
  const paragraphText = await page.getParagraphText();

  await t.expect(paragraphText).contains('Demo1 works!');
});
