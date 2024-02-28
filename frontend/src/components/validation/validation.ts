export function passwordValidation(val: string): boolean {
  const hasDigit = /\d/.test(val);
  const hasUppercase = /[A-Z]/.test(val);
  const isMinimumLength = val.length >= 8;
  const isShorterThanMaxLength = val.length <= 40;
  return hasDigit && hasUppercase && isMinimumLength && isShorterThanMaxLength;
}

export function emailValidation(val: string): boolean {
  return /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(val);
}
