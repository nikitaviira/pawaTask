import { defineStore } from 'pinia';
import type { AuthResponse, Credentials } from '@/api/controllers/authController';
import authController from '@/api/controllers/authController';

export const useUserStore = defineStore('user', {
  state: () => ({
    jwtToken: localStorage.getItem('jwtToken'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.jwtToken
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
      localStorage.setItem('jwtToken', payload.jwtToken);
      this.jwtToken = payload.jwtToken;
    },
    removeUserInfo() {
      localStorage.removeItem('jwtToken');
      this.jwtToken = '';
    }
  }
});
