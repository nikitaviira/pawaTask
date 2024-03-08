import apiClient from '../client';
import type { AxiosResponse } from 'axios';

export interface PasswordResetDto {
  email: string;
}

export interface NewPasswordDto {
  newPassword: string;
}

export default {
  initPasswordReset(body: PasswordResetDto): Promise<AxiosResponse<void>> {
    return apiClient().post('/auth/password/reset', body);
  },

  saveNewPassword(otp: string, body: NewPasswordDto): Promise<AxiosResponse<void>> {
    return apiClient().post(`/auth/password/reset/${otp}/save-new`, body);
  },

  verifyOtp(otp: string): Promise<AxiosResponse<void>> {
    return apiClient().get(`/auth/password/reset/${otp}`);
  }
};
