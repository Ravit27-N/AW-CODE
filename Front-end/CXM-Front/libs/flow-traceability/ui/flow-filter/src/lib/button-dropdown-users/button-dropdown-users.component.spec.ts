import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonDropdownUsersComponent } from './button-dropdown-users.component';

describe('ButtonDropdownUsersComponent', () => {
  let component: ButtonDropdownUsersComponent;
  let fixture: ComponentFixture<ButtonDropdownUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ButtonDropdownUsersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonDropdownUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
