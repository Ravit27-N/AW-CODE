import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiSelectorCheckComponent } from './multi-selector-check.component';

describe('MultiSelectorCheckComponent', () => {
  let component: MultiSelectorCheckComponent;
  let fixture: ComponentFixture<MultiSelectorCheckComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiSelectorCheckComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiSelectorCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
