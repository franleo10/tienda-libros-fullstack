import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Libro } from '../../../services/libro';
import { Generos } from '../../../services/generos';
import { FiltrosCatalogo, LibroDato } from '../../../models/libro';
import { Portada } from '../../../shared/portada/portada';
import { PromedioPuntuacion } from '../../../shared/promedio-puntuacion/promedio-puntuacion';

@Component({
  selector: 'app-libro-listado',
  imports: [RouterLink, ReactiveFormsModule, Portada, PromedioPuntuacion],
  templateUrl: './libro-listado.html',
  styleUrl: './libro-listado.css',
})

export class LibroListado implements OnInit{

  ngOnInit(){
    this.cargarPagina(0);
    this.generoService.listar().subscribe({ next: (lista) => this.generos.set(lista) });
  }

  private libroService = inject(Libro);
  private readonly fb = inject(FormBuilder);
  private readonly generoService = inject(Generos);
  protected readonly libros = signal<LibroDato[]>([]);
  protected readonly generos = signal<{ idGenero: number; nombre: string }[]>([]);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');
  protected readonly paginaActual = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly totalLibros = signal(0);

  // Todo lo que se está buscando y filtrando en este momento
  protected readonly filtros = signal<FiltrosCatalogo>({
    texto: '',
    idGenero: null,
    precioMin: null,
    precioMax: null,
    orden: '',
  });
  protected readonly textoBuscado = computed(() => this.filtros().texto);
  protected readonly hayFiltros = computed(() => {
    const f = this.filtros();
    return f.idGenero !== null || f.precioMin !== null || f.precioMax !== null;
  });

  protected readonly formularioFiltros = this.fb.group({
    idGenero: [null as number | null],
    precioMin: [null as number | null],
    precioMax: [null as number | null],
    orden: [''],
  });

  private readonly formatoPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  protected cargarPagina(pagina: number) {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.libroService.buscarLibros(this.filtros(), pagina).subscribe({
      next: (resultado) => {
        this.libros.set(resultado.content);
        this.paginaActual.set(resultado.number);
        this.totalPaginas.set(resultado.totalPages);
        this.totalLibros.set(resultado.totalElements);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mensajeError.set('No se pudieron cargar los libros. Intentá de nuevo.');
      },
    });
  }

  protected buscar(texto: string) {
    this.filtros.update((f) => ({ ...f, texto: texto.trim() }));
    this.cargarPagina(0);
  }

  protected aplicarFiltros() {
    const valores = this.formularioFiltros.value;
    this.filtros.update((f) => ({
      ...f,
      idGenero: valores.idGenero ?? null,
      precioMin: valores.precioMin ?? null,
      precioMax: valores.precioMax ?? null,
      orden: valores.orden ?? '',
    }));
    this.cargarPagina(0);
  }

  protected quitarFiltros() {
    this.formularioFiltros.reset({ idGenero: null, precioMin: null, precioMax: null, orden: '' });
    this.aplicarFiltros();
  }

  protected formatearPrecio(precio: number) {
    return this.formatoPrecio.format(precio);
  }

  protected nombreAutor(libro: LibroDato) {
    const autor = libro.autores[0];
    return autor ? `${autor.nombre} ${autor.apellido}` : 'Autor desconocido';
  }
}