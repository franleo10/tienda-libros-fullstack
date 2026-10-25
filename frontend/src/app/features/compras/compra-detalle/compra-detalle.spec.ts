import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { CompraDetalle } from './compra-detalle';

describe('CompraDetalle', () => {
  let httpTesting: HttpTestingController;

  function crear(id: string) {
    TestBed.configureTestingModule({
      imports: [CompraDetalle],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id }) } } },
      ],
    });
    httpTesting = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CompraDetalle);
    fixture.detectChanges();
    return fixture;
  }

  const texto = (fixture: ReturnType<typeof crear>) =>
    (fixture.nativeElement.textContent as string).replace(/\s+/g, ' ');

  afterEach(() => httpTesting.verify());

  it('muestra los libros, el total y los datos de la factura', () => {
    const fixture = crear('9');
    httpTesting.expectOne('http://localhost:8080/factura/9').flush({
      idFactura: 9,
      monto: 3000,
      fechaCompra: '2026-10-24T18:05:00',
      metodoPago: 'account_money',
      externalReference: 'carrito-1',
      idPago: 987654,
      idUsuario: 7,
      titulosLibrosComprados: ['Rayuela', 'Ficciones'],
    });
    fixture.detectChanges();

    const items = Array.from(fixture.nativeElement.querySelectorAll('.items li')).map(
      (li) => (li as HTMLElement).textContent?.trim()
    );
    expect(items).toEqual(['Rayuela', 'Ficciones']);
    expect(texto(fixture)).toContain('Factura N.º 9');
    expect(texto(fixture)).toContain('24/10/2026 18:05');
    expect(texto(fixture)).toContain('Dinero en cuenta de MercadoPago');
    expect(texto(fixture)).toContain('987654');
    expect(texto(fixture)).toMatch(/\$\s?3\.000,00/);
  });

  it('descarga la factura con el diálogo de impresión', () => {
    const fixture = crear('9');
    httpTesting.expectOne('http://localhost:8080/factura/9').flush({
      idFactura: 9,
      monto: 3000,
      fechaCompra: '2026-10-24T18:05:00',
      metodoPago: 'account_money',
      externalReference: 'carrito-1',
      idPago: null,
      idUsuario: 7,
      titulosLibrosComprados: ['Rayuela'],
    });
    fixture.detectChanges();
    const imprimir = spyOn(window, 'print');

    fixture.nativeElement.querySelector('button.boton-principal').click();

    expect(imprimir).toHaveBeenCalled();
    expect(texto(fixture)).not.toContain('Operación MercadoPago');
  });

  it('avisa cuando la compra no existe o es de otra cuenta', () => {
    const fixture = crear('4');
    httpTesting
      .expectOne('http://localhost:8080/factura/4')
      .flush('No encontramos esa compra.', { status: 404, statusText: 'Not Found' });
    fixture.detectChanges();

    expect(texto(fixture)).toContain('No encontramos esa compra.');
    expect(fixture.nativeElement.querySelector('a[href="/compras"]')).not.toBeNull();
  });

  it('no llama al backend con un id inválido', () => {
    const fixture = crear('abc');

    httpTesting.expectNone(() => true);
    expect(texto(fixture)).toContain('No encontramos esa compra.');
  });
});
