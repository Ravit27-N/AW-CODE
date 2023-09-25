import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentTraceabilityTableComponent } from './document-traceability-table.component';

describe('ShipmentTrackingTableComponent', () => {
  let component: DocumentTraceabilityTableComponent;
  let fixture: ComponentFixture<DocumentTraceabilityTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DocumentTraceabilityTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentTraceabilityTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
