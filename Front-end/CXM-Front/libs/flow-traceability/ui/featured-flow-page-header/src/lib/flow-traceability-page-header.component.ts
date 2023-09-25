import { Component, Input } from '@angular/core';

export declare type FlowTraceabilityPageHeaderType =
  | 'listOfFlow'
  | 'listOfDocument'
  | 'postalFlowHistory'
  | 'digitalFlowHistory'
  | 'digitalSMSFlowHistory'
  | 'postalDocumentDetail'
  | 'digitalDocumentDetail';

@Component({
  selector: 'cxm-smartflow-flow-traceability-page-header',
  templateUrl: './flow-traceability-page-header.component.html',
  styleUrls: ['./flow-traceability-page-header.component.scss'],
})
export class FlowTraceabilityPageHeaderComponent {
  showFileName: FlowTraceabilityPageHeaderType[] = [
    'listOfFlow',
    'listOfDocument',
  ];
  @Input() fileName = '';
  @Input() type: FlowTraceabilityPageHeaderType = 'listOfFlow';
}
