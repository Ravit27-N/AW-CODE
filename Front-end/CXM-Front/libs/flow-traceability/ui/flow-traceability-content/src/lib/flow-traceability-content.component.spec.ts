import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowTraceabilityContentComponent } from './flow-traceability-content.component';

describe('FlowTraceabilityContentComponent', () => {
  let component: FlowTraceabilityContentComponent;
  let fixture: ComponentFixture<FlowTraceabilityContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlowTraceabilityContentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlowTraceabilityContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
