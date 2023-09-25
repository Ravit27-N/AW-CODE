import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentTraceabilityComponent } from './document-traceability.component';

describe('ShipmentTrackingComponent', () => {
  let component: DocumentTraceabilityComponent;
  let fixture: ComponentFixture<DocumentTraceabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DocumentTraceabilityComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentTraceabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
