import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepositNavControlComponent } from './deposit-nav-control.component';

describe('DepositNavControlComponent', () => {
  let component: DepositNavControlComponent;
  let fixture: ComponentFixture<DepositNavControlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DepositNavControlComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DepositNavControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
