import {IContentStatus} from '@/redux/slides/corporate-admin/corporateSettingSlide';

export const maxValue = <T extends Array<IContentStatus>>({
  data,
}: {
  data: T;
}) => {
  return data.find(
    i => i.value === Math.max(...data.map(o => Number(o.value))),
  )!;
};
