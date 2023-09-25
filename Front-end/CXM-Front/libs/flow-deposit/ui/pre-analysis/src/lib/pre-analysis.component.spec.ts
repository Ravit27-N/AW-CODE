import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreAnalysisComponent } from './pre-analysis.component';

describe('PreAnalysisComponent', () => {
  let component: PreAnalysisComponent;
  let fixture: ComponentFixture<PreAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreAnalysisComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
