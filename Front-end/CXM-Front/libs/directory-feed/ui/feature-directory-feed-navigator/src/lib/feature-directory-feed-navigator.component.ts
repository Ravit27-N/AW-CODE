import { Component, OnInit } from '@angular/core';
import { DirectoryNavTab } from '@cxm-smartflow/definition-directory/data-access';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-feature-directory-feed-navigator',
  templateUrl: './feature-directory-feed-navigator.component.html',
  styleUrls: ['./feature-directory-feed-navigator.component.scss'],
})
export class FeatureDirectoryFeedNavigatorComponent implements OnInit {
  directoryFeedNavTab: DirectoryNavTab[] = [];

  constructor(private router: Router
  ) {}

  ngOnInit(): void {
    this.directoryFeedNavTab = Object.values(directoryFeedTabNav);
  }

  isActiveTab(link: string) {
    return location.pathname.includes(link);
  }

  navigateTo(item: DirectoryNavTab) {
    this.router.navigateByUrl(item.link);
  }
}

export const directoryFeedTabNav: any = {
  list: {
    name: 'directory.feed.list.title',
    link: '/cxm-directory/directory-feed',
    active: false,
  },
  detail: {
    name: 'directory.feed.detail.title',
    link: '/cxm-directory/directory-feed/detail-directory-feed',
    active: true,
  }
};
