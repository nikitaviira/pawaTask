import apiClient from '../client';
import type { AxiosResponse } from 'axios';

export interface Credentials {
  email: string;
  password: string;
}

export interface RegistrationCredentials extends Credentials {
  repeatedPassword: string;
}

export interface AuthResponse {
  jwtToken: string;
}

export default {
  login(credentials: Credentials): Promise<AxiosResponse<AuthResponse>> {
    return apiClient().post('/auth/login', credentials);
  },

  register(credentials: Credentials): Promise<AxiosResponse<AuthResponse>> {
    return apiClient().post('/auth/register', credentials);
  }
};
