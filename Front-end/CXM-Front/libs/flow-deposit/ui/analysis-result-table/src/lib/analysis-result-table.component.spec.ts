import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysisResultTableComponent } from './analysis-result-table.component';

describe('AnalysisResultTableComponent', () => {
  let component: AnalysisResultTableComponent;
  let fixture: ComponentFixture<AnalysisResultTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalysisResultTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalysisResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
