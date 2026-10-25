import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { HistorialCompras } from './historial-compras';

const URL = 'http://localhost:8080/factura/usuario/pag/0';

const compra = (idFactura: number, titulos: string[], monto: number, fechaCompra: string) => ({
  idFactura,
  monto,
  fechaCompra,
  metodoPago: 'account_money',
  externalReference: 'carrito-1',
  idPago: 123,
  idUsuario: 7,
  titulosLibrosComprados: titulos,
});

describe('HistorialCompras', () => {
  let fixture: ComponentFixture<HistorialCompras>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialCompras],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(HistorialCompras);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  const texto = () => (fixture.nativeElement.textContent as string).replace(/\s+/g, ' ');

  it('muestra las compras con fecha, total, estado y enlace al detalle', () => {
    httpTesting.expectOne(URL).flush({
      content: [
        compra(9, ['Rayuela', 'Ficciones', 'Aleph', 'Sobre héroes y tumbas'], 4500, '2026-10-24T18:05:00'),
        compra(4, ['Rayuela'], 1500, '2026-10-01T10:00:00'),
      ],
      totalPages: 1,
      totalElements: 2,
      number: 0,
    });
    fixture.detectChanges();

    const tarjetas: HTMLAnchorElement[] = Array.from(fixture.nativeElement.querySelectorAll('a.compra'));
    expect(tarjetas.length).toBe(2);
    expect(tarjetas[0].getAttribute('href')).toBe('/compras/9');
    expect(tarjetas[0].textContent).toContain('24/10/2026 18:05');
    expect(tarjetas[0].textContent).toContain('Rayuela, Ficciones y 2 más');
    expect(tarjetas[0].textContent).toContain('4 libros');
    expect(tarjetas[0].textContent).toContain('Aprobada');
    expect(tarjetas[1].textContent).toContain('1 libro');
    expect(texto()).toMatch(/\$\s?4\.500,00/);
  });

  it('muestra un mensaje cuando todavía no hay compras', () => {
    httpTesting.expectOne(URL).flush({ content: [], totalPages: 0, totalElements: 0, number: 0 });
    fixture.detectChanges();

    expect(texto()).toContain('Todavía no hiciste ninguna compra');
    expect(fixture.nativeElement.querySelector('a[href="/libros"]')).not.toBeNull();
  });

  it('pide la página siguiente', () => {
    httpTesting.expectOne(URL).flush({
      content: [compra(9, ['Rayuela'], 1500, '2026-10-24T18:05:00')],
      totalPages: 2,
      totalElements: 11,
      number: 0,
    });
    fixture.detectChanges();

    const botones: HTMLButtonElement[] = Array.from(
      fixture.nativeElement.querySelectorAll('.paginacion button')
    );
    botones[1].click();

    httpTesting
      .expectOne('http://localhost:8080/factura/usuario/pag/1')
      .flush({ content: [], totalPages: 2, totalElements: 11, number: 1 });
  });
});
