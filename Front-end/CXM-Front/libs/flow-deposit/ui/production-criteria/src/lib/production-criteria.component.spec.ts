import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductionCriteriaComponent } from './production-criteria.component';

describe('ProductionCriteriaComponent', () => {
  let component: ProductionCriteriaComponent;
  let fixture: ComponentFixture<ProductionCriteriaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductionCriteriaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductionCriteriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
