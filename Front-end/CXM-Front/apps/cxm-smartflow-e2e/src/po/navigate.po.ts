import { browser } from '../utils';
import { appRoute } from '../../../../libs/shared/data-access/model/src';

export class NavigatePage {
  navigateToLogin() {
    return browser.goTo('/login');
  }

  navigateToFollowCampaign() {
    return browser.goTo(appRoute.cxmCampaign.followMyCampaign.listEmailCampaign);
  }

  navigateToCreateEmailTemplate() {
    return browser.goTo(
      '/cxm-template/design-model/feature-create-email-template'
    );
  }

  navigateToListEmailTemplate() {
    return browser.goTo(appRoute.cxmTemplate.template.listEmailTemplate);
  }
}
