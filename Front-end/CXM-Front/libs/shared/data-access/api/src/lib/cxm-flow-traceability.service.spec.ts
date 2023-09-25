import { TestBed } from '@angular/core/testing';

import { CxmFlowTraceabilityService } from './cxm-flow-traceability.service';

describe('CxmFlowTrackingService', () => {
  let service: CxmFlowTraceabilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmFlowTraceabilityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
