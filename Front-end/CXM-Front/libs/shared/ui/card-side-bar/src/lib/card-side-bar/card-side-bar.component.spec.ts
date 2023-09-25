import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardSideBarComponent } from './card-side-bar.component';

describe('CardSideBarComponent', () => {
  let component: CardSideBarComponent;
  let fixture: ComponentFixture<CardSideBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardSideBarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardSideBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
