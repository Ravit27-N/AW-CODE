import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmFlowTraceabilityComponent } from './cxm-flow-traceability.component';

describe('CxmFlowTraceabilityComponent', () => {
  let component: CxmFlowTraceabilityComponent;
  let fixture: ComponentFixture<CxmFlowTraceabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CxmFlowTraceabilityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmFlowTraceabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
