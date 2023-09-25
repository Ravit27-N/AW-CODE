import { Selector } from 'testcafe';

export class AppPage {
  getParagraphText() {
    return Selector('h2').textContent;
  }
}
