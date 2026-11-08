import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

// Mismos límites que el backend (RegisterRequest, CambiarPasswordRequest y las columnas de usuario)
export const EMAIL_MAX = 100;
export const PASSWORD_MIN = 6;
export const PASSWORD_MAX = 72;

// Validators.email acepta "a@b"; acá exigimos un dominio con extensión (ej. "@gmail.com")
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

export function emailValido(control: AbstractControl): ValidationErrors | null {
  const valor = control.value as string | null;
  if (!valor) {
    return null;
  }
  return EMAIL_REGEX.test(valor.trim()) ? null : { emailInvalido: true };
}

export function passwordSegura(control: AbstractControl): ValidationErrors | null {
  const valor = control.value as string | null;
  if (!valor) {
    return null;
  }
  return /[A-Za-zÁÉÍÓÚáéíóúÑñ]/.test(valor) && /\d/.test(valor) ? null : { passwordDebil: true };
}

// Validador de grupo: marca { noCoinciden } si los dos campos tienen valores distintos
export function camposCoinciden(campo: string, confirmacion: string): ValidatorFn {
  return (grupo: AbstractControl): ValidationErrors | null =>
    grupo.get(campo)?.value === grupo.get(confirmacion)?.value ? null : { noCoinciden: true };
}

export const reglasEmail: ValidatorFn[] = [
  Validators.required,
  Validators.maxLength(EMAIL_MAX),
  emailValido,
];

// Para crear o cambiar una contraseña. En el login solo se pide que no esté vacía,
// así las cuentas creadas antes de estas reglas pueden seguir entrando.
export const reglasPasswordNueva: ValidatorFn[] = [
  Validators.required,
  Validators.minLength(PASSWORD_MIN),
  Validators.maxLength(PASSWORD_MAX),
  passwordSegura,
];

export function mensajeErrorEmail(control: AbstractControl): string | null {
  if (!control.touched || control.valid) {
    return null;
  }
  if (control.hasError('required')) {
    return 'Ingresá tu email.';
  }
  if (control.hasError('maxlength')) {
    return `El email no puede superar los ${EMAIL_MAX} caracteres.`;
  }
  return 'Ingresá un email válido (ej. nombre@correo.com).';
}

export function mensajeErrorPassword(control: AbstractControl): string | null {
  if (!control.touched || control.valid) {
    return null;
  }
  if (control.hasError('required')) {
    return 'Ingresá una contraseña.';
  }
  if (control.hasError('minlength')) {
    return `La contraseña debe tener al menos ${PASSWORD_MIN} caracteres.`;
  }
  if (control.hasError('maxlength')) {
    return `La contraseña no puede superar los ${PASSWORD_MAX} caracteres.`;
  }
  return 'La contraseña debe tener al menos una letra y un número.';
}
