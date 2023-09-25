import { TestBed } from '@angular/core/testing';

import { DefinitionDirectoryService } from './definition-directory.service';

describe('DefinitionService', () => {
  let service: DefinitionDirectoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DefinitionDirectoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
