import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../../core/services/auth';
import { Router, RouterLink } from '@angular/router';
import {
  camposCoinciden,
  mensajeErrorEmail,
  mensajeErrorPassword,
  reglasEmail,
  reglasPasswordNueva,
} from '../../../shared/validadores';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');
  protected router = inject(Router);

  formulario = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(50)]],
    edad: [null as number | null, [Validators.required, Validators.min(1)]],
    email: ['', reglasEmail],
    password: ['', reglasPasswordNueva],
    confirmarPassword: ['', Validators.required]
  }, { validators: camposCoinciden('password', 'confirmarPassword') });

  protected readonly mensajeErrorEmail = mensajeErrorEmail;
  protected readonly mensajeErrorPassword = mensajeErrorPassword;

  protected alEnviar() {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.auth.register(this.formulario.value.nombre!, this.formulario.value.edad!, this.formulario.value.email!, this.formulario.value.password!).subscribe({
      next: (respuesta) => { this.cargando.set(false); this.router.navigateByUrl('/libros'); },
      error: (error) => { this.cargando.set(false); if ((error.status === 409 || error.status === 400) && typeof error.error === 'string') { this.mensajeError.set(error.error) } else { this.mensajeError.set('No se pudo registrar. Intentá de nuevo.') } },
    });


  }
}
