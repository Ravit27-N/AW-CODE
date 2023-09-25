import {
  LIST_EMAIL_TEMPLATE,
  PREVIOUS_URL,
} from '@cxm-smartflow/follow-my-campaign/data-access';

export interface TemplateCriteria {
  page: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
  templateType: string;
  filter: string;

  [property: string]: string | number | boolean | undefined | null;
}

export const templateFiltering = {
  shouldRestoreFilter: () => {
    const choiceOfModel = [
      '/cxm-template/design-model/sms-template-composition/sms',
      '/cxm-template/design-model/sms-template-composition/email',
    ];

    const localUrl = localStorage.getItem(PREVIOUS_URL);

    const previousURL = localUrl !== null ? JSON.parse(localUrl) : {};

    if (
      previousURL &&
      !choiceOfModel.some((e) =>
        previousURL[previousURL.length - 1]?.includes(e)
      )
    ) {
      localStorage.removeItem(LIST_EMAIL_TEMPLATE);
    }

    const filtering = localStorage.getItem(LIST_EMAIL_TEMPLATE);

    return filtering ? JSON.parse(filtering) : false;
  },

  rememberFilter: (filter: any) => {
    localStorage.setItem(LIST_EMAIL_TEMPLATE, JSON.stringify(filter));
  },
};
