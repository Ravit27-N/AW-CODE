import { TestBed } from '@angular/core/testing';

import { FollowMyCampaignResolverService } from './follow-my-campaign-resolver.service';

describe('FollowMyCampaignResolverService', () => {
  let service: FollowMyCampaignResolverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FollowMyCampaignResolverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
