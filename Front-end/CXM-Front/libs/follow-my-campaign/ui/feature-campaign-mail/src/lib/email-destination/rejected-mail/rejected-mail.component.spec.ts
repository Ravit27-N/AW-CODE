import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RejectedMailComponent } from './rejected-mail.component';

describe('RejectedMailComponent', () => {
  let component: RejectedMailComponent;
  let fixture: ComponentFixture<RejectedMailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RejectedMailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RejectedMailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
