import { Component, inject, input, OnInit, signal } from '@angular/core';
import { Resenias } from '../../../services/resenias';
import { ReseniaDato } from '../../../models/resenia';
import { Estrellas } from '../../../shared/estrellas/estrellas';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-resenia-listado',
  imports: [Estrellas, DatePipe],
  templateUrl: './resenia-listado.html',
  styleUrl: './resenia-listado.css',
})
export class ReseniaListado implements OnInit {
  private reseniaService = inject(Resenias);

  idLibro = input.required<number>();

  protected readonly resenias = signal<ReseniaDato[]>([]);
  protected readonly pagina = signal<number>(0);
  protected readonly totalPaginas = signal<number>(0);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');


  ngOnInit(): void {
    this.cargar(0);
  }

  cargar(pagina: number) {
    this.cargando.set(true);
    this.mensajeError.set('');

    this.reseniaService.listarPorLibro(this.idLibro(), pagina).subscribe({
      next: (p) => {
        this.resenias.set(p.content);
        this.pagina.set(p.number);
        this.totalPaginas.set(p.totalPages);
        this.cargando.set(false);
      },
      error: (e) => {
        if (e.status === 404) {
          this.resenias.set([]);
          this.totalPaginas.set(0);
        } else {
          this.mensajeError.set('No se pudieron cargar las reseñas.');
        }
        this.cargando.set(false);
      },
    });
  }

  anterior() {
    this.cargar(this.pagina() - 1)
  }

  siguiente(){
    this.cargar(this.pagina() + 1)
  }
}
