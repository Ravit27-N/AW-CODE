import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgressionCircleComponent } from './progression-circle.component';

describe('ProgressionCircleComponent', () => {
  let component: ProgressionCircleComponent;
  let fixture: ComponentFixture<ProgressionCircleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProgressionCircleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgressionCircleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
