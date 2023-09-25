import { Component } from '@angular/core';
import { CommunicationInteractiveControlService } from '@cxm-smartflow/communication-interactive/data-access';


@Component({
  selector: 'cxm-smartflow-communication-interactive',
  templateUrl: './communication-interactive.component.html',
  styleUrls: ['./communication-interactive.component.scss']
})
export class CommunicationInteractiveComponent {

  constructor(private communicationService: CommunicationInteractiveControlService) { }

}
