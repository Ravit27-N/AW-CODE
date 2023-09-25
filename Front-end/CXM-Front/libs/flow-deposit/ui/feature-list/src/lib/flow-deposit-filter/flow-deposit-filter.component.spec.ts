import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowDepositFilterComponent } from './flow-deposit-filter.component';

describe('FlowDepositFilterComponent', () => {
  let component: FlowDepositFilterComponent;
  let fixture: ComponentFixture<FlowDepositFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowDepositFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowDepositFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
