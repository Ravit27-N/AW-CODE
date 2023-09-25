import { TestBed } from '@angular/core/testing';

import { CxmTemplateService } from './cxm-template.service';

describe('CxmTemplateService', () => {
  let service: CxmTemplateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmTemplateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
