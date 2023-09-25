import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnvelopeReferenceComponent } from './envelope-reference.component';

describe('EnvelopeReferenceComponent', () => {
  let component: EnvelopeReferenceComponent;
  let fixture: ComponentFixture<EnvelopeReferenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EnvelopeReferenceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EnvelopeReferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
