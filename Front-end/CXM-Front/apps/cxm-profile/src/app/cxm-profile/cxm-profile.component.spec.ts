import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmProfileComponent } from './cxm-profile.component';

describe('CxmProfileComponent', () => {
  let component: CxmProfileComponent;
  let fixture: ComponentFixture<CxmProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CxmProfileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
