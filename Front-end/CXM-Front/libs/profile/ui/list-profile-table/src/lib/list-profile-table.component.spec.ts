import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListProfileTableComponent } from './list-profile-table.component';

describe('ListProfileTableComponent', () => {
  let component: ListProfileTableComponent;
  let fixture: ComponentFixture<ListProfileTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListProfileTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListProfileTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
