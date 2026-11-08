import { FormControl, FormGroup } from '@angular/forms';
import {
  camposCoinciden,
  mensajeErrorEmail,
  mensajeErrorPassword,
  reglasEmail,
  reglasPasswordNueva,
} from './validadores';

describe('validadores', () => {
  it('acepta un email con dominio y extensión', () => {
    expect(new FormControl('ana@correo.com', reglasEmail).valid).toBeTrue();
  });

  it('rechaza emails sin extensión o con espacios', () => {
    expect(new FormControl('ana@correo', reglasEmail).hasError('emailInvalido')).toBeTrue();
    expect(new FormControl('ana @correo.com', reglasEmail).hasError('emailInvalido')).toBeTrue();
  });

  it('exige contraseña de al menos 6 caracteres con letra y número', () => {
    expect(new FormControl('abc12', reglasPasswordNueva).hasError('minlength')).toBeTrue();
    expect(new FormControl('abcdef', reglasPasswordNueva).hasError('passwordDebil')).toBeTrue();
    expect(new FormControl('123456', reglasPasswordNueva).hasError('passwordDebil')).toBeTrue();
    expect(new FormControl('abc123', reglasPasswordNueva).valid).toBeTrue();
  });

  it('marca el grupo cuando las contraseñas no coinciden', () => {
    const grupo = new FormGroup(
      { password: new FormControl('abc123'), confirmar: new FormControl('abc124') },
      { validators: camposCoinciden('password', 'confirmar') }
    );
    expect(grupo.hasError('noCoinciden')).toBeTrue();

    grupo.controls.confirmar.setValue('abc123');
    expect(grupo.hasError('noCoinciden')).toBeFalse();
  });

  it('solo muestra mensajes cuando el campo fue tocado', () => {
    const email = new FormControl('ana@correo', reglasEmail);
    expect(mensajeErrorEmail(email)).toBeNull();

    email.markAsTouched();
    expect(mensajeErrorEmail(email)).toBe('Ingresá un email válido (ej. nombre@correo.com).');

    const password = new FormControl('abcdef', reglasPasswordNueva);
    password.markAsTouched();
    expect(mensajeErrorPassword(password)).toBe('La contraseña debe tener al menos una letra y un número.');
  });
});
