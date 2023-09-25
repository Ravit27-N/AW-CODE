import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureSettingComponent } from './feature-setting.component';

describe('FeatureSettingComponent', () => {
  let component: FeatureSettingComponent;
  let fixture: ComponentFixture<FeatureSettingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeatureSettingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureSettingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
