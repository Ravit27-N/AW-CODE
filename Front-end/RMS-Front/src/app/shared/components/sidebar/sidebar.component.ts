import { FlatTreeControl } from '@angular/cdk/tree';
import { Component } from '@angular/core';
import {
  MatTreeFlatDataSource,
  MatTreeFlattener,
} from '@angular/material/tree';
import { AccessGuardService } from 'src/app/auth';
import { MenuNode, MENU_DATA } from '../../../menu.setting';
import { getAssetPrefix } from '../../utils/asset-util';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  assetPrefix = getAssetPrefix();

  constructor(private accessGuard: AccessGuardService) {
    this.dataSource.data = MENU_DATA;
  }

  private transformer = (node: MenuNode, level: number) => ({
    expandable: !!node.children && node.children.length > 0,
    name: node.name,
    icon: node.icon,
    level,
    link: node.link,
    access: !!node.perm ? this.accessGuard.check(node.perm) : true, // default true IF not set perm
  });

  // eslint-disable-next-line @typescript-eslint/member-ordering
  treeControl = new FlatTreeControl<SideMenuNode>(
    (node) => node.level,
    (node) => node.expandable,
  );

  // eslint-disable-next-line @typescript-eslint/member-ordering
  treeFlattern = new MatTreeFlattener(
    this.transformer,
    (node) => node.level,
    (node) => node.expandable,
    (node) => node.children,
  );

  // eslint-disable-next-line @typescript-eslint/member-ordering
  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattern);

  hasChild = (_: number, node: SideMenuNode) => node.expandable;
  canAccess = (_: number, node: SideMenuNode) => node.access;
}

export interface SideMenuNode {
  name: string;
  level: number;
  expandable: boolean;
  access: boolean;
}
