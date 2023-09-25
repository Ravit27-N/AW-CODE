import { TestBed } from '@angular/core/testing';

import { CxmProfileService } from './cxm-profile.service';

describe('CxmProfileService', () => {
  let service: CxmProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmProfileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
