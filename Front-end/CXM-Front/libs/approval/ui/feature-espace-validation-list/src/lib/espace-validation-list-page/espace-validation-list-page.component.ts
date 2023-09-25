import { Component, HostListener } from '@angular/core';
import { ApprovalControlService } from '@cxm-smartflow/approval/data-access';

@Component({
  selector: 'cxm-smartflow-espace-validation-list-page',
  templateUrl: './espace-validation-list-page.component.html',
  styleUrls: ['./espace-validation-list-page.component.scss']
})
export class EspaceValidationListPageComponent {
  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    this.storageService.clearFilteringInStorage();
  }

  constructor(private readonly storageService: ApprovalControlService) {
  }
}
