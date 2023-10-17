import {FolderTemplateInterface} from '@/redux/slides/profile/template/templateSlide';

export const NestedArrayToArrayObj = ({Data}: {Data: any}) => {
  const total: any[] = [];
  Data.data.map((item: any) =>
    item.templates.map((temp: any) => total.push(temp)),
  );
  return total;
};

export function groupArrayTemplates(
  array: FolderTemplateInterface[],
): FolderTemplateInterface {
  const result: FolderTemplateInterface = {
    id: 0,
    unitName: '',
    businessUnitId: 0,
    countTemplates: 0,
    templates: [],
  };
  array.forEach((folder: FolderTemplateInterface) => {
    folder.templates.forEach(template => {
      result.templates.push({...template, unitName: folder.unitName});
    });
  });
  return result;
}
