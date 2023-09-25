import { SideBarDataSource } from './cxm-templates/sideBar.model';

/**
 *
 * @param itemFoundLabel the label of the table is to show the items founded
 * @param total the total number of items
 * @returns
 */
export function totalItemsFound(itemFoundLabel: string, total: number): string {
  return itemFoundLabel.replace('${totalItems}', String(total ? total : 0));
}

export function getFileMessageLabel(message: string, name: string, size?: number): string {
  return message.replace('${file.name}', String(name)).replace('${file.size}', String(size ? size : 0));
}
/**
 *
 * @param sideBar list of sidebars
 * @param currentPath the current path of the component
 * @returns
 */
export function sideBarDataSource(
  sideBar: SideBarDataSource[],
  currentPath: string
) {
  return sideBar.map((item) => {
    item.link = item.link === currentPath ? '/' : item.link;
    return item;
  });
}

// export all functions
export const globalMethods = {
  totalItemsFound,
  sideBarDataSource,
  getFileMessageLabel
};
