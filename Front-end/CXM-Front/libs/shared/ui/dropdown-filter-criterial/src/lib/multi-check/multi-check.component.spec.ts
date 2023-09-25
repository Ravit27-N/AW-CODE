import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiCheckComponent } from './multi-check.component';

describe('MultiCheckComponent', () => {
  let component: MultiCheckComponent;
  let fixture: ComponentFixture<MultiCheckComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiCheckComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
