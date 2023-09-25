import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonDropdownStatusComponent } from './button-dropdown-status.component';

describe('ButtonDropdownStatusComponent', () => {
  let component: ButtonDropdownStatusComponent;
  let fixture: ComponentFixture<ButtonDropdownStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ButtonDropdownStatusComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonDropdownStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
