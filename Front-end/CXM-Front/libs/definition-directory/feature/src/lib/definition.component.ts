import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { directoryFeedTabNav } from '@cxm-smartflow/directory-feed/ui/feature-directory-feed-navigator';
import { DirectoryManagement } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-definition',
  templateUrl: './definition.component.html',
  styleUrls: ['./definition.component.scss'],
})
export class DefinitionComponent {
  directoryManagement: typeof DirectoryManagement = DirectoryManagement;
  DirectoryMgt = (key: DirectoryManagement) =>
    `${DirectoryManagement.CXM_DIRECTORY_MANAGEMENT}:${key}`;

  constructor(private router: Router) {}

  navigateTo() {
    this.router.navigateByUrl(directoryFeedTabNav.list.link);
  }
}
