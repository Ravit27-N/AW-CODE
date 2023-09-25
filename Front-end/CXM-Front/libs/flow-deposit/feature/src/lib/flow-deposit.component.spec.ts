import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowDepositComponent } from './flow-deposit.component';

describe('FlowDepositComponent', () => {
  let component: FlowDepositComponent;
  let fixture: ComponentFixture<FlowDepositComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowDepositComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowDepositComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
