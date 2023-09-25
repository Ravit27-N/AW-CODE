import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileTabNavComponent } from './profile-tab-nav.component';

describe('ProfileTabNavComponent', () => {
  let component: ProfileTabNavComponent;
  let fixture: ComponentFixture<ProfileTabNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProfileTabNavComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileTabNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
