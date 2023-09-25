import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentOfFlowTraceabilityComponent } from './document-of-flow-traceability.component';

describe('DocumentOfFlowTraceabilityComponent', () => {
  let component: DocumentOfFlowTraceabilityComponent;
  let fixture: ComponentFixture<DocumentOfFlowTraceabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentOfFlowTraceabilityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentOfFlowTraceabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
