import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonDropdownDepositModeComponent } from './button-dropdown-deposit-mode.component';

describe('ButtonDropdownDepositModeComponent', () => {
  let component: ButtonDropdownDepositModeComponent;
  let fixture: ComponentFixture<ButtonDropdownDepositModeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ButtonDropdownDepositModeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonDropdownDepositModeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
