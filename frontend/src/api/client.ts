import type { AxiosInstance } from 'axios';
import Axios from 'axios';
import router from '@/router';
import { useUserStore } from '@/stores/userStore';
import { toast } from 'vue3-toastify';

const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL;

interface ErrorDto {
  message: string
  fields: Record<string, string>
}

export default (): AxiosInstance => {
  const store = useUserStore();
  const instance = Axios.create({
    baseURL: API_GATEWAY_URL + '/api/'
  });

  instance.defaults.headers.common['Access-Control-Allow-Origin'] = API_GATEWAY_URL;

  instance.interceptors.response.use((x) => x, async(error) => {
    if (error.response && error.response.status < 500) {
      const { status, data }: { status: number, data: ErrorDto } = error.response;
      switch (status) {
        case 401:
          store.logout();
          await router.push('/auth');
          break;
        case 400:
          toast.error(data.message);
          break;
      }
    } else {
      toast.error('Something went wrong!');
    }

    throw error;
  });

  instance.interceptors.request.use(function(config) {
    if (store.isLoggedIn) {
      config.headers.Authorization = `Bearer ${store.jwtToken}`;
    }
    return config;
  });

  return instance;
};
