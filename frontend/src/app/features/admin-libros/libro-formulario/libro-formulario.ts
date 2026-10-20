import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Libro } from '../../../services/libro';
import { Autor } from '../../../services/autor';
import { Generos } from '../../../services/generos';
import { ActivatedRoute, Router } from '@angular/router';
import { AutorResumen } from '../../../models/autor';
import { LibroDato, LibroGuardado } from '../../../models/libro';

@Component({
  selector: 'app-libro-formulario',
  imports: [ReactiveFormsModule],
  templateUrl: './libro-formulario.html',
  styleUrl: './libro-formulario.css',
})
export class LibroFormulario implements OnInit {
  private fb = inject(FormBuilder);
  private libroService = inject(Libro);
  private autorService = inject(Autor);
  private generoService = inject(Generos);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected readonly autores = signal<AutorResumen[]>([]);
  protected readonly generos = signal<{ idGenero: number, nombre: string }[]>([]);
  protected readonly idLibro = signal<number | null>(null);
  protected readonly mensajeError = signal<string>('');
  protected readonly libroActual = signal<LibroDato | null>(null);

  formulario = this.fb.group({
    titulo: ['', [Validators.required, Validators.maxLength(100)]],
    sinopsis: ['', Validators.required],
    precio: [null as number | null, [Validators.required, Validators.min(0)]],
    fecha_lanzamiento: ['', Validators.required],
    idAutor: [null as number | null, Validators.required],
    idGenero: [null as number | null, Validators.required]
  });

  ngOnInit(): void {
    this.autorService.listar(0).subscribe({
      next: (pagina) => { this.autores.set(pagina.content) },
      error: (error) => { this.mensajeError.set('No se pudieron cargar los autores') }
    });

    this.generoService.listar().subscribe({
      next: (lista) => { this.generos.set(lista) },
      error: (error) => { this.mensajeError.set('No se pudieron cargar los géneros') }
    });

    const id = this.route.snapshot.paramMap.get('id');   

    if (id) {
      this.idLibro.set(Number(id));                      
      this.libroService.obtenerLibro(Number(id)).subscribe({
        next: (libro) => {
          this.libroActual.set(libro);
          this.formulario.controls.idAutor.disable();
          this.formulario.controls.idGenero.disable();
          this.formulario.patchValue({
            titulo: libro.titulo,
            sinopsis: libro.sinopsis,
            precio: libro.precio,
            fecha_lanzamiento: libro.fecha_lanzamiento
            
          });
        },
        error: (error) => { this.mensajeError.set('No se pudo cargar el libro') }
      });
    }


  }
  guardar() {
    if (this.formulario.invalid) return;
    const valores = this.formulario.value;
    const libro: LibroGuardado = {
      titulo: valores.titulo!,
      sinopsis: valores.sinopsis!,
      precio: valores.precio!,
      fecha_lanzamiento: valores.fecha_lanzamiento!
    };

    const id = this.idLibro();
    const pedido = id
      ? this.libroService.modificar(id, libro)
      : this.libroService.crear(valores.idAutor!, valores.idGenero!, libro);

    pedido.subscribe({
      next: () => { this.router.navigate(['/admin/libros']); },
      error: () => { this.mensajeError.set('No se pudo guardar el libro'); }
    });

  }



}
