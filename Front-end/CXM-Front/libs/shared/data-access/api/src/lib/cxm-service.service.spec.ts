import { TestBed } from '@angular/core/testing';

import { CxmServiceService } from './cxm-service.service';

describe('CxmServiceService', () => {
  let service: CxmServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
