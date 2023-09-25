import { TestBed } from '@angular/core/testing';

import { ConfirmationMessageService } from './confirmation-message.service';

describe('ConfirmationMessageService', () => {
  let service: ConfirmationMessageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfirmationMessageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
