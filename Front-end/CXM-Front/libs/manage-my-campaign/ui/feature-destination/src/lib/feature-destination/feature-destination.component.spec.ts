import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureDestinationComponent } from './feature-destination.component';

describe('FeatureDestinationComponent', () => {
  let component: FeatureDestinationComponent;
  let fixture: ComponentFixture<FeatureDestinationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeatureDestinationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDestinationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
