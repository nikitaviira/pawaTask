import { helpers } from '@vuelidate/validators';
import type { Ref } from 'vue';

function passwordValidation(val: string): boolean {
  const hasDigit = /\d/.test(val);
  const hasUppercase = /[A-Z]/.test(val);
  const isMinimumLength = val.length >= 8;
  return hasDigit && hasUppercase && isMinimumLength;
}

function emailValidation(val: string): boolean {
  return /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(val);
}

export const repeatedPasswordValid = (password: Ref<string>) => helpers.withMessage('Password does not match', (value: string) => password.value === value);
export const passwordValid = helpers.withMessage('At least 8 chars long, one capital letter and one number', passwordValidation);
export const emailValid = helpers.withMessage('Incorrect email format', emailValidation);
