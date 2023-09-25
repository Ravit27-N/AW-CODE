import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CriteriaOptionsComponent } from './criteria-options.component';

describe('CriteriaOptionsComponent', () => {
  let component: CriteriaOptionsComponent;
  let fixture: ComponentFixture<CriteriaOptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CriteriaOptionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CriteriaOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
