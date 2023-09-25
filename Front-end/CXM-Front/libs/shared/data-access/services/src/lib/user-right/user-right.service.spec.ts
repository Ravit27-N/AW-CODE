import { TestBed } from '@angular/core/testing';

import { UserRightService } from './user-right.service';

describe('UserRightService', () => {
  let service: UserRightService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserRightService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
