import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowTraceabilityComponent } from './flow-traceability.component';

describe('FlowTraceabilityComponent', () => {
  let component: FlowTraceabilityComponent;
  let fixture: ComponentFixture<FlowTraceabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowTraceabilityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowTraceabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
