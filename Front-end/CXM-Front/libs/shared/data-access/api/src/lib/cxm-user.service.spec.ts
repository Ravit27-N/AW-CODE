import { TestBed } from '@angular/core/testing';

import { CxmUserService } from './cxm-user.service';

describe('CxmUserService', () => {
  let service: CxmUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmUserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
