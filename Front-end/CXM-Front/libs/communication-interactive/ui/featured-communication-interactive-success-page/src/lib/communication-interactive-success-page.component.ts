import { Component } from '@angular/core';
import { AnimatedConfirmationPageOptions, AnimatedConfirmationType } from '@cxm-smartflow/shared/common-typo';
import { CommunicationInteractiveControlService } from '@cxm-smartflow/communication-interactive/data-access';

@Component({
  selector: 'cxm-smartflow-communication-interactive-success-page',
  templateUrl: './communication-interactive-success-page.component.html',
  styleUrls: ['./communication-interactive-success-page.component.scss']
})
export class CommunicationInteractiveSuccessPageComponent {
  animationConfig: AnimatedConfirmationPageOptions;

  constructor(private readonly controlService: CommunicationInteractiveControlService) {
    this.initialPage();
  }

  async initialPage(): Promise<void> {
    const data = await this.controlService.getSuccessMessage();
    this.animationConfig = {
      ...data,
      isShowSubContent: true
    };
  }

  validateAction($event: AnimatedConfirmationType) {
    if ($event === 'Main') {
      this.controlService.navigateToFlow();
    }

    if ($event === 'Alter') {
      this.controlService.navigateToChoosingModel();
    }
  }
}
