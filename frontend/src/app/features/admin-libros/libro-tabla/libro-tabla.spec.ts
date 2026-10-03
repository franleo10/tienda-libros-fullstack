import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibroTabla } from './libro-tabla';

describe('LibroTabla', () => {
  let component: LibroTabla;
  let fixture: ComponentFixture<LibroTabla>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroTabla]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibroTabla);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
