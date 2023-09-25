import { TestBed } from '@angular/core/testing';

import { CxmCampaignService } from './cxm-campaign.service';

describe('CxmCampaignService', () => {
  let service: CxmCampaignService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CxmCampaignService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
