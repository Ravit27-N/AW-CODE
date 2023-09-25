import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropdownFilterUserComponent } from './dropdown-filter-user.component';

describe('DropdownFilterUserComponent', () => {
  let component: DropdownFilterUserComponent;
  let fixture: ComponentFixture<DropdownFilterUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DropdownFilterUserComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DropdownFilterUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
