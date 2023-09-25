import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMyCampaignComponent } from './manage-my-campaign.component';

describe('ManageMyCampaignComponent', () => {
  let component: ManageMyCampaignComponent;
  let fixture: ComponentFixture<ManageMyCampaignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageMyCampaignComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageMyCampaignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
