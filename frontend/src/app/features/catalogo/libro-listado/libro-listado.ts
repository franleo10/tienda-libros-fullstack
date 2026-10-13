import { Component, inject, OnInit, signal } from '@angular/core';
import { Libro, LibroDato } from '../../../core/services/libro';

@Component({
  selector: 'app-libro-listado',
  imports: [],
  templateUrl: './libro-listado.html',
  styleUrl: './libro-listado.css',
})

export class LibroListado implements OnInit{
  
  ngOnInit(){
    this.libroService.obtenerLibros(1).subscribe({
      next: (pagina) => {this.libros.set(pagina.content); this.cargando.set(false)},
      error: (error) => {this.cargando.set(false); console.log('Error al traer libros', error)},
    });
  }

  private libroService = inject(Libro);
  protected readonly libros = signal<LibroDato[]>([]);
  protected readonly cargando = signal(true);


}
