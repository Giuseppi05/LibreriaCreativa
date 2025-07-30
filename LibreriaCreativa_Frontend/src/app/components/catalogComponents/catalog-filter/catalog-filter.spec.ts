import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogFilter } from './catalog-filter';

describe('CatalogFilter', () => {
  let component: CatalogFilter;
  let fixture: ComponentFixture<CatalogFilter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogFilter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CatalogFilter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
