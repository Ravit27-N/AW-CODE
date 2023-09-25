import { TestBed } from '@angular/core/testing';

import { DirectoryFeedSelectionServiceService } from './directory-feed-selection-service.service';

describe('DirectoryFeedSelectionServiceService', () => {
  let service: DirectoryFeedSelectionServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DirectoryFeedSelectionServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
