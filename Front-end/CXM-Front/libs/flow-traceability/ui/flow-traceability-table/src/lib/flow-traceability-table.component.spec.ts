import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowTraceabilityTableComponent } from './flow-traceability-table.component';

describe('FlowTraceabilityTableComponent', () => {
  let component: FlowTraceabilityTableComponent;
  let fixture: ComponentFixture<FlowTraceabilityTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowTraceabilityTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowTraceabilityTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
