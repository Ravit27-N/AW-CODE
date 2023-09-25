import { TestBed } from '@angular/core/testing';

import { FlowTraceabilityService } from './flow-traceability.service';

describe('FlowTraceabilityService', () => {
  let service: FlowTraceabilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlowTraceabilityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
