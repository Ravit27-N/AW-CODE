import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonDropdownFacultyComponent } from './button-dropdown-faculty.component';

describe('ButtonDropdownFacultyComponent', () => {
  let component: ButtonDropdownFacultyComponent;
  let fixture: ComponentFixture<ButtonDropdownFacultyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ButtonDropdownFacultyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonDropdownFacultyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
