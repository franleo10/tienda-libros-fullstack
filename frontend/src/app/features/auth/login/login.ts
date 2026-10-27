import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../../core/services/auth';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');
  protected router = inject(Router);
  protected route = inject(ActivatedRoute);

  formulario = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected alEnviar() {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.auth.login(this.formulario.value.email!, this.formulario.value.password!).subscribe({
      next: () => {
        this.cargando.set(false);

        const volverA = this.route.snapshot.queryParamMap.get('returnUrl');
        const destino = volverA && volverA.startsWith('/') && !volverA.startsWith('//') ? volverA : '/libros';
        this.router.navigateByUrl(destino);
      },
      error: (error) => {
        console.log('Login falló', error); this.cargando.set(false); this.mensajeError.set(
          // 403 = cuenta dada de baja por un administrador; el backend manda el motivo como texto
          error.status === 403 && typeof error.error === 'string'
            ? error.error
            : 'No se pudo iniciar sesión. Revisá tu email y contraseña.')
      },
    });
  }

}
