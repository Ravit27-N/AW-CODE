import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepositTabNavComponent } from './deposit-tab-nav.component';

describe('DepositTabNavComponent', () => {
  let component: DepositTabNavComponent;
  let fixture: ComponentFixture<DepositTabNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DepositTabNavComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DepositTabNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
