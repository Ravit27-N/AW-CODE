import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmCampaignComponent } from './cxm-campaign.component';

describe('CxmCampaignComponent', () => {
  let component: CxmCampaignComponent;
  let fixture: ComponentFixture<CxmCampaignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CxmCampaignComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmCampaignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
