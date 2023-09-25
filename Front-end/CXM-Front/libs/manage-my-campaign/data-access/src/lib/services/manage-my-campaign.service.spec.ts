import { TestBed } from '@angular/core/testing';

import { ManageMyCampaignService } from './manage-my-campaign.service';

describe('ManageMyCampaignService', () => {
  let service: ManageMyCampaignService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageMyCampaignService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

});
