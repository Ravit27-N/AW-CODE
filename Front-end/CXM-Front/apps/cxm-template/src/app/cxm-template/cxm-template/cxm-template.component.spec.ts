import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmTemplateComponent } from './cxm-template.component';

describe('CxmTemplateComponent', () => {
  let component: CxmTemplateComponent;
  let fixture: ComponentFixture<CxmTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CxmTemplateComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should render the route outlet`, () => {
    expect(fixture.nativeElement.querySelector('router-outlet')).toBeTruthy();
  });
});
