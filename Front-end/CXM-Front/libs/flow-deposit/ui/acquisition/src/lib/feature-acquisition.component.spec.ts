import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureAcquisitionComponent } from './feature-acquisition.component';

describe('FeatureAcquisitionComponent', () => {
  let component: FeatureAcquisitionComponent;
  let fixture: ComponentFixture<FeatureAcquisitionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeatureAcquisitionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureAcquisitionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
