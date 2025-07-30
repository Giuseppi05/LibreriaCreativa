import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminMessages } from './admin-messages';

describe('AdminMessages', () => {
  let component: AdminMessages;
  let fixture: ComponentFixture<AdminMessages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminMessages]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminMessages);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
