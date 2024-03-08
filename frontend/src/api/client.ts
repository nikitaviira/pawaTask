import type { AxiosInstance } from 'axios';
import Axios from 'axios';
import { useUserStore } from '@/stores/userStore';
import { toast } from 'vue3-toastify';
import { useAlertStore } from '@/stores/alertStore';

const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL;

export interface ErrorDto {
  message: string
  fields: Record<string, string[]>
}

export default (): AxiosInstance => {
  const userStore = useUserStore();
  const alertStore = useAlertStore();
  const instance = Axios.create({
    baseURL: API_GATEWAY_URL + '/api/'
  });

  instance.defaults.headers.common['Access-Control-Allow-Origin'] = API_GATEWAY_URL;

  instance.interceptors.response.use((response) => response, async(error) => {
    if (error.response && error.response.status === 401) {
      alertStore.addAlert('Please login to proceed');
      return await userStore.logout();
    } else if (!error.response || error.response.status === 500) {
      toast.error('Something went wrong!');
    }
    throw error;
  });

  instance.interceptors.request.use(function(config) {
    if (userStore.isLoggedIn) {
      config.headers.Authorization = `Bearer ${userStore.jwtToken}`;
    }
    return config;
  });

  return instance;
};
