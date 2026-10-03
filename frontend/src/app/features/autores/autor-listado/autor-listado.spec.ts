import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutorListado } from './autor-listado';

describe('AutorListado', () => {
  let component: AutorListado;
  let fixture: ComponentFixture<AutorListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutorListado]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AutorListado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
