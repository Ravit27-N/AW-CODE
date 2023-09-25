import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateProfileFeatureComponent } from './update-profile-feature.component';

describe('UpdateProfileFeatureComponent', () => {
  let component: UpdateProfileFeatureComponent;
  let fixture: ComponentFixture<UpdateProfileFeatureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateProfileFeatureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateProfileFeatureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
