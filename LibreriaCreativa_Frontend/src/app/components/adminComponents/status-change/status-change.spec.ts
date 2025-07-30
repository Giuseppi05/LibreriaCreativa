import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusChange } from './status-change';

describe('StatusChange', () => {
  let component: StatusChange;
  let fixture: ComponentFixture<StatusChange>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusChange]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatusChange);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
