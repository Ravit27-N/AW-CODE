import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowTraceabilityFilterComponent } from './flow-traceability-filter.component';

describe('FlowTraceabilityFilterComponent', () => {
  let component: FlowTraceabilityFilterComponent;
  let fixture: ComponentFixture<FlowTraceabilityFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowTraceabilityFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowTraceabilityFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
