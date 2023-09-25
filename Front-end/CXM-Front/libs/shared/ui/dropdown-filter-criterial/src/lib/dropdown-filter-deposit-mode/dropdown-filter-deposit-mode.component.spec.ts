import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropdownFilterDepositModeComponent } from './dropdown-filter-deposit-mode.component';

describe('DropdownFilterDepositModeComponent', () => {
  let component: DropdownFilterDepositModeComponent;
  let fixture: ComponentFixture<DropdownFilterDepositModeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DropdownFilterDepositModeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DropdownFilterDepositModeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
