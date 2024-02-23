import { defineStore } from 'pinia';
import type { AuthResponse, Credentials } from '@/api/controllers/authController';
import authController from '@/api/controllers/authController';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('jwt'),
    email: localStorage.getItem('email')
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async register(credentials: Credentials) {
      try {
        const { data } = await authController.register(credentials);
        this.setUserInfo(data);
      } catch (err) {
        this.removeUserInfo();
        throw err;
      }
    },
    async login(credentials: Credentials) {
      try {
        const { data } = await authController.login(credentials);
        this.setUserInfo(data);
      } catch (err) {
        this.removeUserInfo();
        throw err;
      }
    },
    logout() {
      this.removeUserInfo();
    },
    setUserInfo(payload: AuthResponse) {
      localStorage.setItem('jwt', payload.jwtToken);
      localStorage.setItem('email', payload.email);
      this.token = payload.jwtToken;
      this.email = payload.email;
    },
    removeUserInfo() {
      localStorage.removeItem('jwt');
      localStorage.removeItem('email');
      this.token = '';
      this.email = '';
    }
  }
});
