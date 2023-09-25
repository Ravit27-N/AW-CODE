import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmDepositComponent } from './cxm-deposit.component';

describe('CxmDepositComponent', () => {
  let component: CxmDepositComponent;
  let fixture: ComponentFixture<CxmDepositComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CxmDepositComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmDepositComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
