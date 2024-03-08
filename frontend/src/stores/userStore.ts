import { defineStore } from 'pinia';
import type { CredentialsDto, LoginRequestDto, RegisterRequestDto } from '@/api/controllers/authController';
import authController from '@/api/controllers/authController';
import router from '@/router';

export const useUserStore = defineStore('user', {
  state: () => ({
    jwtToken: localStorage.getItem('jwtToken'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.jwtToken
  },
  actions: {
    async register(credentials: RegisterRequestDto) {
      try {
        const { data } = await authController.register(credentials);
        this.setUserInfo(data);
      } catch (err) {
        this.removeUserInfo();
        throw err;
      }
    },
    async login(credentials: LoginRequestDto) {
      try {
        const { data } = await authController.login(credentials);
        this.setUserInfo(data);
      } catch (err) {
        this.removeUserInfo();
        throw err;
      }
    },
    async logout() {
      this.removeUserInfo();
      await router.push('/auth');
    },
    setUserInfo(payload: CredentialsDto) {
      localStorage.setItem('jwtToken', payload.jwtToken);
      this.jwtToken = payload.jwtToken;
    },
    removeUserInfo() {
      localStorage.removeItem('jwtToken');
      this.jwtToken = '';
    }
  }
});
