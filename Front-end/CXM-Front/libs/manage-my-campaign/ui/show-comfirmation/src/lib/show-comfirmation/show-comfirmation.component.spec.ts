import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowComfirmationComponent } from './show-comfirmation.component';

describe('ShowComfirmationComponent', () => {
  let component: ShowComfirmationComponent;
  let fixture: ComponentFixture<ShowComfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowComfirmationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowComfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
