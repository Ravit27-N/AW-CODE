import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListEnvelopeReferencesComponent } from './list-envelope-references.component';

describe('EnvelopeReferenceComponent', () => {
  let component: ListEnvelopeReferencesComponent;
  let fixture: ComponentFixture<ListEnvelopeReferencesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListEnvelopeReferencesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListEnvelopeReferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
