import { TestBed } from '@angular/core/testing';

import { CxmDirectoryService } from './cxm-directory.service';

describe('CxmDefinitionService', () => {
  let service: CxmDirectoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmDirectoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
