import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MiBiblioteca } from './mi-biblioteca';

describe('MiBiblioteca', () => {
  let component: MiBiblioteca;
  let fixture: ComponentFixture<MiBiblioteca>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiBiblioteca]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MiBiblioteca);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
