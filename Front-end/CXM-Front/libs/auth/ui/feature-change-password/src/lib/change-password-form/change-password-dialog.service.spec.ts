import { TestBed } from '@angular/core/testing';

import { ChangePasswordDialogService } from './change-password-dialog.service';

describe('ChangePasswordDialogService', () => {
  let service: ChangePasswordDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChangePasswordDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
