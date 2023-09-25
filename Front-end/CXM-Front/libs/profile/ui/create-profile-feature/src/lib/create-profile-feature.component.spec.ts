import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateProfileFeatureComponent } from './create-profile-feature.component';

describe('CreateProfileFeatureComponent', () => {
  let component: CreateProfileFeatureComponent;
  let fixture: ComponentFixture<CreateProfileFeatureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateProfileFeatureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateProfileFeatureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
