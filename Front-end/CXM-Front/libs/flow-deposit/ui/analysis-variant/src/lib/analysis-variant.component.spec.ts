import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysisVariantComponent } from './analysis-variant.component';

describe('AnalysisVariantComponent', () => {
  let component: AnalysisVariantComponent;
  let fixture: ComponentFixture<AnalysisVariantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalysisVariantComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalysisVariantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
