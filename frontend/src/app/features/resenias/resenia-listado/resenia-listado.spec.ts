import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReseniaListado } from './resenia-listado';

describe('ReseniaListado', () => {
  let component: ReseniaListado;
  let fixture: ComponentFixture<ReseniaListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReseniaListado]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReseniaListado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
