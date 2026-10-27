import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReseniaFormulario } from './resenia-formulario';

describe('ReseniaFormulario', () => {
  let component: ReseniaFormulario;
  let fixture: ComponentFixture<ReseniaFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReseniaFormulario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReseniaFormulario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
