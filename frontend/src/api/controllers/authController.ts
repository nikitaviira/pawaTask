import apiClient from '../client';
import type { AxiosResponse } from 'axios';

export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface RegisterRequestDto extends LoginRequestDto {
  userName: string;
}

export interface CredentialsDto {
  jwtToken: string;
}

export default {
  login(credentials: LoginRequestDto): Promise<AxiosResponse<CredentialsDto>> {
    return apiClient().post('/auth/login', credentials);
  },

  register(credentials: LoginRequestDto): Promise<AxiosResponse<CredentialsDto>> {
    return apiClient().post('/auth/register', credentials);
  }
};
