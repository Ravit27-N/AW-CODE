import * as packageInfo from '../../../../../../../package.json';

export interface CustomFooterModel {
  text: string;
  link: string;
  download?: boolean;
}

export const combineDynamicVersion = (footers?: CustomFooterModel[]) => {
  if (footers) {
    const { version } = packageInfo;
    const firstFooter = footers?.[0]?.text?.replace("$version$", version);
    footers[0] = {text: firstFooter, link: footers[0]?.link};
  }
  return footers;
}
