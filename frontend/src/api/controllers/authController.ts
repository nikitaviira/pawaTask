import apiClient from '../client'
import type { AxiosResponse } from 'axios'

export interface Credentials {
  username: string;
  password: string;
}

export interface AuthResponse {
  jwtToken: string;
  username: string;
}

export default {
  login(credentials: Credentials): Promise<AxiosResponse<AuthResponse>> {
    return apiClient().post('/auth/login', credentials)
  },

  register(credentials: Credentials): Promise<AxiosResponse<AuthResponse>> {
    return apiClient().post('/auth/register', credentials)
  }
}
