import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListUserTableComponent } from './list-user-table.component';

describe('ListUserTableComponent', () => {
  let component: ListUserTableComponent;
  let fixture: ComponentFixture<ListUserTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListUserTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListUserTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
