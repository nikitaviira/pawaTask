import { defineStore } from 'pinia';
import type { AuthResponse, Credentials } from '@/api/controllers/authController';
import authController from '@/api/controllers/authController';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('jwt'),
    userName: localStorage.getItem('userName')
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
      localStorage.setItem('userName', payload.username);
      this.token = payload.jwtToken;
      this.userName = payload.username;
    },
    removeUserInfo() {
      localStorage.removeItem('jwt');
      localStorage.removeItem('userName');
      this.token = '';
      this.userName = '';
    }
  }
});
