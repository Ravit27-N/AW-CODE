import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowMyCampaignComponent } from './follow-my-campaign.component';

describe('FollowMyCampaignComponent', () => {
  let component: FollowMyCampaignComponent;
  let fixture: ComponentFixture<FollowMyCampaignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FollowMyCampaignComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowMyCampaignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
