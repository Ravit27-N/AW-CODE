import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfilePermissionFormComponent } from './user-profile-permission-form.component';

describe('UserProfilePermissionFormNewComponent', () => {
  let component: UserProfilePermissionFormComponent;
  let fixture: ComponentFixture<UserProfilePermissionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfilePermissionFormComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfilePermissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
